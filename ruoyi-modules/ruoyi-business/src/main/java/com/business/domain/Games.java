package com.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 游戏列表对象 t_games
 *
 * @author Lion Li
 * @date 2026-06-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_games")
public class Games extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 游戏名称
     */
    private String name;

    /**
     * 游戏图标地址
     */
    private String icon;

    /**
     * 描述
     */
    private String description;

    /**
     * 游戏分类(参见字典sys_category)
     */
    private String category;

    /**
     * 排序
     */
    private Long sort;

    /**
     * 状态(0-启用[默认] 1-禁用)
     */
    private Long status;


}
