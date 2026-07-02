package com.companion.xcx.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录用户类型枚举
 *
 * @author Mk
 */
@Getter
@AllArgsConstructor
public enum LoginUserType {

    COMPANION("1", "陪玩"),
    USER("2", "用户");

    private final String code;
    private final String desc;

    /**
     * 根据 code 获取枚举
     */
    public static LoginUserType getByCode(String code) {
        for (LoginUserType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断是否为陪玩类型
     */
    public static boolean isCompanion(String code) {
        return COMPANION.getCode().equals(code);
    }

    /**
     * 判断是否为用户类型
     */
    public static boolean isUser(String code) {
        return USER.getCode().equals(code);
    }
}
