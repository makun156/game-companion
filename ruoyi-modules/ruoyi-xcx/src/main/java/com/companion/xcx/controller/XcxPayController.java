package com.companion.xcx.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.business.domain.PayOrder;
import com.companion.xcx.domain.bo.PayCreateBo;
import com.companion.xcx.domain.vo.PayCreateVo;
import com.companion.xcx.service.IXcxPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 小程序微信支付-controller
 *
 * @author companion
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/xcx/pay")
public class XcxPayController {

    private final IXcxPayService payService;

    /**
     * 创建JSAPI支付订单，返回wx.requestPayment调起支付所需参数
     */
    @Log(title = "小程序支付下单", businessType = BusinessType.INSERT)
    @PostMapping("/jsapi")
    public R<PayCreateVo> jsapiPay(@RequestBody PayCreateBo bo) {
        return R.ok(payService.create(bo));
    }

    /**
     * 查询订单信息，必要时同步微信支付侧订单状态
     */
    @GetMapping("/query/{orderNo}")
    public R<PayOrder> query(@PathVariable String orderNo) {
        return R.ok(payService.query(orderNo));
    }

    /**
     * 关闭未支付订单
     */
    @Log(title = "小程序支付关单", businessType = BusinessType.UPDATE)
    @PostMapping("/close/{orderNo}")
    public R<Boolean> close(@PathVariable String orderNo) {
        return R.ok(payService.close(orderNo));
    }

    /**
     * 查询当前用户的支付订单列表
     */
    @GetMapping("/list")
    public R<List<PayOrder>> list() {
        return R.ok(payService.list());
    }

    /**
     * 微信支付APIv3异步通知回调
     */
    @SaIgnore
    @PostMapping("/notify")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 读取微信支付回调请求体
        String body;
        try {
            body = request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            log.error("读取微信支付回调请求体失败", e);
            writeNotifyFail(response, HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            // 解析并处理支付回调通知（验签、核验金额、更新订单状态）
            payService.handleNotify(
                body,
                request.getHeader("Wechatpay-Serial"),
                request.getHeader("Wechatpay-Timestamp"),
                request.getHeader("Wechatpay-Nonce"),
                request.getHeader("Wechatpay-Signature"),
                request.getHeader("Wechatpay-Signature-Type"));
            // 通知微信支付侧处理成功
            response.setStatus(HttpServletResponse.SC_OK);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"SUCCESS\",\"message\":\"成功\"}");
        } catch (Exception e) {
            log.error("处理微信支付回调失败", e);
            writeNotifyFail(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 写入支付回调失败响应
     */
    private void writeNotifyFail(HttpServletResponse response, int status) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.getWriter().write("{\"code\":\"FAIL\",\"message\":\"处理失败\"}");
    }

}
