package com.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.business.domain.Games;
import com.business.domain.bo.GamesBo;
import com.business.domain.vo.GamesVo;
import com.business.mapper.TGamesMapper;
import com.business.service.IGamesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 游戏列表Service业务层处理
 *
 * @author Lion Li
 * @date 2026-06-02
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GamesServiceImpl implements IGamesService {

    private final TGamesMapper baseMapper;

    /**
     * 查询游戏列表
     *
     * @param id 主键
     * @return 游戏列表
     */
    @Override
    public GamesVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询游戏列表列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 游戏列表分页列表
     */
    @Override
    public TableDataInfo<GamesVo> queryPageList(GamesBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Games> lqw = buildQueryWrapper(bo);
        Page<GamesVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的游戏列表列表
     *
     * @param bo 查询条件
     * @return 游戏列表列表
     */
    @Override
    public List<GamesVo> queryList(GamesBo bo) {
        LambdaQueryWrapper<Games> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Games> buildQueryWrapper(GamesBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Games> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Games::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), Games::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), Games::getCategory, bo.getCategory());
        lqw.eq(bo.getStatus() != null, Games::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增游戏列表
     *
     * @param bo 游戏列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(GamesBo bo) {
        Games add = MapstructUtils.convert(bo, Games.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改游戏列表
     *
     * @param bo 游戏列表
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(GamesBo bo) {
        Games update = MapstructUtils.convert(bo, Games.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Games entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除游戏列表信息
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
