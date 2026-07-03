package com.companion.xcx.controller;

import com.business.domain.bo.GameCompanionPhotoBo;
import com.business.domain.vo.GameCompanionUserPhotoVo;
import com.companion.xcx.service.IXcxCompanionPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序陪玩照片-controller
 *
 * @author system
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/companion/photo")
public class XcxCompanionPhotoController {

    private final IXcxCompanionPhotoService companionPhotoService;

    /**
     * 获取当前登录陪玩的照片列表
     */
    @GetMapping("/list")
    public R<List<GameCompanionUserPhotoVo>> getPhotoList() {
        return R.ok(companionPhotoService.getPhotoList());
    }

    /**
     * 修改当前登录陪玩的照片
     */
    @Log(title = "小程序陪玩照片-修改", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public R<Boolean> updatePhoto(@RequestBody List<GameCompanionPhotoBo> photoList) {
        return R.ok(companionPhotoService.updatePhoto(photoList));
    }
}
