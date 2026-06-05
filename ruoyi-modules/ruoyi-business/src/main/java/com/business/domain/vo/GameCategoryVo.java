package com.business.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.business.domain.GameCategory;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;

import java.io.Serial;
import java.io.Serializable;



/**
 * 游戏类别视图对象 t_game_category
 *
 * @author Mk
 * @date 2026-06-04
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = GameCategory.class)
public class GameCategoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 游戏类别名称
     */
    @ExcelProperty(value = "游戏类别名称")
    private String name;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Integer sort;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "business_use_status")
    private Integer status;


}
