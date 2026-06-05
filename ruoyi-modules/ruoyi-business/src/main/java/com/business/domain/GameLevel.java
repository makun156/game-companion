package com.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 游戏段位对象 t_game_level
 *
 * @author Mk
 * @date 2026-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_game_level")
public class GameLevel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id")
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
