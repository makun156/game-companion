package com.business.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;

/**
 * 商家表对象 t_esports_hotel
 *
 * @author Mk
 * @date 2026-06-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_esports_hotel")
public class EsportsHotel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 商家名称
     */
    private String name;

    /**
     * 商家logo
     */
    private String avatar;

    /**
     * 城市区域id
     */
    private Long city;

    /**
     * 商家详细地址
     */
    private String address;

    /**
     * 营业时间
     */
    private String businessHours;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 标签
     */
    private String tags;

    /**
     * 商家介绍
     */
    private String description;

    /**
     * 评分
     */
    private BigDecimal rating;

    /**
     * 状态
     */
    private String status;


}
