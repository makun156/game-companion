package com.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 陪玩关联游戏段位对象 t_companion_game_level
 *
 * @author Mk
 * @date 2026-06-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_companion_game_level")
public class CompanionGameLevel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id")
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
