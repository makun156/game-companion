package com.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 游戏类别对象 t_game_category
 *
 * @author Mk
 * @date 2026-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_game_category")
public class GameCategory extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 游戏类别名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;


}
