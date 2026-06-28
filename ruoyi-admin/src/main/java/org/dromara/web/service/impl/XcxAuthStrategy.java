package org.dromara.web.service.impl;

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
import com.business.domain.bo.UserBo;
import com.business.domain.vo.UserVo;
import com.business.mapper.GameCompanionUserMapper;
import com.business.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.core.domain.model.XcxLoginBody;
import org.dromara.common.core.domain.model.XcxLoginUser;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.domain.vo.SysClientVo;
import org.dromara.web.config.properties.WechatMiniappProperties;
import org.dromara.web.domain.vo.LoginVo;
import org.dromara.web.service.IAuthStrategy;
import org.dromara.web.service.SysLoginService;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 小程序认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("xcx" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class XcxAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;
    private final IUserService userService;
    private final WechatMiniappProperties wechatMiniappProperties;
    private final GameCompanionUserMapper companionUserMapper;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        XcxLoginBody loginBody = JsonUtils.parseObject(body, XcxLoginBody.class);
        ValidatorUtils.validate(loginBody);
        // xcxCode 为小程序调用 wx.login 授权后获取
        String xcxCode = loginBody.getXcxCode();
        // 多个小程序识别使用
        String appid = loginBody.getAppid();

        // 根据 appid 获取对应的 secret
        String secret = wechatMiniappProperties.getConfigs() != null ? wechatMiniappProperties.getConfigs().get(appid) : null;
        if (StrUtil.isBlank(secret)) {
            throw new ServiceException("未配置小程序 appid[" + appid + "] 对应的 secret，请在 yml 中 wechat.miniapp.configs 下配置");
        }
        //获取微信access_token
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

        //获取微信用户信息
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

        // 构建小程序登录用户
        XcxLoginUser loginUser = new XcxLoginUser();
        if (obj instanceof GameCompanionUser companionUser) {
            loginUser.setUserId(companionUser.getId());
            loginUser.setUsername(companionUser.getName());
            loginUser.setNickname(companionUser.getNickName());
            loginUser.setUserType(UserType.COMPANION_USER.getUserType());
        } else {
            UserVo user = (UserVo) obj;
            loginUser.setUserId(user.getId());
            loginUser.setUsername(user.getUserName());
            loginUser.setNickname(user.getNickName());
            loginUser.setUserType(UserType.XCX_USER.getUserType());
        }
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());

        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());
        // 生成token
        LoginHelper.login(loginUser, model);

        // 记录登录日志
        loginService.recordLogininfor(null, loginUser.getUsername(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        loginVo.setLoginUserType(obj instanceof GameCompanionUser ? UserType.COMPANION_USER.getUserType() : UserType.XCX_USER.getUserType());
        return loginVo;
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
        UserVo user = userService.selectUserByPhonenumber(phoneNumber);
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

        boolean success = userService.insertUser(userBo);
        if (!success) {
            throw new ServiceException("小程序用户注册失败");
        }
        return userService.selectUserById(userBo.getId());
    }

}
