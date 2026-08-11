package com.business.domain;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 陪玩订单状态枚举.
 *
 * @author companion
 */
public enum CompanionOrderStatus {

    /**
     * 待支付：用户已下单，等待支付
     */
    PENDING_PAYMENT("PENDING_PAYMENT", "待支付"),

    /**
     * 已支付：支付成功，等待开始服务
     */
    PAID("PAID", "已支付"),

    /**
     * 进行中：陪玩正在服务中
     */
    IN_PROGRESS("IN_PROGRESS", "进行中"),

    /**
     * 已完成：服务结束
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 已取消：用户主动取消（未支付时）
     */
    CANCELLED("CANCELLED", "已取消"),

    /**
     * 已过期：超时未支付，系统自动关闭
     */
    EXPIRED("EXPIRED", "已过期"),

    /**
     * 退款中：已支付后申请退款，等待处理
     */
    REFUNDING("REFUNDING", "退款中"),

    /**
     * 已退款：退款完成
     */
    REFUNDED("REFUNDED", "已退款");

    @EnumValue
    private final String code;

    private final String displayName;

    CompanionOrderStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 获取状态码，用于JSON序列化
     */
    @JsonValue
    public String getCode() {
        return code;
    }

    /**
     * 获取中文显示名
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据code获取枚举
     */
    public static CompanionOrderStatus of(String code) {
        for (CompanionOrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
