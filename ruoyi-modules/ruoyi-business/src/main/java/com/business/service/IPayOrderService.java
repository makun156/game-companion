package com.business.service;

import com.business.domain.PayOrder;

import java.util.Date;
import java.util.List;

/**
 * 支付订单服务接口.
 *
 * @author companion
 */
public interface IPayOrderService {

    /**
     * 创建支付订单.
     *
     * @param order 订单信息
     * @return 创建后的订单
     */
    PayOrder createOrder(PayOrder order);

    /**
     * 更新支付订单.
     *
     * @param order 订单信息
     * @return 是否更新成功
     */
    boolean updateOrder(PayOrder order);

    /**
     * 根据订单号查询订单.
     *
     * @param orderNo 商户订单号
     * @return 订单信息
     */
    PayOrder getByOrderNo(String orderNo);

    /**
     * 查询用户的所有支付订单.
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<PayOrder> listByUserId(Long userId);

    /**
     * 标记订单为已支付.
     *
     * @param orderNo       商户订单号
     * @param transactionId 微信交易号
     * @param payTime       支付时间
     * @return 是否标记成功
     */
    boolean markPaid(String orderNo, String transactionId, Date payTime);

    /**
     * 标记订单为已关闭.
     *
     * @param orderNo 商户订单号
     * @return 是否关闭成功
     */
    boolean markClosed(String orderNo);

}
