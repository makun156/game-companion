package com.companion.xcx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.City;
import com.business.domain.EsportsHotel;
import com.business.domain.vo.EsportsHotelVo;
import com.business.mapper.CityMapper;
import com.business.mapper.EsportsHotelMapper;
import com.companion.xcx.service.IXcxMerchantService;
import com.companion.xcx.service.IXcxShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序商家Service业务层处理
 *
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxMerchantServiceImpl implements IXcxMerchantService {

    private final EsportsHotelMapper esportsHotelMapper;
    private final CityMapper cityMapper;

    /**
     * 获取商家列表
     *
     * @return 商家列表
     */
    @Override
    public List<EsportsHotelVo> getShopList() {
        LambdaQueryWrapper<EsportsHotel> wrapper = Wrappers.lambdaQuery(EsportsHotel.class);
        wrapper.eq(EsportsHotel::getStatus, "0");
        wrapper.orderByDesc(EsportsHotel::getRating);
        return esportsHotelMapper.selectVoList(wrapper);
    }

    /**
     * 根据城市ID获取商家列表（包含所有子区域商家）
     *
     * @param cityId 城市ID
     * @return 商家列表
     */
    @Override
    public List<EsportsHotelVo> getShopListByCity(Long cityId) {
        // 查询该城市及其所有子区域ID
        LambdaQueryWrapper<City> cityWrapper = Wrappers.lambdaQuery(City.class);
        cityWrapper.apply("FIND_IN_SET({0}, ancestors) OR id = {0}", cityId);
        List<Long> cityIds = cityMapper.selectList(cityWrapper).stream()
                .map(City::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<EsportsHotel> wrapper = Wrappers.lambdaQuery(EsportsHotel.class);
        wrapper.eq(EsportsHotel::getStatus, "0");
        wrapper.in(EsportsHotel::getCity, cityIds);
        wrapper.orderByDesc(EsportsHotel::getRating);
        return esportsHotelMapper.selectVoList(wrapper);
    }
}
