package com.business.domain.bo;

import com.business.domain.City;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 区域业务对象 t_city
 *
 * @author Mk
 * @date 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = City.class, reverseConvertGenerate = false)
public class CityBo extends BaseEntity {

    /**
     * 区域名称
     */
    @NotBlank(message = "区域名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 层级
     */
    @NotNull(message = "层级不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long level;

    /**
     * 城市全称
     */
    private String fullName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;


}
