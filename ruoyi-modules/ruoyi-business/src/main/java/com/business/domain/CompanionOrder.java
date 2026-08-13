package com.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 陪玩订单表对象 t_companion_order.
 *
 * @author companion
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_companion_order")
public class CompanionOrder extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 订单号，如 CO202608091234560001
     */
    private String orderNo;

    /**
     * 下单用户ID（t_user）
     */
    private Long userId;

    /**
     * 陪玩用户ID（t_game_companion_user）
     */
    private Long companionUserId;

    /** 商户ID（t_esports_hotel） */
    private Long merchantId;

    /**
     * 游戏ID（t_game）
     */
    private Long gameId;

    /**
     * 游戏段位ID（t_game_level）
     */
    private Long gameLevelId;

    /**
     * 预约时长（小时，如1.5）
     */
    private BigDecimal duration;

    /**
     * 单价（分/小时）
     */
    private Long unitPrice;

    /**
     * 订单总金额（分）
     */
    private Long totalAmount;

    /**
     * 已支付金额（分）
     */
    private Long paidAmount;

    /**
     * 已退款金额（分）
     */
    private Long refundAmount;

    /**
     * 预约开始时间
     */
    private Date appointmentTime;

    /**
     * 实际上单时间
     */
    private Date actualStartTime;

    /**
     * 实际结束时间
     */
    private Date actualEndTime;

    /**
     * 订单状态：PENDING_PAYMENT-待支付 PAID-已支付 IN_PROGRESS-进行中 COMPLETED-已完成 CANCELLED-已取消 EXPIRED-已过期 REFUNDING-退款中 REFUNDED-已退款
     */
    private CompanionOrderStatus orderStatus;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 取消时间
     */
    private Date cancelTime;

    /**
     * 用户备注
     */
    private String remark;

    /**
     * 删除标志（0存在 1删除）
     */
    @TableLogic
    private String delFlag;

}
