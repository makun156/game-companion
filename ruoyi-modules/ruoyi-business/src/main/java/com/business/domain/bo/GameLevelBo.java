package com.business.domain.bo;

import com.business.domain.GameLevel;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 游戏段位业务对象 t_game_level
 *
 * @author Mk
 * @date 2026-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = GameLevel.class, reverseConvertGenerate = false)
public class GameLevelBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 游戏id
     */
    private Long gameId;

    /**
     * 游戏段位
     */
    private String level;

    /**
     * 排序
     */
    private Integer sort;
    /**
     * 状态
     */
    private Integer status;

}
