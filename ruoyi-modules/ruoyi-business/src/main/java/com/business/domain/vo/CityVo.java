package com.business.domain.vo;

import com.business.domain.City;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 区域视图对象 t_city
 *
 * @author Mk
 * @date 2026-06-03
 */
@Data
@AutoMapper(target = City.class)
public class CityVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 父级id
     */
    private Long parentId;

    /**
     * 层级
     */
    private Long level;

    /**
     * 城市全称
     */
    private String fullName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private Integer status;

    private List<CityVo> children;

}
