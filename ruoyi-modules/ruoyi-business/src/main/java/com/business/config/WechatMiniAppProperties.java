package com.business.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信小程序配置
 *
 * @author Mk
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniAppProperties {

    /**
     * 小程序配置 Map，key 为 appid，value 为 secret
     */
    private Map<String, String> configs;

    /**
     * 获取 appid（取 configs 第一个 key）
     */
    public String getAppid() {
        if (configs == null || configs.isEmpty()) {
            return null;
        }
        return configs.keySet().iterator().next();
    }

    /**
     * 获取 secret（取 configs 第一个 value）
     */
    public String getSecret() {
        if (configs == null || configs.isEmpty()) {
            return null;
        }
        return configs.values().iterator().next();
    }
}
