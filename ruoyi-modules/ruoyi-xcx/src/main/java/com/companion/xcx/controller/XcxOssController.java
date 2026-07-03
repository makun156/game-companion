package com.companion.xcx.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.system.domain.vo.SysOssUploadVo;
import org.dromara.system.domain.vo.SysOssVo;
import org.dromara.system.service.ISysOssService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 小程序OSS对象存储-controller
 *
 * @author system
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/oss")
public class XcxOssController {

    private final ISysOssService ossService;

    /**
     * 上传OSS对象存储
     *
     * @param file 文件
     */
    @Log(title = "小程序OSS上传", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<SysOssUploadVo> upload(@RequestPart("file") MultipartFile file) {
        SysOssVo oss = ossService.upload(file);
        SysOssUploadVo uploadVo = new SysOssUploadVo();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileName(oss.getOriginalName());
        uploadVo.setOssId(oss.getOssId().toString());
        return R.ok(uploadVo);
    }

    /**
     * 删除OSS对象存储
     *
     * @param ossIds OSS对象ID串
     */
    @Log(title = "小程序OSS删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ossIds) {
        Boolean result = ossService.deleteWithValidByIds(List.of(ossIds), true);
        return result ? R.ok() : R.fail("删除失败");
    }
}
