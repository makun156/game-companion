package com.companion.xcx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.Game;
import com.business.domain.GameLevel;
import com.business.domain.vo.GameLevelVo;
import com.business.domain.vo.GameVo;
import com.business.mapper.GameLevelMapper;
import com.business.mapper.GamesMapper;
import com.companion.xcx.service.IXcxGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 小程序游戏Service业务层处理
 *
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxGameServiceImpl implements IXcxGameService {

    private final GamesMapper gamesMapper;
    private final GameLevelMapper gameLevelMapper;

    /**
     * 获取所有游戏列表
     *
     * @return 游戏列表
     */
    @Override
    public List<GameVo> getGameList() {
        LambdaQueryWrapper<Game> wrapper = Wrappers.lambdaQuery(Game.class);
        wrapper.eq(Game::getStatus, 0);
        wrapper.orderByAsc(Game::getSort);
        return gamesMapper.selectVoList(wrapper);
    }

    /**
     * 根据游戏ID获取段位列表
     *
     * @param gameId 游戏ID
     * @return 段位列表
     */
    @Override
    public List<GameLevelVo> getLevelList(Long gameId) {
        LambdaQueryWrapper<GameLevel> wrapper = Wrappers.lambdaQuery(GameLevel.class);
        wrapper.eq(GameLevel::getGameId, gameId);
        wrapper.eq(GameLevel::getStatus, 0);
        wrapper.orderByAsc(GameLevel::getSort);
        return gameLevelMapper.selectVoList(wrapper);
    }
}
