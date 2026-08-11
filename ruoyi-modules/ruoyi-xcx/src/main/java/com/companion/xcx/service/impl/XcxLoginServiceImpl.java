package com.companion.xcx.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.GameCompanionUser;
import com.business.domain.User;
import com.business.domain.bo.UserBo;
import com.business.domain.vo.UserVo;
import com.business.domain.vo.XcxLoginVo;
import com.business.mapper.GameCompanionUserMapper;
import com.business.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.domain.model.XcxLoginUser;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import com.business.enums.LoginUserType;
import org.dromara.common.satoken.utils.LoginHelper;
import com.companion.xcx.config.WechatMiniappProperties;
import com.companion.xcx.service.IXcxLoginService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 小程序登录Service业务层处理
 *
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxLoginServiceImpl implements IXcxLoginService {

    private final UserMapper userMapper;
    private final WechatMiniappProperties wechatMiniappProperties;
    private final GameCompanionUserMapper companionUserMapper;

    /**
     * 微信小程序一键登录
     *
     * @param xcxCode 小程序code
     * @return 登录信息
     */
    @Override
    public XcxLoginVo login(String xcxCode, String wxCode) {
        // 获取小程序配置
        String appid = wechatMiniappProperties.getAppid();
        String secret = wechatMiniappProperties.getSecret();
        if (StrUtil.isBlank(appid) || StrUtil.isBlank(secret)) {
            throw new ServiceException("未配置小程序 appid 或 secret，请在 yml 中配置 wechat.miniapp.configs");
        }
        // 获取微信access_token
        HttpResponse getTokenResponse = HttpUtil.createGet("https://api.weixin.qq.com/cgi-bin/token")
            .form("appid", appid)
            .form("secret", secret)
            .form("grant_type", "client_credential")
            .execute();
        if (!getTokenResponse.isOk()) {
            throw new ServiceException("获取小程序 access_token 失败,请联系管理员");
        }
        String accessTokenStr = getTokenResponse.body();
        Map<String, Object> accessTokenMap = JSONUtil.toBean(accessTokenStr, Map.class);
        Object accessToken = accessTokenMap.get("access_token");

        // 获取微信用户信息
        HttpResponse getWxLoginInfo = HttpUtil.createPost("https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken)
            .body(JSONUtil.toJsonStr(Map.of("code", xcxCode)))
            .execute();
        if (!getWxLoginInfo.isOk()) {
            throw new ServiceException("获取小程序用户信息失败，请重试");
        }
        String wxLoginInfoStr = getWxLoginInfo.body();
        Map<String, Object> wxLoginInfoMap = JSONUtil.toBean(wxLoginInfoStr, Map.class);

        // 校验微信接口返回状态
        Integer errcode = (Integer) wxLoginInfoMap.get("errcode");
        if (errcode == null || errcode != 0) {
            throw new ServiceException("获取手机号失败，微信返回错误码: " + wxLoginInfoMap.get("errcode") + "，错误信息: " + wxLoginInfoMap.get("errmsg"));
        }

        // 提取手机号
        Map<String, Object> phoneInfo = (Map<String, Object>) wxLoginInfoMap.get("phone_info");
        if (phoneInfo == null) {
            throw new ServiceException("微信返回数据中未包含 phone_info，无法获取手机号");
        }
        String phoneNumber = (String) phoneInfo.get("phoneNumber");
        if (StrUtil.isBlank(phoneNumber)) {
            throw new ServiceException("微信返回的手机号为空");
        }

        // 通过手机号查找或创建用户
        Object obj = loadUserByPhoneNumber(phoneNumber);
        bindOpenid(obj, wxCode);

        // 构建小程序登录用户
        XcxLoginUser loginUser = new XcxLoginUser();
        String userType;
        if (obj instanceof GameCompanionUser companionUser) {
            loginUser.setUserId(companionUser.getId());
            loginUser.setUsername(companionUser.getName());
            loginUser.setNickname(companionUser.getNickName());
            loginUser.setUserType(UserType.COMPANION_USER.getUserType());
            userType = LoginUserType.COMPANION.getCode();
        } else {
            UserVo user = (UserVo) obj;
            loginUser.setUserId(user.getId());
            loginUser.setUsername(user.getUserName());
            loginUser.setNickname(user.getNickName());
            loginUser.setUserType(UserType.XCX_USER.getUserType());
            userType = LoginUserType.USER.getCode();
        }
        loginUser.setClientKey("xcx");

        // 生成token
        LoginHelper.login(loginUser, new SaLoginParameter());

        XcxLoginVo loginVo = new XcxLoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setLoginUserType(userType);
        return loginVo;
    }

    /**
     * Bind wx.login() openid to the current user so pay orders can reuse it.
     */
    private void bindOpenid(Object user, String wxCode) {
        if (StrUtil.isBlank(wxCode)) {
            return;
        }
        String openid = getOpenidByCode(wxCode);
        if (user instanceof GameCompanionUser companionUser) {
            GameCompanionUser update = new GameCompanionUser();
            update.setId(companionUser.getId());
            update.setOpenid(openid);
            companionUserMapper.updateById(update);
        } else {
            UserVo userVo = (UserVo) user;
            User update = new User();
            update.setId(userVo.getId());
            update.setOpenid(openid);
            userMapper.updateById(update);
        }
    }

    private String getOpenidByCode(String code) {
        HttpResponse response = HttpUtil.createGet("https://api.weixin.qq.com/sns/jscode2session")
            .form("appid", wechatMiniappProperties.getAppid())
            .form("secret", wechatMiniappProperties.getSecret())
            .form("js_code", code)
            .form("grant_type", "authorization_code")
            .execute();
        if (!response.isOk()) {
            throw new ServiceException("获取微信openid失败");
        }
        Map<String, Object> result = JSONUtil.toBean(response.body(), Map.class);
        String openid = (String) result.get("openid");
        if (StrUtil.isBlank(openid)) {
            log.error("微信登录code2session接口调用失败，response={}", response.body());
            throw new ServiceException("获取微信openid失败");
        }
        return openid;
    }

    /**
     * 通过手机号查找用户，如果不存在则自动注册
     *
     * @param phoneNumber 微信手机号快速组件返回的手机号
     * @return 用户信息
     */
    private Object loadUserByPhoneNumber(String phoneNumber) {
        LambdaQueryWrapper<GameCompanionUser> companionUserWrapper = Wrappers.lambdaQuery(GameCompanionUser.class);
        companionUserWrapper.eq(GameCompanionUser::getPhone, phoneNumber);
        GameCompanionUser gameCompanionUser = companionUserMapper.selectOne(companionUserWrapper);
        if (gameCompanionUser != null) {
            return gameCompanionUser;
        }
        // 1. 通过手机号查询用户
        UserVo user = selectUserByPhonenumber(phoneNumber);
        if (ObjectUtil.isNotNull(user)) {
            return user;
        }

        // 2. 新用户 → 自动注册
        UserBo userBo = new UserBo();
        // 生成唯一用户名：wx_ + 手机号后8位
        userBo.setUserName(phoneNumber);
        userBo.setNickName("微信用户" + RandomUtil.randomNumbers(6));
        userBo.setPhonenumber(phoneNumber);
        userBo.setSex("2"); // 2 未知
        userBo.setStatus(SystemConstants.NORMAL);

        boolean success = insertUser(userBo);
        if (!success) {
            throw new ServiceException("小程序用户注册失败");
        }
        return selectUserById(userBo.getId());
    }

    /**
     * 通过手机号查询用户
     *
     * @param phonenumber 手机号
     * @return 用户信息
     */
    private UserVo selectUserByPhonenumber(String phonenumber) {
        LambdaQueryWrapper<User> lqw = Wrappers.lambdaQuery();
        lqw.eq(User::getPhonenumber, phonenumber);
        return userMapper.selectVoOne(lqw);
    }

    /**
     * 通过ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    private UserVo selectUserById(Long id) {
        return userMapper.selectVoById(id);
    }

    /**
     * 新增用户
     *
     * @param bo 用户
     * @return 是否新增成功
     */
    private boolean insertUser(UserBo bo) {
        User add = MapstructUtils.convert(bo, User.class);
        boolean flag = userMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @Override
    public UserVo getInfo() {
        Long userId = LoginHelper.getUserId();
        UserVo userVo = selectUserById(userId);
        if (userVo == null) {
            throw new ServiceException("用户信息不存在");
        }
        return userVo;
    }

    /**
     * 退出登录
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }
}
