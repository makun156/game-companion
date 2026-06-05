package com.business.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.business.domain.GameLevel;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;



/**
 * 游戏段位视图对象 t_game_level
 *
 * @author Mk
 * @date 2026-06-05
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = GameLevel.class)
public class GameLevelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    private Long id;

    /**
     * 游戏id
     */
    @ExcelProperty(value = "游戏id")
    private Long gameId;

    /**
     * 游戏级别
     */
    @ExcelProperty(value = "游戏级别")
    private String level;

    private Integer sort;

    /**
     * 状态(0-启用[默认] 1-禁用)
     */
    private Integer status;

}
