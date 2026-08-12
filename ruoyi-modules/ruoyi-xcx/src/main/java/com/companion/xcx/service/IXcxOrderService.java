package com.companion.xcx.service;

import com.business.domain.CompanionOrder;
import com.companion.xcx.domain.bo.CompanionOrderCreateBo;
import com.companion.xcx.domain.vo.PayCreateVo;

import java.util.List;

/**
 * 小程序陪玩订单服务接口.
 *
 * @author companion
 */
public interface IXcxOrderService {

    /**
     * 创建陪玩订单并发起支付.
     * <p>
     * 业务流程：创建陪玩订单 → 创建支付记录 → 调微信JSAPI预下单 → 返回调起支付参数
     */
    PayCreateVo createOrder(CompanionOrderCreateBo bo);

    /**
     * 重新支付：获取缓存的预支付参数，或重新调用微信JSAPI预下单.
     * <p>
     * 用户关闭支付窗口后，在订单列表中点击"重新支付"时调用.
     *
     * @param orderNo 陪玩订单号
     * @return 调起微信支付所需参数
     */
    PayCreateVo repayOrder(String orderNo);

    /**
     * 查询当前用户的陪玩订单列表.
     */
    List<CompanionOrder> listOrders(String status);

    /**
     * 查询陪玩订单详情.
     */
    CompanionOrder getOrderDetail(String orderNo);

    /**
     * 取消陪玩订单（仅限 PENDING_PAYMENT 状态）.
     */
    Boolean cancelOrder(String orderNo);

}