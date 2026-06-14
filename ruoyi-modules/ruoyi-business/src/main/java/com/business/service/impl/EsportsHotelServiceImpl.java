package com.business.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.business.domain.bo.EsportsHotelBo;
import com.business.domain.vo.EsportsHotelVo;
import com.business.domain.EsportsHotel;
import com.business.mapper.EsportsHotelMapper;
import com.business.service.IEsportsHotelService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 商家表Service业务层处理
 *
 * @author Mk
 * @date 2026-06-14
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EsportsHotelServiceImpl implements IEsportsHotelService {

    private final EsportsHotelMapper baseMapper;

    /**
     * 查询商家表
     *
     * @param id 主键
     * @return 商家表
     */
    @Override
    public EsportsHotelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询商家表列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商家表分页列表
     */
    @Override
    public TableDataInfo<EsportsHotelVo> queryPageList(EsportsHotelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<EsportsHotel> lqw = buildQueryWrapper(bo);
        Page<EsportsHotelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的商家表列表
     *
     * @param bo 查询条件
     * @return 商家表列表
     */
    @Override
    public List<EsportsHotelVo> queryList(EsportsHotelBo bo) {
        LambdaQueryWrapper<EsportsHotel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<EsportsHotel> buildQueryWrapper(EsportsHotelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<EsportsHotel> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(EsportsHotel::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), EsportsHotel::getName, bo.getName());
        lqw.eq(bo.getCity() != null, EsportsHotel::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getPhone()), EsportsHotel::getPhone, bo.getPhone());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), EsportsHotel::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增商家表
     *
     * @param bo 商家表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(EsportsHotelBo bo) {
        EsportsHotel add = MapstructUtils.convert(bo, EsportsHotel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改商家表
     *
     * @param bo 商家表
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(EsportsHotelBo bo) {
        EsportsHotel update = MapstructUtils.convert(bo, EsportsHotel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(EsportsHotel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除商家表信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
