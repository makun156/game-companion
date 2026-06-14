package com.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 游戏列表对象 t_game
 *
 * @author Lion Li
 * @date 2026-06-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_game")
public class Game extends BaseEntity {

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
     * 游戏分类id
     */
    private Long categoryId;

    /**
     * 排序
     */
    private Long sort;

    /**
     * 状态(0-启用[默认] 1-禁用)
     */
    @TableLogic
    private Long status;


}
