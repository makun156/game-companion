package org.dromara.web.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信小程序配置属性
 *
 * @author Mk
 */
@Data
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappProperties {

    /**
     * 多个小程序的 appid → secret 映射
     * 例如: configs:
     *        wx1234567890: abc123def456
     *        wx0987654321: xyz789uvw012
     */
    private Map<String, String> configs;

}