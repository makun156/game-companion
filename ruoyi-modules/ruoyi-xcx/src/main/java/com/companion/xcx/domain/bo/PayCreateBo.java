package com.companion.xcx.domain.bo;

import lombok.Data;

/**
 * 小程序支付下单请求参数.
 * <p>
 * openid 不需要前端传入，服务端根据当前登录用户 ID 查库获取.
 *
 * @author companion
 */
@Data
public class PayCreateBo {

    /**
     * 商户订单号，可选，为空时自动生成
     */
    private String orderNo;

    /**
     * 商品描述，最大127个字符
     */
    private String description;

    /**
     * 支付金额，单位为分，必须大于0
     */
    private Long amount;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 订单过期时间（分钟），默认15分钟
     */
    private Integer expireMinutes;

}