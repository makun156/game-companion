package com.business.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import com.business.domain.GameCompanionUserPhoto;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = GameCompanionUserPhoto.class)
public class GameCompanionUserPhotoVo {
    /**
     * 主键id
     */
    private Long id;

    /**
     * 相册ossId
     */
    private String photo;

    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "photo")
    private String photoUrl;
}
