package com.business.domain.bo;

import com.business.domain.CompanionGameLevel;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 陪玩关联游戏段位业务对象 t_companion_game_level
 *
 * @author Mk
 * @date 2026-06-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = CompanionGameLevel.class, reverseConvertGenerate = false)
public class CompanionGameLevelBo extends BaseEntity {

    /**
     * 主键id
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 陪玩id
     */
    private Long userId;

    /**
     * 游戏id
     */
    private Long gameId;

    /**
     * 游戏段位id
     */
    private Long gameLevelId;

}
