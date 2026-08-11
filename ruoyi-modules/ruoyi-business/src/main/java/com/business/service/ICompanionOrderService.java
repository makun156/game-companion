package com.business.service;

import com.business.domain.vo.CompanionOrderVo;
import com.business.domain.bo.CompanionOrderBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

/**
 * 陪玩订单Service接口.
 * <p>
 * 仅提供查询和详情功能，订单的创建/修改/删除由小程序端业务层处理.
 *
 * @author companion
 */
public interface ICompanionOrderService {

    /**
     * 查询陪玩订单详情
     */
    CompanionOrderVo queryById(Long id);

    /**
     * 分页查询陪玩订单列表
     */
    TableDataInfo<CompanionOrderVo> queryPageList(CompanionOrderBo bo, PageQuery pageQuery);

}