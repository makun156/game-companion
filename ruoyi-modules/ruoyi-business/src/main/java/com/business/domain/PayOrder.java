package com.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.util.Date;

/**
 * 微信小程序支付订单.
 *
 * @author companion
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_pay_order")
public class PayOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id")
    private Long id;

    /**
     * 商户订单号，在商户内唯一.
     */
    private String orderNo;

    /**
     * 当前登录用户ID.
     */
    private Long userId;

    /**
     * 调用方传入的业务类型.
     */
    private String bizType;

    /**
     * 调用方传入的业务ID.
     */
    private Long bizId;

    /**
     * 微信支付用户的openid.
     */
    private String openid;

    /**
     * 商品描述，展示在微信账单中.
     */
    private String title;

    /**
     * 支付金额，单位为分.
     */
    private Long amount;

    /**
     * 订单状态：WAITING-待支付，PAID-已支付，CLOSED-已关闭，REFUNDED-已退款.
     */
    private PayOrderStatus status;

    /**
     * 商户自定义数据，微信支付回调时原样返回.
     */
    private String attach;

    /**
     * 微信支付交易号.
     */
    private String transactionId;

    /**
     * 支付时间.
     */
    private Date payTime;

    /**
     * 订单过期时间.
     */
    private Date expireTime;

    /**
     * 关闭时间.
     */
    private Date closeTime;

    private String remark;

}
