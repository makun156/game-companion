package com.business.domain.vo;

import java.math.BigDecimal;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;
import com.business.domain.EsportsHotel;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 商家表视图对象 t_esports_hotel
 *
 * @author Mk
 * @date 2026-06-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = EsportsHotel.class)
public class EsportsHotelVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private Long id;

    /**
     * 商家名称
     */
    @ExcelProperty(value = "商家名称")
    private String name;

    /**
     * 商家logo
     */
    @ExcelProperty(value = "商家logo")
    private String avatar;

    /**
     * 商家logoUrl
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "avatar")
    private String avatarUrl;
    /**
     * 城市区域id
     */
    @ExcelProperty(value = "城市区域id")
    private Long city;

    /**
     * 商家详细地址
     */
    @ExcelProperty(value = "商家详细地址")
    private String address;

    /**
     * 营业时间
     */
    @ExcelProperty(value = "营业时间")
    private String businessHours;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String phone;

    /**
     * 评分
     */
    @ExcelProperty(value = "评分")
    private BigDecimal rating;

    /**
     * 状态
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "business_use_status")
    private String status;


}
