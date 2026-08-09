package com.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.PayOrder;
import com.business.domain.PayOrderStatus;
import com.business.mapper.PayOrderMapper;
import com.business.service.IPayOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 支付订单服务实现类.
 *
 * @author companion
 */
@RequiredArgsConstructor
@Service
public class PayOrderServiceImpl implements IPayOrderService {

    private final PayOrderMapper baseMapper;

    @Override
    public PayOrder createOrder(PayOrder order) {
        // 设置初始状态为"待支付"
        order.setStatus(PayOrderStatus.WAITING);
        baseMapper.insert(order);
        return order;
    }

    @Override
    public boolean updateOrder(PayOrder order) {
        return baseMapper.updateById(order) > 0;
    }

    @Override
    public PayOrder getByOrderNo(String orderNo) {
        // 根据唯一订单号查询
        return baseMapper.selectOne(
            Wrappers.lambdaQuery(PayOrder.class).eq(PayOrder::getOrderNo, orderNo));
    }

    @Override
    public List<PayOrder> listByUserId(Long userId) {
        // 查询指定用户的所有订单，按创建时间倒序排列
        return baseMapper.selectList(
            Wrappers.lambdaQuery(PayOrder.class)
                .eq(PayOrder::getUserId, userId)
                .orderByDesc(PayOrder::getCreateTime));
    }

    @Override
    public boolean markPaid(String orderNo, String transactionId, Date payTime) {
        // 只更新"待支付"状态的订单，避免重复覆盖已支付订单
        PayOrder update = new PayOrder();
        update.setStatus(PayOrderStatus.PAID);
        update.setTransactionId(transactionId);
        update.setPayTime(payTime);
        return baseMapper.update(update,
            Wrappers.lambdaUpdate(PayOrder.class)
                .eq(PayOrder::getOrderNo, orderNo)
                .eq(PayOrder::getStatus, PayOrderStatus.WAITING)) > 0;
    }

    @Override
    public boolean markClosed(String orderNo) {
        // 只关闭"待支付"状态的订单，防止误关已支付订单
        PayOrder update = new PayOrder();
        update.setStatus(PayOrderStatus.CLOSED);
        update.setCloseTime(new Date());
        return baseMapper.update(update,
            Wrappers.lambdaUpdate(PayOrder.class)
                .eq(PayOrder::getOrderNo, orderNo)
                .eq(PayOrder::getStatus, PayOrderStatus.WAITING)) > 0;
    }

}
