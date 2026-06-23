package com.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.business.domain.vo.CompanionGameLevelVo;
import com.business.service.ICompanionGameLevelService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 陪玩关联游戏段位
 *
 * @author Mk
 * @date 2026-06-22
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/companion/gameLevel")
public class CompanionGameLevelController extends BaseController {

    private final ICompanionGameLevelService companionGameLevelService;

    /**
     * 根据陪玩用户id查询关联游戏段位列表（含游戏名称、段位名称）
     *
     * @param userId 陪玩用户id
     */
    @GetMapping("/list/{userId}")
    @SaCheckPermission("companion:gameLevel:list")
    public R<List<CompanionGameLevelVo>> list(
        @NotNull(message = "陪玩用户id不能为空") @PathVariable Long userId) {
        return R.ok(companionGameLevelService.queryListByUserId(userId));
    }

}
