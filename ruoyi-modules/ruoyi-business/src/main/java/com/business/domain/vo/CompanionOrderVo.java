package com.business.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.business.domain.CompanionOrder;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 陪玩订单视图对象 t_companion_order（管理端列表用）.
 *
 * @author companion
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CompanionOrder.class)
public class CompanionOrderVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 订单号 */
    @ExcelProperty(value = "订单号")
    private String orderNo;

    /** 订单状态 */
    @ExcelProperty(value = "订单状态")
    private String orderStatus;

    /** 订单总金额（分） */
    @ExcelProperty(value = "总金额")
    private Long totalAmount;

    /** 已支付金额（分） */
    @ExcelProperty(value = "已支付")
    private Long paidAmount;

    /** 已退款金额（分） */
    @ExcelProperty(value = "已退款")
    private Long refundAmount;

    /** 预约时长（小时） */
    @ExcelProperty(value = "时长")
    private BigDecimal duration;

    /** 单价（分/小时） */
    @ExcelProperty(value = "单价")
    private Long unitPrice;

    /** 预约开始时间 */
    @ExcelProperty(value = "预约时间")
    private Date appointmentTime;

    /** 实际上单时间 */
    @ExcelProperty(value = "实际上单")
    private Date actualStartTime;

    /** 实际结束时间 */
    @ExcelProperty(value = "实际结束")
    private Date actualEndTime;

    /** 取消原因 */
    @ExcelProperty(value = "取消原因")
    private String cancelReason;

    /** 取消时间 */
    @ExcelProperty(value = "取消时间")
    private Date cancelTime;

    /** 创建时间 */
    @ExcelProperty(value = "下单时间")
    private Date createTime;

    /** 备注 */
    private String remark;

    // ========== 关联查询字段（非数据库字段） ==========

    /** 下单用户昵称 */
    @ExcelProperty(value = "用户昵称")
    private String userNickName;

    /** 下单用户手机号 */
    @ExcelProperty(value = "用户手机")
    private String userPhone;

    /** 陪玩名称 */
    @ExcelProperty(value = "陪玩名称")
    private String companionName;

    /** 陪玩昵称 */
    @ExcelProperty(value = "陪玩昵称")
    private String companionNickName;

    /** 陪玩手机号 */
    @ExcelProperty(value = "陪玩手机")
    private String companionPhone;

    /** 陪玩头像 */
    private String companionAvatar;

    /** 游戏名称 */
    @ExcelProperty(value = "游戏")
    private String gameName;

    /** 游戏段位名称 */
    @ExcelProperty(value = "段位")
    private String gameLevelName;

    /** 支付订单号 */
    @ExcelProperty(value = "支付单号")
    private String payOrderNo;

    /** 微信支付交易号 */
    @ExcelProperty(value = "微信交易号")
    private String transactionId;

    /** 支付时间 */
    @ExcelProperty(value = "支付时间")
    private Date payTime;

}