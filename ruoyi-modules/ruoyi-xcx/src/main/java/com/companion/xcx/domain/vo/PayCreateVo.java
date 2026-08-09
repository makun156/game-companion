package com.companion.xcx.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * wx.requestPayment调起支付所需参数
 *
 * @author companion
 */
@Data
public class PayCreateVo {

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 小程序AppID
     */
    private String appId;

    /**
     * 时间戳（秒级）
     */
    private String timeStamp;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 统一下单接口返回的prepay_id，格式：prepay_id=***
     */
    @JsonProperty("package")
    private String packageVal;

    /**
     * 签名方式，如RSA
     */
    private String signType;

    /**
     * 签名
     */
    private String paySign;

}
