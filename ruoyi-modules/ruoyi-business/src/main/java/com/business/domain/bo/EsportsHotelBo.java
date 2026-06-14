package com.business.domain.bo;

import com.business.domain.EsportsHotel;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

/**
 * 商家表业务对象 t_esports_hotel
 *
 * @author Mk
 * @date 2026-06-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = EsportsHotel.class, reverseConvertGenerate = false)
public class EsportsHotelBo extends BaseEntity {

    /**
     * 
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 商家名称
     */
    @NotBlank(message = "商家名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 商家logo
     */
    private String avatar;

    /**
     * 城市区域id
     */
    @NotNull(message = "城市区域id不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "联系电话不能为空", groups = { AddGroup.class, EditGroup.class })
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
     * 状态
     */
    private String status;


}
