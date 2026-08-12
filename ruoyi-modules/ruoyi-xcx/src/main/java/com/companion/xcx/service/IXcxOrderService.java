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


    /**
     * 开始服务（陪玩端）：PAID → IN_PROGRESS.
     * <p>
     * 陪玩点击"开始服务"，记录实际上单时间.
     * 仅限 PAID 状态的订单，且调用者必须是该陪玩本人.
     *
     * @param orderNo 陪玩订单号
     * @return 是否成功
     */
    Boolean startService(String orderNo);

    /**
     * 结束服务（陪玩端）：IN_PROGRESS → COMPLETED.
     * <p>
     * 陪玩点击"结束服务"，记录实际结束时间，释放预约时间段.
     * 仅限 IN_PROGRESS 状态的订单，且调用者必须是该陪玩本人.
     *
     * @param orderNo 陪玩订单号
     * @return 是否成功
     */
    Boolean completeService(String orderNo);

    /**
     * 申请退款（用户端）：PAID → REFUNDING.
     * <p>
     * TODO: 暂未实现，后续接入退款流程.
     *
     * @param orderNo 陪玩订单号
     * @param reason  退款原因
     * @return 是否成功
     */
    Boolean requestRefund(String orderNo, String reason);

    /**
     * 修改服务时间（用户端）：重新预约时间段.
     * <p>
     * 仅限 PENDING_PAYMENT 和 PAID 状态的订单.
     * 会检查新时间段是否有冲突，释放旧时间段，写入新时间段.
     *
     * @param orderNo            陪玩订单号
     * @param newAppointmentTime 新的预约时间，格式 yyyy-MM-dd HH:mm:ss
     * @return 是否成功
     */
    Boolean rescheduleOrder(String orderNo, String newAppointmentTime);
}