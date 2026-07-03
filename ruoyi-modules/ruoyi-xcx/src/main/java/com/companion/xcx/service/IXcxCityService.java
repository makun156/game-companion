package com.companion.xcx.service;

import com.business.domain.vo.CityVo;

import java.util.List;

/**
 * 小程序区域Service接口
 *
 * @author system
 */
public interface IXcxCityService {

    /**
     * 获取所有区域树形结构
     *
     * @return 区域树形列表
     */
    List<CityVo> getCityTree();

    /**
     * 根据cityId获取自己和下级区域列表
     *
     * @param cityId 城市ID
     * @return 区域列表
     */
    List<CityVo> getCityAndChildren(Long cityId);
}
