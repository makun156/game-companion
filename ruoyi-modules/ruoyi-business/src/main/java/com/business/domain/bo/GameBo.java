package com.business.domain.bo;

import com.business.domain.Game;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 游戏列表业务对象 t_game
 *
 * @author Lion Li
 * @date 2026-06-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Game.class, reverseConvertGenerate = false)
public class GameBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 游戏名称
     */
    private String name;

    /**
     * 游戏图标地址
     */
    private String icon;

    /**
     * 描述
     */
    private String description;

    /**
     * 游戏分类()
     */
    private Long categoryId;

    /**
     * 排序
     */
    private Long sort;

    /**
     * 状态(0-启用[默认] 1-禁用)
     */
    private Long status;


}
