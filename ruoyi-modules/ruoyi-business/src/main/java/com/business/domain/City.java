package com.business.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 区域对象 t_city
 *
 * @author Mk
 * @date 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_city")
public class City extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 祖籍列表
     */
    private String ancestors;

    /**
     * 父id
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


}
