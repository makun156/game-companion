package com.business.domain.vo;

import com.business.domain.GameCompanionUser;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 陪玩表视图对象 t_game_companion_user
 *
 * @author Mk
 * @date 2026-06-10
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = GameCompanionUser.class)
public class GameCompanionUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 陪玩名称
     */
    @ExcelProperty(value = "陪玩名称")
    private String name;

    /**
     * 性别(0男 1女)
     */
    @ExcelProperty(value = "性别(0男 1女)", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "business_gender")
    private String gender;

    /**
     * 年龄
     */
    @ExcelProperty(value = "年龄")
    private Long age;

    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号")
    private String phone;

    /**
     * 头像路径
     */
    @ExcelProperty(value = "头像路径")
    private String avatar;
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "avatar")
    private String avatarUrl;

    /**
     * 接单区域id
     */
    @ExcelProperty(value = "接单区域id")
    private Long city;

    /**
     * 自我介绍
     */
    @ExcelProperty(value = "自我介绍")
    private String introduction;

    /**
     * 标签
     */
    @ExcelProperty(value = "标签")
    private String tags;

    /**
     * 小时价格
     */
    @ExcelProperty(value = "小时价格")
    private Long pricePerHour;

    /**
     * 接单量
     */
    @ExcelProperty(value = "接单量")
    private Integer totalOrders;


    /**
     * 工作状态
     */
    @ExcelProperty(value = "工作状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "business_work_status")
    private String workStatus;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "business_use_status")
    private String status;

    private List<GameCompanionUserPhotoVo> photos;
}
