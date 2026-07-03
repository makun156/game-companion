package com.companion.xcx.service;

import com.business.domain.vo.GameLevelVo;
import com.business.domain.vo.GameVo;

import java.util.List;

/**
 * 小程序游戏Service接口
 *
 * @author system
 */
public interface IXcxGameService {

    /**
     * 获取所有游戏列表
     *
     * @return 游戏列表
     */
    List<GameVo> getGameList();

    /**
     * 根据游戏ID获取段位列表
     *
     * @param gameId 游戏ID
     * @return 段位列表
     */
    List<GameLevelVo> getLevelList(Long gameId);
}
