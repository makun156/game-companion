package com.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.City;
import com.business.domain.bo.CityBo;
import com.business.domain.vo.CityVo;
import com.business.mapper.CityMapper;
import com.business.service.ICityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 区域Service业务层处理
 *
 * @author Mk
 * @date 2026-06-03
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CityServiceImpl implements ICityService {

    private final CityMapper baseMapper;

    /**
     * 查询区域
     *
     * @param id 主键
     * @return 区域
     */
    @Override
    public CityVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }


    /**
     * 查询符合条件的区域列表
     *
     * @param bo 查询条件
     * @return 区域列表
     */
    @Override
    public List<CityVo> queryList(CityBo bo) {
        LambdaQueryWrapper<City> lqw = buildQueryWrapper(bo);
        List<City> queryCityList = baseMapper.selectList(lqw);
        List<CityVo> voList = MapstructUtils.convert(queryCityList, CityVo.class);
        return buildTree(voList);
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

    private LambdaQueryWrapper<City> buildQueryWrapper(CityBo bo) {
        LambdaQueryWrapper<City> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(City::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), City::getName, bo.getName());
        lqw.eq(bo.getLevel() != null, City::getLevel, bo.getLevel());
        lqw.ne(City::getParentId, "0");
        lqw.like(StringUtils.isNotBlank(bo.getFullName()), City::getFullName, bo.getFullName());
        lqw.eq(bo.getSort() != null, City::getSort, bo.getSort());
        lqw.eq(bo.getStatus() != null, City::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增区域
     *
     * @param bo 区域
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(CityBo bo) {
        City add = MapstructUtils.convert(bo, City.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        //if (flag) {
        //    bo.setId(add.getId());
        //}
        return flag;
    }

    /**
     * 修改区域
     *
     * @param bo 区域
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(CityBo bo) {
        City update = MapstructUtils.convert(bo, City.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(City entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除区域信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
