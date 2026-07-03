package com.companion.xcx.service.impl;

import com.business.domain.bo.GameCompanionUserBo;
import com.business.domain.vo.GameCompanionUserVo;
import com.business.service.IGameCompanionUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.satoken.utils.LoginHelper;
import com.companion.xcx.service.IXcxCompanionUserService;
import org.springframework.stereotype.Service;

/**
 * 小程序陪玩用户Service业务层处理
 *
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxCompanionUserServiceImpl implements IXcxCompanionUserService {

    private final IGameCompanionUserService gameCompanionUserService;

    /**
     * 获取当前登录陪玩信息
     *
     * @return 陪玩信息
     */
    @Override
    public GameCompanionUserVo getInfo() {
        Long userId = LoginHelper.getUserId();
        GameCompanionUserVo companionUserVo = gameCompanionUserService.queryById(userId);
        if (companionUserVo == null) {
            throw new ServiceException("陪玩信息不存在");
        }
        return companionUserVo;
    }

    /**
     * 修改当前登录陪玩信息
     *
     * @param bo 陪玩信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateInfo(GameCompanionUserBo bo) {
        Long userId = LoginHelper.getUserId();
        bo.setId(userId);
        return gameCompanionUserService.updateByBo(bo);
    }
}
