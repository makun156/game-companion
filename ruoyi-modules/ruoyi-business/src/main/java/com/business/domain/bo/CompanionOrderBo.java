package com.business.domain.bo;

import com.business.domain.CompanionOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 陪玩订单业务对象 t_companion_order.
 *
 * @author companion
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = CompanionOrder.class, reverseConvertGenerate = false)
public class CompanionOrderBo extends BaseEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 下单用户ID
     */
    private Long userId;

    /**
     * 陪玩用户ID
     */
    private Long companionUserId;

    /**
     * 游戏ID
     */
    private Long gameId;

    /**
     * 游戏段位ID
     */
    private Long gameLevelId;

    /**
     * 预约时长（小时）
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
     * 订单状态
     */
    private String orderStatus;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 取消时间
     */
    private Date cancelTime;

    /**
     * 备注
     */
    private String remark;

}