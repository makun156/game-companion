package com.companion.xcx.config;

import cn.hutool.core.util.StrUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 微信支付APIv3配置（商户号、API证书、回调地址等）
 *
 * @author companion
 */
@Slf4j
@Component
public class WechatPayConfig {

    @Value("${wechat.pay.merchantId}")
    private String merchantId;

    @Value("${wechat.pay.merchantSerialNumber}")
    private String merchantSerialNumber;

    @Value("${wechat.pay.apiV3Key}")
    private String apiV3Key;

    @Value("${wechat.pay.appid}")
    private String appid;

    @Value("${wechat.pay.privateKeyPath}")
    private String privateKeyPath;

    @Value("${wechat.pay.notifyUrl}")
    private String notifyUrl;

    /**
     * 微信支付公钥ID，配置后启用公钥模式，格式：PUB_KEY_ID_xxx
     */
    @Value("${wechat.pay.publicKeyId:}")
    private String publicKeyId;

    /**
     * 微信支付公钥文件路径，classpath下的PEM文件，从商户平台下载后放入证书目录
     */
    @Value("${wechat.pay.publicKeyPath:}")
    private String publicKeyPath;

    private Config config;

    private NotificationConfig notificationConfig;

    private JsapiServiceExtension jsapiServiceExtension;

    private NotificationParser notificationParser;

    @PostConstruct
    public void init() {
        try {
            // 加载商户API私钥文件
            String privateKey = loadResource(privateKeyPath);
            if (StrUtil.isNotBlank(publicKeyId) && StrUtil.isNotBlank(publicKeyPath)) {
                // 微信支付公钥模式：新商户无平台证书，需使用此模式
                // 使用商户API公钥+公钥ID进行配置
                RSAPublicKeyConfig publicKeyConfig = new RSAPublicKeyConfig.Builder()
                    .merchantId(merchantId)
                    .merchantSerialNumber(merchantSerialNumber)
                    .privateKey(privateKey)
                    .apiV3Key(apiV3Key)
                    .publicKey(loadResource(publicKeyPath))
                    .publicKeyId(publicKeyId)
                    .build();
                config = publicKeyConfig;
                notificationConfig = publicKeyConfig;
            } else {
                // 平台证书模式：自动下载并更新平台证书
                // 使用平台证书自动更新配置，证书到期后自动续期
                RSAAutoCertificateConfig certificateConfig = new RSAAutoCertificateConfig.Builder()
                    .merchantId(merchantId)
                    .merchantSerialNumber(merchantSerialNumber)
                    .privateKey(privateKey)
                    .apiV3Key(apiV3Key)
                    .build();
                config = certificateConfig;
                notificationConfig = certificateConfig;
            }
            // 构建JSAPI支付服务扩展（包含下单、查询、关单等能力）
            jsapiServiceExtension = new JsapiServiceExtension.Builder()
                .config(config)
                .build();
            // 构建通知解析器，用于验签并解析支付回调通知
            notificationParser = new NotificationParser(notificationConfig);
            log.info("Wechat Pay config initialized, merchantId={}, appid={}, mode={}",
                merchantId, appid, StrUtil.isNotBlank(publicKeyId) ? "publicKey" : "certificate");
        } catch (Exception e) {
            log.error("Wechat Pay config init failed", e);
        }
    }

    /**
     * 从classpath加载PEM证书/密钥文件
     */
    private String loadResource(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * 检查微信支付所需的全部组件是否已成功初始化
     */
    public boolean isReady() {
        return config != null && notificationConfig != null
            && jsapiServiceExtension != null && notificationParser != null;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getAppid() {
        return appid;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public JsapiServiceExtension getJsapiServiceExtension() {
        return jsapiServiceExtension;
    }

    public NotificationParser getNotificationParser() {
        return notificationParser;
    }

}
