package com.companion.xcx.controller;

import com.business.domain.bo.UserBo;
import com.business.domain.vo.UserVo;
import com.companion.xcx.service.IXcxUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序用户-controller
 *
 * @author system
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/xcx/user")
public class XcxUserController {

    private final IXcxUserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public R<UserVo> getUserInfo() {
        return R.ok(userService.getUserInfo());
    }

    /**
     * 修改当前登录用户信息
     */
    @Log(title = "小程序用户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public R<Boolean> updateUserInfo(@RequestBody UserBo bo) {
        return R.ok(userService.updateUserInfo(bo));
    }
}
