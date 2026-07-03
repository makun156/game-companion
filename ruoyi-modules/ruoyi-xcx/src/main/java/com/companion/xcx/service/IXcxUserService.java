package com.companion.xcx.service;

import com.business.domain.bo.UserBo;
import com.business.domain.vo.UserVo;

/**
 * 小程序用户Service接口
 *
 * @author system
 */
public interface IXcxUserService {

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserVo getUserInfo();

    /**
     * 修改当前登录用户信息
     *
     * @param bo 用户信息
     * @return 是否修改成功
     */
    Boolean updateUserInfo(UserBo bo);
}
