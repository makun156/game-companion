package com.companion.xcx.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.business.domain.*;
import com.business.mapper.CompanionOrderMapper;
import com.business.mapper.GameCompanionUserMapper;
import com.business.mapper.UserMapper;
import com.business.service.IPayOrderService;
import com.companion.xcx.config.WechatPayConfig;
import com.companion.xcx.domain.bo.PayCreateBo;
import com.companion.xcx.domain.vo.PayCreateVo;
import com.companion.xcx.service.IXcxPayService;
import com.wechat.pay.java.core.notification.RequestParam;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.CloseOrderRequest;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import com.wechat.pay.java.service.payments.jsapi.model.QueryOrderByOutTradeNoRequest;
import com.wechat.pay.java.service.payments.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 小程序支付服务实现.
 *
 * @author companion
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxPayServiceImpl implements IXcxPayService {

    /**
     * 商户订单号正则：6-32位数字、字母或_-|*
     */
    private static final Pattern ORDER_NO_PATTERN = Pattern.compile("^[0-9a-zA-Z_\\-|*]{6,32}$");

    /**
     * 上海时区，用于格式化过期时间
     */
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    /**
     * 微信支付成功事件类型
     */
    private static final String EVENT_TRANSACTION_SUCCESS = "TRANSACTION.SUCCESS";

    private final IPayOrderService payOrderService;
    private final WechatPayConfig wechatPayConfig;
    private final UserMapper userMapper;
    private final CompanionOrderMapper companionOrderMapper;
    private final GameCompanionUserMapper companionUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayCreateVo create(PayCreateBo bo) {
        // 检查微信支付是否已初始化配置
        checkPayReady();

        // ---------- 参数校验 ----------
        if (bo == null) {
            throw new ServiceException("支付下单参数不能为空");
        }
        if (StrUtil.isBlank(bo.getDescription()) || bo.getDescription().length() > 127) {
            throw new ServiceException("商品描述不能为空且不能超过127个字符");
        }
        if (bo.getAmount() == null || bo.getAmount() <= 0 || bo.getAmount() > Integer.MAX_VALUE) {
            throw new ServiceException("支付金额必须大于0且不超过21亿元");
        }
        // 校验订单号格式（支持传入自定义订单号，为空时自动生成）
        String orderNo = StrUtil.blankToDefault(bo.getOrderNo(), generateOrderNo());
        if (!ORDER_NO_PATTERN.matcher(orderNo).matches()) {
            throw new ServiceException("商户订单号需6-32位数字、字母或_-|*");
        }

        // 从数据库获取当前登录用户的openid，不需要前端传code
        String openid = getStoredOpenid();
        Long userId = LoginHelper.getUserId();
        Date expireTime = calcExpireTime(bo.getExpireMinutes());

        // ---------- 创建或更新订单记录 ----------
        PayOrder order = payOrderService.getByOrderNo(orderNo);
        if (order == null) {
            // 订单不存在，创建新订单
            order = new PayOrder();
            order.setOrderNo(orderNo);
            order.setUserId(userId);
            order.setBizType(bo.getBizType());
            order.setBizId(bo.getBizId());
            order.setOpenid(openid);
            order.setTitle(bo.getDescription());
            order.setAmount(bo.getAmount());
            order.setStatus(PayOrderStatus.WAITING);
            order.setExpireTime(expireTime);
            payOrderService.createOrder(order);
        } else {
            // 订单已存在，校验归属和状态
            if (order.getUserId() != null && !order.getUserId().equals(userId)) {
                throw new ServiceException("无权操作该订单");
            }
            if (order.getStatus() == PayOrderStatus.PAID) {
                throw new ServiceException("订单已支付，请勿重复下单");
            }
            if (order.getStatus() == PayOrderStatus.CLOSED || order.getStatus() == PayOrderStatus.REFUNDED) {
                throw new ServiceException("订单已关闭或已退款，请重新下单");
            }
            // 更新订单信息（重新设置openid、金额、过期时间等）
            order.setOpenid(openid);
            order.setTitle(bo.getDescription());
            order.setAmount(bo.getAmount());
            order.setExpireTime(expireTime);
            payOrderService.updateOrder(order);
        }

        // ---------- 调用微信支付JSAPI下单 ----------
        PrepayRequest prepayRequest = new PrepayRequest();
        prepayRequest.setAppid(wechatPayConfig.getAppid());
        prepayRequest.setMchid(wechatPayConfig.getMerchantId());
        prepayRequest.setDescription(bo.getDescription());
        prepayRequest.setOutTradeNo(orderNo);
        prepayRequest.setTimeExpire(formatExpireTime(expireTime));
        prepayRequest.setNotifyUrl(wechatPayConfig.getNotifyUrl());

        // 设置支付金额（单位：分）
        Amount amount = new Amount();
        amount.setTotal(Math.toIntExact(bo.getAmount()));
        prepayRequest.setAmount(amount);

        // 设置用户标识
        Payer payer = new Payer();
        payer.setOpenid(openid);
        prepayRequest.setPayer(payer);

        // 调用微信支付预下单接口，获取调起支付所需的参数
        PrepayWithRequestPaymentResponse response =
            wechatPayConfig.getJsapiServiceExtension().prepayWithRequestPayment(prepayRequest);

        // 封装返回给前端的调起支付参数
        PayCreateVo vo = new PayCreateVo();
        vo.setOrderNo(orderNo);
        vo.setAppId(response.getAppId());
        vo.setTimeStamp(response.getTimeStamp());
        vo.setNonceStr(response.getNonceStr());
        vo.setPackageVal(response.getPackageVal());
        vo.setSignType(response.getSignType());
        vo.setPaySign(response.getPaySign());
        log.info("小程序支付订单创建成功，orderNo={}, amount={}分", orderNo, bo.getAmount());
        return vo;
    }

    @Override
    public PayOrder query(String orderNo) {
        // 获取当前用户的订单，校验归属
        PayOrder order = getOwnedOrder(orderNo);
        // 如果订单待支付且微信支付配置可用，则同步查询微信侧最新状态
        if (order.getStatus() == PayOrderStatus.WAITING && wechatPayConfig.isReady()) {
            try {
                Transaction transaction = queryWechatOrder(orderNo);
                if (transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
                    // 微信侧已支付，核验金额后更新本地订单为已支付
                    if (amountMatched(order, transaction)) {
                        payOrderService.markPaid(orderNo, transaction.getTransactionId(),
                            parsePayTime(transaction.getSuccessTime()));
                    }
                } else if (transaction.getTradeState() == Transaction.TradeStateEnum.CLOSED) {
                    // 微信侧已关闭，同步更新本地状态
                    payOrderService.markClosed(orderNo);
                }
            } catch (Exception e) {
                log.warn("查询微信支付订单状态失败，orderNo={}", orderNo, e);
            }
        }
        return payOrderService.getByOrderNo(orderNo);
    }

    @Override
    public Boolean close(String orderNo) {
        // 获取当前用户的订单，校验归属
        PayOrder order = getOwnedOrder(orderNo);
        if (order.getStatus() == PayOrderStatus.PAID) {
            throw new ServiceException("已支付订单不能关闭");
        }
        if (order.getStatus() == PayOrderStatus.CLOSED) {
            // 已关闭，直接返回成功
            return true;
        }
        // 如果微信支付配置可用，先查询微信侧最新状态，再发起关单
        if (wechatPayConfig.isReady()) {
            try {
                Transaction transaction = queryWechatOrder(orderNo);
                if (transaction.getTradeState() == Transaction.TradeStateEnum.SUCCESS) {
                    // 微信侧已支付，核验金额后更新本地状态，并提示不可关闭
                    if (amountMatched(order, transaction)) {
                        payOrderService.markPaid(orderNo, transaction.getTransactionId(),
                            parsePayTime(transaction.getSuccessTime()));
                    }
                    throw new ServiceException("订单已支付，不能关闭");
                }
                if (transaction.getTradeState() == Transaction.TradeStateEnum.CLOSED) {
                    // 微信侧已关闭，同步更新本地状态
                    payOrderService.markClosed(orderNo);
                    return true;
                }
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                log.warn("关单前查询微信订单状态失败，orderNo={}", orderNo, e);
            }
            // 调用微信支付关单接口
            try {
                CloseOrderRequest closeRequest = new CloseOrderRequest();
                closeRequest.setOutTradeNo(orderNo);
                closeRequest.setMchid(wechatPayConfig.getMerchantId());
                wechatPayConfig.getJsapiServiceExtension().closeOrder(closeRequest);
            } catch (Exception e) {
                log.warn("微信支付关单失败，orderNo={}", orderNo, e);
            }
        }
        // 更新本地订单状态为已关闭
        payOrderService.markClosed(orderNo);
        return true;
    }

    @Override
    public void handleNotify(String body, String serial, String timestamp, String nonce,
                             String signature, String signType) {
        // 检查微信支付是否已初始化配置
        checkPayReady();

        // 构建微信支付回调验签参数
        RequestParam requestParam = new RequestParam.Builder()
            .serialNumber(serial)
            .timestamp(timestamp)
            .nonce(nonce)
            .signature(signature)
            .signType(signType)
            .body(body)
            .build();

        // 验签并解析回调报文中的交易对象
        Transaction transaction =
            wechatPayConfig.getNotificationParser().parse(requestParam, Transaction.class);

        // 校验事件类型是否为支付成功
        String eventType = JSONUtil.parseObj(body).getStr("event_type");
        if (!EVENT_TRANSACTION_SUCCESS.equals(eventType)) {
            log.warn("微信支付回调事件类型异常: {}", eventType);
            return;
        }
        // 校验交易状态是否为成功
        if (transaction.getTradeState() != Transaction.TradeStateEnum.SUCCESS) {
            log.warn("微信支付回调交易状态不是SUCCESS，state={}", transaction.getTradeState());
            return;
        }

        // 根据订单号查询本地订单
        PayOrder order = payOrderService.getByOrderNo(transaction.getOutTradeNo());
        if (order == null) {
            throw new ServiceException("订单不存在 " + transaction.getOutTradeNo());
        }
        // 核验支付金额是否一致
        if (!amountMatched(order, transaction)) {
            throw new ServiceException("支付金额校验失败: " + transaction.getOutTradeNo());
        }

        // 更新订单为已支付（幂等处理：重复回调不会重复更新）
        boolean updated = payOrderService.markPaid(
            order.getOrderNo(), transaction.getTransactionId(),
            parsePayTime(transaction.getSuccessTime())
        );
        if (updated) {
            log.info("支付订单已标记为已支付，orderNo={}, transactionId={}", order.getOrderNo(), transaction.getTransactionId());
            // 如果有关联的陪玩订单，同步更新状态为已支付
            if (order.getCompanionOrderId() != null) {
                CompanionOrder companionOrder = new CompanionOrder();
                companionOrder.setId(order.getCompanionOrderId());
                companionOrder.setOrderStatus(CompanionOrderStatus.PAID);
                companionOrder.setPaidAmount(order.getAmount());
                companionOrderMapper.updateById(companionOrder);
                log.info("陪玩订单已同步标记为已支付，companionOrderId={}", order.getCompanionOrderId());
                // 从 Redis 查询陪玩预约时间并更新状态为已支付
                if (order.getCompanionOrderId() != null) {
                    updateScheduleStatus(order.getCompanionOrderId());
                }

            }
        } else {
            // 处理重复回调：如果订单已标记为支付成功且交易号一致，则忽略
            PayOrder latest = payOrderService.getByOrderNo(order.getOrderNo());
            if (latest != null && latest.getStatus() == PayOrderStatus.PAID
                && StrUtil.equals(latest.getTransactionId(), transaction.getTransactionId())) {
                log.info("重复的支付回调已处理，orderNo={}", order.getOrderNo());
            } else {
                log.warn("支付订单状态更新跳过，orderNo={}", order.getOrderNo());
            }
        }
    }

    @Override
    public List<PayOrder> list() {
        return payOrderService.listByUserId(LoginHelper.getUserId());
    }

    /**
     * 获取当前用户拥有的订单，校验订单归属
     */
    private PayOrder getOwnedOrder(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            throw new ServiceException("订单号不能为空");
        }
        PayOrder order = payOrderService.getByOrderNo(orderNo);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        Long userId = LoginHelper.getUserId();
        if (userId == null || !userId.equals(order.getUserId())) {
            throw new ServiceException("无权查看该订单");
        }
        return order;
    }

    /**
     * 校验本地订单金额与微信交易金额是否一致
     */
    private boolean amountMatched(PayOrder order, Transaction transaction) {
        return order.getAmount() != null
            && transaction.getAmount() != null
            && transaction.getAmount().getTotal() != null
            && order.getAmount().equals(Long.valueOf(transaction.getAmount().getTotal()));
    }

    /**
     * 查询微信侧的订单状态
     */
    private Transaction queryWechatOrder(String orderNo) {
        QueryOrderByOutTradeNoRequest request = new QueryOrderByOutTradeNoRequest();
        request.setOutTradeNo(orderNo);
        request.setMchid(wechatPayConfig.getMerchantId());
        return wechatPayConfig.getJsapiServiceExtension().queryOrderByOutTradeNo(request);
    }

    /**
     * 从数据库获取当前用户已绑定的openid.
     * 根据用户类型（普通用户 / 陪玩用户）分别查询对应表.
     */
    private String getStoredOpenid() {
        Long userId = LoginHelper.getUserId();
        UserType userType = LoginHelper.getUserType();
        String openid;
        if (userType == UserType.COMPANION_USER) {
            GameCompanionUser companionUser = companionUserMapper.selectById(userId);
            openid = companionUser == null ? null : companionUser.getOpenid();
        } else {
            User user = userMapper.selectById(userId);
            openid = user == null ? null : user.getOpenid();
        }
        if (StrUtil.isBlank(openid)) {
            throw new ServiceException("用户未绑定微信Openid，请重新登录");
        }
        return openid;
    }

    /**
     * 生成商户订单号：前缀GC + 时间戳 + 6位随机数
     */
    private String generateOrderNo() {
        return "GC" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS") + RandomUtil.randomNumbers(6);
    }

    /**
     * 计算订单过期时间，默认15分钟，范围1分钟~15天
     */
    private Date calcExpireTime(Integer expireMinutes) {
        int minutes = expireMinutes == null ? 15 : expireMinutes;
        if (minutes < 1 || minutes > 21600) {
            throw new ServiceException("支付有效时间必须在1分钟到15天之间");
        }
        return Date.from(Instant.now().plus(Duration.ofMinutes(minutes)));
    }

    /**
     * 格式化过期时间为ISO 8601格式（上海时区）
     */
    private String formatExpireTime(Date expireTime) {
        return OffsetDateTime.ofInstant(expireTime.toInstant(), SHANGHAI)
            .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * 解析微信支付成功时间，解析失败时使用当前时间
     */
    private Date parsePayTime(String successTime) {
        if (StrUtil.isBlank(successTime)) {
            return new Date();
        }
        try {
            return Date.from(OffsetDateTime.parse(successTime).toInstant());
        } catch (Exception e) {
            log.warn("解析微信支付成功时间失败: {}", successTime);
            return new Date();
        }
    }


    /**
     * 更新 Redis 预约时间状态为已支付.
     */
    private void updateScheduleStatus(Long companionOrderId) {
        // 查询陪玩订单获取预约信息
        CompanionOrder companionOrder = companionOrderMapper.selectById(companionOrderId);
        if (companionOrder == null || companionOrder.getAppointmentTime() == null) {
            return;
        }
        String key = "companion_schedule:" + companionOrder.getCompanionUserId() + ":"
            + DateUtil.format(companionOrder.getAppointmentTime(), "yyyyMMdd");
        String hKey = DateUtil.format(companionOrder.getAppointmentTime(), "HH:mm:ss");
        String cached = RedisUtils.getCacheMapValue(key, hKey);
        if (StrUtil.isNotBlank(cached)) {
            JSONObject value = JSONUtil.parseObj(cached);
            value.set("status", "PAID");
            RedisUtils.setCacheMapValue(key, hKey, value.toString());
        }
    }

    /**
     * 检查微信支付配置是否已初始化
     */
    private void checkPayReady() {
        if (!wechatPayConfig.isReady()) {
            throw new ServiceException("微信支付服务未初始化，请检查商户配置和证书");
        }
    }

}
