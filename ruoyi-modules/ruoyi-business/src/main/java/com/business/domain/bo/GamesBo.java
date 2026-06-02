package com.business.domain.bo;

import com.business.domain.Games;
import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 游戏列表业务对象 t_games
 *
 * @author Lion Li
 * @date 2026-06-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Games.class, reverseConvertGenerate = false)
public class GamesBo extends BaseEntity {

    /**
     *
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
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
