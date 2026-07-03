package com.companion.xcx.service.impl;

import com.business.domain.User;
import com.business.domain.bo.UserBo;
import com.business.domain.vo.UserVo;
import com.business.mapper.UserMapper;
import com.companion.xcx.service.IXcxUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;

/**
 * 小程序用户Service业务层处理
 *
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxUserServiceImpl implements IXcxUserService {

    private final UserMapper userMapper;

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @Override
    public UserVo getUserInfo() {
        Long userId = LoginHelper.getUserId();
        UserVo userVo = userMapper.selectVoById(userId);
        if (userVo == null) {
            throw new ServiceException("用户信息不存在");
        }
        return userVo;
    }

    /**
     * 修改当前登录用户信息
     *
     * @param bo 用户信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateUserInfo(UserBo bo) {
        Long userId = LoginHelper.getUserId();
        bo.setId(userId);
        User update = MapstructUtils.convert(bo, User.class);
        return userMapper.updateById(update) > 0;
    }
}
