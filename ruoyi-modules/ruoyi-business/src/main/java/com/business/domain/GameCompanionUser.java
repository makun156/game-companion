package com.business.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 陪玩表对象 t_game_companion_user
 *
 * @author Mk
 * @date 2026-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_game_companion_user")
public class GameCompanionUser extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 陪玩名称
     */
    private String name;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别(0男 1女)
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 手机号
     */
    private String phone;

    /**
     * Wechat mini program openid.
     */
    private String openid;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 接单区域id
     */
    private Long city;

    /**
     * 自我介绍
     */
    private String introduction;

    /**
     * 标签
     */
    private String tags;

    /**
     * 小时价格
     */
    private Long pricePerHour;

    /**
     * 接单量
     */
    private Integer totalOrders;

    /**
     * 状态 0 正常 1 禁用
     */
    private String status;

    /**
     * 工作状态 1接单中 2陪玩中 3暂离4 休息中 5离线
     */
    private String workStatus;


}
