package com.companion.xcx.service;

import com.business.domain.vo.UserVo;
import com.business.domain.vo.XcxLoginVo;

/**
 * 小程序登录Service接口
 *
 * @author system
 */
public interface IXcxLoginService {

    /**
     * 微信小程序一键登录
     *
     * @param phoneCode 手机号code
     * @return 登录信息
     */
    XcxLoginVo login(String phoneCode, String loginCode);

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    UserVo getInfo();

    /**
     * 退出登录
     */
    void logout();
}
