package com.business.domain;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 支付订单状态枚举.
 *
 * @author companion
 */
public enum PayOrderStatus {

    /**
     * 待支付
     */
    WAITING("0"),

    /**
     * 已支付
     */
    PAID("1"),

    /**
     * 已关闭
     */
    CLOSED("2"),

    /**
     * 已退款
     */
    REFUNDED("3");

    @EnumValue
    private final String code;

    PayOrderStatus(String code) {
        this.code = code;
    }

    /**
     * 获取状态码，用于JSON序列化
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 根据code获取枚举
     */
    public static PayOrderStatus of(String code) {
        for (PayOrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
