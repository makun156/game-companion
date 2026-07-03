package com.companion.xcx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.City;
import com.business.domain.vo.CityVo;
import com.business.mapper.CityMapper;
import com.companion.xcx.service.IXcxCityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 小程序区域Service业务层处理
 *
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxCityServiceImpl implements IXcxCityService {

    private final CityMapper cityMapper;

    /**
     * 获取所有区域树形结构
     *
     * @return 区域树形列表
     */
    @Override
    public List<CityVo> getCityTree() {
        LambdaQueryWrapper<City> wrapper = Wrappers.lambdaQuery(City.class);
        wrapper.eq(City::getStatus, 0);
        wrapper.orderByAsc(City::getSort);
        List<CityVo> cityVoList = cityMapper.selectVoList(wrapper);
        return buildTree(cityVoList);
    }

    /**
     * 根据cityId获取自己和下级区域列表
     *
     * @param cityId 城市ID
     * @return 区域列表
     */
    @Override
    public List<CityVo> getCityAndChildren(Long cityId) {
        LambdaQueryWrapper<City> wrapper = Wrappers.lambdaQuery(City.class);
        wrapper.eq(City::getStatus, 0);
        wrapper.apply("FIND_IN_SET({0}, ancestors) OR id = {0}", cityId);
        wrapper.orderByAsc(City::getSort);
        return cityMapper.selectVoList(wrapper);
    }

    /**
     * 将扁平的CityVo列表构建为省->市->区/县的树形结构
     */
    private List<CityVo> buildTree(List<CityVo> cityVoList) {
        // 收集所有节点的id集合
        Set<Long> idSet = cityVoList.stream().map(CityVo::getId).collect(Collectors.toSet());
        // 按parentId分组
        Map<Long, List<CityVo>> parentMap = cityVoList.stream()
            .collect(Collectors.groupingBy(CityVo::getParentId));
        // 为每个节点挂载子节点
        cityVoList.forEach(city -> city.setChildren(parentMap.getOrDefault(city.getId(), new ArrayList<>())));
        // 返回根节点：parentId不在当前结果集id中的节点
        return cityVoList.stream()
            .filter(city -> !idSet.contains(city.getParentId()))
            .collect(Collectors.toList());
    }
}
