package com.business.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.business.domain.CompanionGameLevel;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 陪玩关联游戏段位视图对象 t_companion_game_level
 *
 * @author Mk
 * @date 2026-06-22
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = CompanionGameLevel.class)
public class CompanionGameLevelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 陪玩id
     */
    @ExcelProperty(value = "陪玩id")
    private Long userId;

    /**
     * 游戏id
     */
    @ExcelProperty(value = "游戏id")
    private Long gameId;

    /**
     * 游戏名称
     */
    @ExcelProperty(value = "游戏名称")
    private String gameName;

    /**
     * 游戏段位id
     */
    @ExcelProperty(value = "游戏段位id")
    private Long gameLevelId;

    /**
     * 游戏段位名称
     */
    @ExcelProperty(value = "游戏段位名称")
    private String gameLevelName;

}
