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
     * @param xcxCode 小程序code
     * @return 登录信息
     */
    XcxLoginVo login(String xcxCode, String wxCode);

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
