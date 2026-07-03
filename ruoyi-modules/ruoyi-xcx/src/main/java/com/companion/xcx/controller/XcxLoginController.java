package com.companion.xcx.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.business.domain.vo.UserVo;
import com.business.domain.vo.XcxLoginVo;
import com.companion.xcx.config.WechatMiniappProperties;
import com.companion.xcx.service.IXcxLoginService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序登录认证-controller
 *
 * @author system
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class XcxLoginController {

    private final IXcxLoginService loginService;
    private final WechatMiniappProperties wechatMiniappProperties;

    /**
     * 微信一键登录
     *
     * @param xcxCode 小程序code
     * @return 登录信息
     */
    @SaIgnore
    @Log(title = "小程序登录", businessType = BusinessType.OTHER)
    @PostMapping("/login")
    public R<XcxLoginVo> login(
        @NotBlank(message = "小程序code不能为空") @RequestParam String xcxCode) {
        return R.ok(loginService.login(xcxCode));
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public R<UserVo> getInfo() {
        return R.ok(loginService.getInfo());
    }

    /**
     * 获取小程序配置信息
     */
    @SaIgnore
    @GetMapping("/config")
    public R<Map<String, String>> getConfig() {
        Map<String, String> result = new HashMap<>();
        result.put("appId", wechatMiniappProperties.getAppid());
        return R.ok(result);
    }

    /**
     * 退出登录
     */
    @Log(title = "小程序退出", businessType = BusinessType.OTHER)
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok("退出成功");
    }
}
