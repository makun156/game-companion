package com.companion.xcx.controller;

import com.business.domain.vo.GameLevelVo;
import com.business.domain.vo.GameVo;
import com.companion.xcx.service.IXcxGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序游戏-controller
 *
 * @author system
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/xcx/game")
public class XcxGameController {

    private final IXcxGameService gameService;

    /**
     * 获取所有游戏列表
     */
    @GetMapping("/list")
    public R<List<GameVo>> getGameList() {
        return R.ok(gameService.getGameList());
    }

    /**
     * 根据游戏ID获取段位列表
     */
    @GetMapping("/level/{gameId}")
    public R<List<GameLevelVo>> getLevelList(@PathVariable Long gameId) {
        return R.ok(gameService.getLevelList(gameId));
    }
}
