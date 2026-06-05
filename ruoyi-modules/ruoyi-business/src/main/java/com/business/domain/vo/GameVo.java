package com.business.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.business.domain.Game;
import com.business.domain.bo.GameLevelBo;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 游戏列表视图对象 t_game
 *
 * @author Lion Li
 * @date 2026-06-02
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Game.class)
public class GameVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 游戏名称
     */
    @ExcelProperty(value = "游戏名称")
    private String name;

    /**
     * 游戏图标地址
     */
    @ExcelProperty(value = "游戏图标地址")
    private String icon;

    /**
     * 游戏图标地址Url
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "icon")
    private String iconUrl;
    /**
     * 描述
     */
    @ExcelProperty(value = "描述")
    private String description;

    /**
     * 游戏分类
     */
    private Long categoryId;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Long sort;

    /**
     * 状态(0-启用[默认] 1-禁用)
     */
    @ExcelProperty(value = "状态(0-启用[默认] 1-禁用)", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_use_status")
    private Long status;
    /**
     * 游戏段位
     */
    private List<GameLevelVo> gameLevels;

}
