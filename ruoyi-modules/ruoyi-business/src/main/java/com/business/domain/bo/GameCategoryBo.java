package com.business.domain.bo;

import com.business.domain.GameCategory;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 游戏类别业务对象 t_game_category
 *
 * @author Mk
 * @date 2026-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = GameCategory.class, reverseConvertGenerate = false)
public class GameCategoryBo extends BaseEntity {

    private Long id;
    /**
     * 游戏类别名称
     */
    @NotBlank(message = "游戏类别名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer sort;

    /**
     * 状态
     */
    @NotNull(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer status;


}
