
package com.business.service;

import com.business.domain.bo.GameCategoryBo;
import com.business.domain.vo.GameCategoryVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 游戏类别Service接口
 *
 * @author Mk
 * @date 2026-06-04
 */
public interface IGameCategoryService {

    /**
     * 查询游戏类别
     *
     * @param id 主键
     * @return 游戏类别
     */
    GameCategoryVo queryById(Long id);

    /**
     * 分页查询游戏类别列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 游戏类别分页列表
     */
    TableDataInfo<GameCategoryVo> queryPageList(GameCategoryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的游戏类别列表
     *
     * @param bo 查询条件
     * @return 游戏类别列表
     */
    List<GameCategoryVo> queryList(GameCategoryBo bo);

    /**
     * 新增游戏类别
     *
     * @param bo 游戏类别
     * @return 是否新增成功
     */
    Boolean insertByBo(GameCategoryBo bo);

    /**
     * 修改游戏类别
     *
     * @param bo 游戏类别
     * @return 是否修改成功
     */
    Boolean updateByBo(GameCategoryBo bo);

    /**
     * 校验并批量删除游戏类别信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
