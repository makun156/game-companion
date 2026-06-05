package com.business.service;

import com.business.domain.bo.CityBo;
import com.business.domain.vo.CityVo;

import java.util.Collection;
import java.util.List;

/**
 * 区域Service接口
 *
 * @author Mk
 * @date 2026-06-03
 */
public interface ICityService {

    /**
     * 查询区域
     *
     * @param id 主键
     * @return 区域
     */
    CityVo queryById(Long id);


    /**
     * 查询符合条件的区域列表
     *
     * @param bo 查询条件
     * @return 区域列表
     */
    List<CityVo> queryList(CityBo bo);

    /**
     * 新增区域
     *
     * @param bo 区域
     * @return 是否新增成功
     */
    Boolean insertByBo(CityBo bo);

    /**
     * 修改区域
     *
     * @param bo 区域
     * @return 是否修改成功
     */
    Boolean updateByBo(CityBo bo);

    /**
     * 校验并批量删除区域信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
