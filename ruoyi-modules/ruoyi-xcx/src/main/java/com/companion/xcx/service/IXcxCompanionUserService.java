package com.companion.xcx.service;

import com.business.domain.vo.GameCompanionUserVo;

/**
 * 小程序陪玩用户Service接口
 *
 * @author system
 */
public interface IXcxCompanionUserService {

    /**
     * 获取当前登录陪玩信息
     *
     * @return 陪玩信息
     */
    GameCompanionUserVo getInfo();
}
