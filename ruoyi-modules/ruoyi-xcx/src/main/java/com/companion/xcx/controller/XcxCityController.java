package com.companion.xcx.controller;

import com.business.domain.vo.CityVo;
import com.companion.xcx.service.IXcxCityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序区域-controller
 *
 * @author system
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/xcx/city")
public class XcxCityController {

    private final IXcxCityService cityService;

    /**
     * 获取所有区域树形结构
     */
    @GetMapping("/list")
    public R<List<CityVo>> getCityTree() {
        return R.ok(cityService.getCityTree());
    }

    /**
     * 根据cityId获取自己和下级区域列表
     */
    @GetMapping("/list/{cityId}")
    public R<List<CityVo>> getCityAndChildren(@PathVariable Long cityId) {
        return R.ok(cityService.getCityAndChildren(cityId));
    }
}
