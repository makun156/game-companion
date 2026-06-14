package com.business.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_game_companion_user_photo")
public class GameCompanionUserPhoto extends BaseEntity {
     /**
     * 主键id
     */
    @TableId(value = "id")
    private Long id;
    /**
     * 陪玩id
     */
    private Long companionId;

    /**
     * 照片ossid
     */
    private String photo;
}
