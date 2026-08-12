package com.business.domain;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 陪玩工作状态枚举.
 * <p>
 * 与 t_companion_order 订单状态联动：
 * 接单中  → 无进行中的订单
 * 陪玩中  → 有已支付/进行中的订单
 * 暂离    → 陪玩手动设置
 * 休息中  → 陪玩手动设置
 * 离线    → 陪玩手动设置
 *
 * @author companion
 */
public enum WorkStatus {

    /**
     * 接单中：可以接受新订单
     */
    AVAILABLE("AVAILABLE", "接单中"),

    /**
     * 陪玩中：正在服务中
     */
    PLAYING("PLAYING", "陪玩中"),

    /**
     * 暂离：临时离开
     */
    AWAY("AWAY", "暂离"),

    /**
     * 休息中：休息不接单
     */
    RESTING("RESTING", "休息中"),

    /**
     * 离线：未登录
     */
    OFFLINE("OFFLINE", "离线");

    @EnumValue
    private final String code;

    private final String displayName;

    WorkStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static WorkStatus of(String code) {
        for (WorkStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}