package com.companion.xcx.service;

import com.business.domain.vo.EsportsHotelVo;

import java.util.List;

/**
 * 小程序商家Service接口
 *
 * @author system
 */
public interface IXcxMerchantService {

    /**
     * 获取商家列表
     *
     * @return 商家列表
     */
    List<EsportsHotelVo> getShopList();

    /**
     * 根据城市ID获取商家列表
     *
     * @param cityId 城市ID
     * @return 商家列表
     */
    List<EsportsHotelVo> getShopListByCity(Long cityId);
}
