package com.business.enums;

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

    TAKING_ORDER("1", "接单中"),
    PLAYING("2", "陪玩中"),
    AWAY("3", "暂离"),
    RESTING("4", "休息中"),
    OFFLINE("5", "离线");

    private final String code;
    private final String desc;
}
