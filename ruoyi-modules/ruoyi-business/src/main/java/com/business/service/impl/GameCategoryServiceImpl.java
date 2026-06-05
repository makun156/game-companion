package com.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.business.domain.GameCategory;
import com.business.domain.bo.GameCategoryBo;
import com.business.domain.vo.GameCategoryVo;
import com.business.mapper.GameCategoryMapper;
import com.business.service.IGameCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 游戏类别Service业务层处理
 *
 * @author Mk
 * @date 2026-06-04
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GameCategoryServiceImpl implements IGameCategoryService {

    private final GameCategoryMapper baseMapper;

    /**
     * 查询游戏类别
     *
     * @param id 主键
     * @return 游戏类别
     */
    @Override
    public GameCategoryVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询游戏类别列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 游戏类别分页列表
     */
    @Override
    public TableDataInfo<GameCategoryVo> queryPageList(GameCategoryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<GameCategory> lqw = buildQueryWrapper(bo);
        Page<GameCategoryVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的游戏类别列表
     *
     * @param bo 查询条件
     * @return 游戏类别列表
     */
    @Override
    public List<GameCategoryVo> queryList(GameCategoryBo bo) {
        LambdaQueryWrapper<GameCategory> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<GameCategory> buildQueryWrapper(GameCategoryBo bo) {
        LambdaQueryWrapper<GameCategory> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(GameCategory::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), GameCategory::getName, bo.getName());
        lqw.eq(bo.getSort() != null, GameCategory::getSort, bo.getSort());
        lqw.eq(bo.getStatus() != null, GameCategory::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增游戏类别
     *
     * @param bo 游戏类别
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(GameCategoryBo bo) {
        GameCategory add = MapstructUtils.convert(bo, GameCategory.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        //if (flag) {
        //    bo.setId(add.getId());
        //}
        return flag;
    }

    /**
     * 修改游戏类别
     *
     * @param bo 游戏类别
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(GameCategoryBo bo) {
        GameCategory update = MapstructUtils.convert(bo, GameCategory.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(GameCategory entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除游戏类别信息
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
