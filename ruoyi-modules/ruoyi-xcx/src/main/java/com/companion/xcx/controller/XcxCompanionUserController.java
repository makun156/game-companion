package com.companion.xcx.controller;

import com.business.domain.bo.GameCompanionUserBo;
import com.business.domain.vo.GameCompanionUserVo;
import com.companion.xcx.service.IXcxCompanionUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序陪玩用户-controller
 *
 * @author system
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/companion/user")
public class XcxCompanionUserController {

    private final IXcxCompanionUserService companionUserService;

    /**
     * 获取当前登录陪玩信息
     */
    @GetMapping("/info")
    public R<GameCompanionUserVo> getInfo() {
        return R.ok(companionUserService.getInfo());
    }

    /**
     * 修改当前登录陪玩信息
     */
    @Log(title = "小程序陪玩用户-修改", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public R<Boolean> updateInfo(@RequestBody GameCompanionUserBo bo) {
        return R.ok(companionUserService.updateInfo(bo));
    }
}
