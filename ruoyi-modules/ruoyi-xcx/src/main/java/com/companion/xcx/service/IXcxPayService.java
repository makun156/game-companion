package com.companion.xcx.service;

import com.business.domain.PayOrder;
import com.companion.xcx.domain.bo.PayCreateBo;
import com.companion.xcx.domain.vo.PayCreateVo;

import java.util.List;

/**
 * 小程序支付服务接口
 *
 * @author companion
 */
public interface IXcxPayService {

    /**
     * 创建支付订单，返回wx.requestPayment调起支付所需参数
     *
     * @param bo 支付下单请求参数
     * @return wx.requestPayment调起支付所需参数
     */
    PayCreateVo create(PayCreateBo bo);

    /**
     * 查询订单信息，未支付订单会同步微信支付侧最新状态
     *
     * @param orderNo 商户订单号
     * @return 订单信息
     */
    PayOrder query(String orderNo);

    /**
     * 关闭未支付订单
     *
     * @param orderNo 商户订单号
     * @return 是否关闭成功
     */
    Boolean close(String orderNo);

    /**
     * 处理微信支付APIv3异步通知
     *
     * @param body      通知请求体
     * @param serial    微信平台证书序列号
     * @param timestamp 时间戳
     * @param nonce     随机串
     * @param signature 签名值
     * @param signType  签名类型
     */
    void handleNotify(String body, String serial, String timestamp, String nonce,
                      String signature, String signType);

    /**
     * 查询当前用户的支付订单列表
     *
     * @return 订单列表
     */
    List<PayOrder> list();

}
