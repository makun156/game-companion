package com.business.service.impl;

import com.business.domain.CompanionOrder;
import com.business.domain.bo.CompanionOrderBo;
import com.business.domain.vo.CompanionOrderVo;
import com.business.mapper.CompanionOrderMapper;
import com.business.service.ICompanionOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

/**
 * 陪玩订单Service业务层处理.
 * <p>
 * 仅提供查询和详情功能，订单的创建/修改/删除由小程序端业务层处理.
 *
 * @author companion
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CompanionOrderServiceImpl implements ICompanionOrderService {

    private final CompanionOrderMapper baseMapper;

    /**
     * 查询陪玩订单详情
     */
    @Override
    public CompanionOrderVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询陪玩订单列表
     */
    @Override
    public TableDataInfo<CompanionOrderVo> queryPageList(CompanionOrderBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<CompanionOrder> lqw = buildQueryWrapper(bo);
        Page<CompanionOrderVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<CompanionOrder> buildQueryWrapper(CompanionOrderBo bo) {
        LambdaQueryWrapper<CompanionOrder> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getOrderNo()), CompanionOrder::getOrderNo, bo.getOrderNo());
        lqw.eq(bo.getUserId() != null, CompanionOrder::getUserId, bo.getUserId());
        lqw.eq(bo.getCompanionUserId() != null, CompanionOrder::getCompanionUserId, bo.getCompanionUserId());
        lqw.eq(bo.getGameId() != null, CompanionOrder::getGameId, bo.getGameId());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderStatus()), CompanionOrder::getOrderStatus, bo.getOrderStatus());
        lqw.ge(bo.getAppointmentTime() != null, CompanionOrder::getAppointmentTime, bo.getAppointmentTime());
        lqw.orderByDesc(CompanionOrder::getCreateTime);
        return lqw;
    }

}