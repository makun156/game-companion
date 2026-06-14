package com.business.service;

import com.business.domain.vo.GameCompanionUserVo;
import com.business.domain.bo.GameCompanionUserBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 陪玩表Service接口
 *
 * @author Mk
 * @date 2026-06-10
 */
public interface IGameCompanionUserService {

    /**
     * 查询陪玩表
     *
     * @param id 主键
     * @return 陪玩表
     */
    GameCompanionUserVo queryById(Long id);

    /**
     * 分页查询陪玩表列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 陪玩表分页列表
     */
    TableDataInfo<GameCompanionUserVo> queryPageList(GameCompanionUserBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的陪玩表列表
     *
     * @param bo 查询条件
     * @return 陪玩表列表
     */
    List<GameCompanionUserVo> queryList(GameCompanionUserBo bo);

    /**
     * 新增陪玩表
     *
     * @param bo 陪玩表
     * @return 是否新增成功
     */
    Boolean insertByBo(GameCompanionUserBo bo);

    /**
     * 修改陪玩表
     *
     * @param bo 陪玩表
     * @return 是否修改成功
     */
    Boolean updateByBo(GameCompanionUserBo bo);

    /**
     * 校验并批量删除陪玩表信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
