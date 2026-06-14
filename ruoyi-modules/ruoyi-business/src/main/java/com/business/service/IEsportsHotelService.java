package com.business.service;

import com.business.domain.vo.EsportsHotelVo;
import com.business.domain.bo.EsportsHotelBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 商家表Service接口
 *
 * @author Mk
 * @date 2026-06-14
 */
public interface IEsportsHotelService {

    /**
     * 查询商家表
     *
     * @param id 主键
     * @return 商家表
     */
    EsportsHotelVo queryById(Long id);

    /**
     * 分页查询商家表列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商家表分页列表
     */
    TableDataInfo<EsportsHotelVo> queryPageList(EsportsHotelBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的商家表列表
     *
     * @param bo 查询条件
     * @return 商家表列表
     */
    List<EsportsHotelVo> queryList(EsportsHotelBo bo);

    /**
     * 新增商家表
     *
     * @param bo 商家表
     * @return 是否新增成功
     */
    Boolean insertByBo(EsportsHotelBo bo);

    /**
     * 修改商家表
     *
     * @param bo 商家表
     * @return 是否修改成功
     */
    Boolean updateByBo(EsportsHotelBo bo);

    /**
     * 校验并批量删除商家表信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
