package com.business.domain.bo;

import com.business.domain.GameCompanionUser;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.List;

/**
 * 陪玩表业务对象 t_game_companion_user
 *
 * @author Mk
 * @date 2026-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = GameCompanionUser.class, reverseConvertGenerate = false)
public class GameCompanionUserBo extends BaseEntity {

    /**
     *
     */
    private Long id;

    /**
     * 陪玩名称
     */
    @NotBlank(message = "陪玩名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 性别(0男 1女)
     */
    @NotBlank(message = "性别不能为空", groups = { AddGroup.class, EditGroup.class })
    private String gender;

    /**
     * 年龄
     */
    @NotNull(message = "年龄不能为空", groups = { AddGroup.class, EditGroup.class })
    private Integer age;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String phone;

    /**
     * 头像路径
     */
    private String avatar;

    /**
     * 接单区域id
     */
    @NotNull(message = "接单区域不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long city;

    /**
     * 自我介绍
     */
    private String introduction;

    /**
     * 标签
     */
    private String tags;

    /**
     * 小时价格
     */
    @NotNull(message = "小时价格不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long pricePerHour;

    /**
     * 接单量
     */
    private Integer totalOrders;

    /**
     * 状态
     */
    private String status;

    /**
     * 工作状态
     */
    private String workStatus;

    List<GameCompanionPhotoBo> photos;
}
