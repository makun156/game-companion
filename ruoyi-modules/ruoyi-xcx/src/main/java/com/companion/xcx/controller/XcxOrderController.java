package com.companion.xcx.controller;

import com.business.domain.CompanionOrder;
import com.companion.xcx.domain.bo.CompanionOrderCreateBo;
import com.companion.xcx.domain.vo.PayCreateVo;
import com.companion.xcx.service.IXcxOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序陪玩订单 Controller.
 *
 * @author companion
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/xcx/order")
public class XcxOrderController {

    private final IXcxOrderService orderService;

    /**
     * 创建陪玩订单并发起支付.
     * <p>
     * 业务流程：创建陪玩订单 → 创建支付记录 → 调微信JSAPI预下单 → 返回调起支付参数
     */
    @Log(title = "小程序陪玩下单", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public R<PayCreateVo> createOrder(@RequestBody CompanionOrderCreateBo bo) {
        return R.ok(orderService.createOrder(bo));
    }

    /**
     * 查询当前用户的陪玩订单列表.
     *
     * @param status 订单状态过滤（可选），如 PENDING_PAYMENT / PAID / IN_PROGRESS / COMPLETED
     */
    @GetMapping("/list")
    public R<List<CompanionOrder>> listOrders(@RequestParam(required = false) String status) {
        return R.ok(orderService.listOrders(status));
    }

    /**
     * 查询陪玩订单详情.
     *
     * @param orderNo 订单号
     */
    @GetMapping("/detail/{orderNo}")
    public R<CompanionOrder> getDetail(@PathVariable String orderNo) {
        return R.ok(orderService.getOrderDetail(orderNo));
    }

    /**
     * 重新支付
     *
     * @param orderNo 陪玩订单号
     */
    @Log(title = "小程序重新支付", businessType = BusinessType.INSERT)
    @PostMapping("/repay/{orderNo}")
    public R<PayCreateVo> repayOrder(@PathVariable String orderNo) {
        return R.ok(orderService.repayOrder(orderNo));
    }
    /**
     * 取消陪玩订单（仅限 PENDING_PAYMENT 状态）.
     *
     * @param orderNo 订单号
     */
    @Log(title = "小程序取消订单", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{orderNo}")
    public R<Boolean> cancelOrder(@PathVariable String orderNo) {
        return R.ok(orderService.cancelOrder(orderNo));
    }

    /**
     * 开始服务（陪玩端）：PAID → IN_PROGRESS.
     */
    @Log(title = "陪玩开始服务", businessType = BusinessType.UPDATE)
    @PostMapping("/start-service/{orderNo}")
    public R<Boolean> startService(@PathVariable String orderNo) {
        return R.ok(orderService.startService(orderNo));
    }

    /**
     * 结束服务（陪玩端）：IN_PROGRESS → COMPLETED.
     */
    @Log(title = "陪玩结束服务", businessType = BusinessType.UPDATE)
    @PostMapping("/complete-service/{orderNo}")
    public R<Boolean> completeService(@PathVariable String orderNo) {
        return R.ok(orderService.completeService(orderNo));
    }

    /**
     * 申请退款（用户端）：PAID → REFUNDING.
     */
    @Log(title = "陪玩申请退款", businessType = BusinessType.UPDATE)
    @PostMapping("/refund/{orderNo}")
    public R<Boolean> requestRefund(@PathVariable String orderNo, @RequestParam(required = false) String reason) {
        return R.ok(orderService.requestRefund(orderNo, reason));
    }

    /**
     * 修改服务时间（用户端）.
     */
    @Log(title = "陪玩修改服务时间", businessType = BusinessType.UPDATE)
    @PostMapping("/reschedule")
    public R<Boolean> rescheduleOrder(@RequestParam String orderNo, @RequestParam String appointmentTime) {
        return R.ok(orderService.rescheduleOrder(orderNo, appointmentTime));
    }

}
