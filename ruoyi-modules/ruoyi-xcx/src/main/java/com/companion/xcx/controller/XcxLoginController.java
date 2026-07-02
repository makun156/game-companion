package com.companion.xcx.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.business.domain.vo.UserVo;
import com.business.domain.vo.XcxLoginVo;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import com.companion.xcx.config.WechatMiniappProperties;
import com.companion.xcx.service.IXcxLoginService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序登录认证
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
}
