package org.dromara.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 陪玩工作状态枚举
 *
 * @author Mk
 */
@Getter
@AllArgsConstructor
public enum WorkStatus {

    IDLE("1", "空闲中"),
    TAKING_ORDER("2", "接单中"),
    PLAYING("3", "陪玩中"),
    AWAY("4", "暂离"),
    RESTING("5", "休息中"),
    OFFLINE("6", "离线");

    private final String code;
    private final String desc;
}
