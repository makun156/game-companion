package com.companion.xcx.domain.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 小程序陪玩下单请求参数.
 * <p>
 * openid 不需要前端传入，服务端根据当前登录用户 ID 查库获取.
 *
 * @author companion
 */
@Data
public class CompanionOrderCreateBo {

    /** 陪玩用户ID */
    private Long companionUserId;
    /** 商户ID（电竞酒店） */
    private Long merchantId;


    /** 游戏ID */
    private Long gameId;

    /** 游戏段位ID */
    private Long gameLevelId;

    /** 预约时长（小时） */
    private BigDecimal duration;

    /** 单价（分/小时） */
    private Long unitPrice;

    /** 订单总金额（分） */
    private Long totalAmount;

    /** 预约开始时间 */
    private String appointmentTime;

    /** 用户备注 */
    private String remark;

    /** 订单过期时间（分钟），默认15分钟 */
    private Integer expireMinutes;

}