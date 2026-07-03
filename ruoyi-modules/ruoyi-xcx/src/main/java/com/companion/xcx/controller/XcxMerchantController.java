package com.companion.xcx.controller;

import com.business.domain.vo.EsportsHotelVo;
import com.companion.xcx.service.IXcxMerchantService;
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
 * 电竞酒店-controller
 *
 * @author system
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/merchant")
public class XcxMerchantController {

    private final IXcxMerchantService shopService;

    /**
     * 获取商家列表
     */
    @GetMapping("/list")
    public R<List<EsportsHotelVo>> getShopList() {
        return R.ok(shopService.getShopList());
    }

    /**
     * 根据城市ID获取商家列表
     */
    @GetMapping("/list/{cityId}")
    public R<List<EsportsHotelVo>> getShopListByCity(@PathVariable Long cityId) {
        return R.ok(shopService.getShopListByCity(cityId));
    }
}
