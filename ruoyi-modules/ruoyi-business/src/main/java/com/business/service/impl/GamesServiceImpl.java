package com.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.business.domain.Game;
import com.business.domain.bo.GameBo;
import com.business.domain.vo.GameVo;
import com.business.mapper.GamesMapper;
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

    private final GamesMapper baseMapper;

    /**
     * 查询游戏列表
     *
     * @param id 主键
     * @return 游戏列表
     */
    @Override
    public GameVo queryById(Long id){
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
    public TableDataInfo<GameVo> queryPageList(GameBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Game> lqw = buildQueryWrapper(bo);
        Page<GameVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的游戏列表列表
     *
     * @param bo 查询条件
     * @return 游戏列表列表
     */
    @Override
    public List<GameVo> queryList(GameBo bo) {
        LambdaQueryWrapper<Game> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Game> buildQueryWrapper(GameBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Game> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Game::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), Game::getName, bo.getName());
        lqw.eq(bo.getCategoryId()!=null, Game::getCategoryId, bo.getCategoryId());
        lqw.eq(bo.getStatus() != null, Game::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增游戏列表
     *
     * @param bo 游戏列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(GameBo bo) {
        Game add = MapstructUtils.convert(bo, Game.class);
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
    public Boolean updateByBo(GameBo bo) {
        Game update = MapstructUtils.convert(bo, Game.class);
        return baseMapper.updateById(update) > 0;
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
