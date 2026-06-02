package com.business.service;

import com.business.domain.bo.GamesBo;
import com.business.domain.vo.GamesVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 游戏列表Service接口
 *
 * @author Lion Li
 * @date 2026-06-02
 */
public interface IGamesService {

    /**
     * 查询游戏列表
     *
     * @param id 主键
     * @return 游戏列表
     */
    GamesVo queryById(Long id);

    /**
     * 分页查询游戏列表列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 游戏列表分页列表
     */
    TableDataInfo<GamesVo> queryPageList(GamesBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的游戏列表列表
     *
     * @param bo 查询条件
     * @return 游戏列表列表
     */
    List<GamesVo> queryList(GamesBo bo);

    /**
     * 新增游戏列表
     *
     * @param bo 游戏列表
     * @return 是否新增成功
     */
    Boolean insertByBo(GamesBo bo);

    /**
     * 修改游戏列表
     *
     * @param bo 游戏列表
     * @return 是否修改成功
     */
    Boolean updateByBo(GamesBo bo);

    /**
     * 校验并批量删除游戏列表信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
