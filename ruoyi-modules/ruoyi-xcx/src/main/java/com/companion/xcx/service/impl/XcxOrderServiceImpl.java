package com.companion.xcx.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.CompanionOrder;
import com.business.domain.CompanionOrderStatus;
import com.business.domain.GameCompanionUser;
import com.business.domain.PayOrder;
import com.business.domain.PayOrderStatus;
import com.business.domain.User;
import com.business.mapper.CompanionOrderMapper;
import com.business.mapper.GameCompanionUserMapper;
import com.business.mapper.UserMapper;
import com.business.service.IPayOrderService;
import com.companion.xcx.config.WechatPayConfig;
import com.companion.xcx.domain.bo.CompanionOrderCreateBo;
import com.companion.xcx.domain.vo.PayCreateVo;
import com.companion.xcx.service.IXcxOrderService;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayWithRequestPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.enums.UserType;
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
import java.util.Objects;

/**
 * 小程序陪玩订单服务实现.
 *
 * @author companion
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class XcxOrderServiceImpl implements IXcxOrderService {

    /** 陪玩业务类型常量，用于 PayOrder.bizType */
    private static final String BIZ_TYPE_COMPANION_ORDER = "COMPANION_ORDER";

    /** 上海时区，用于格式化过期时间 */
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    private final CompanionOrderMapper companionOrderMapper;
    private final IPayOrderService payOrderService;
    private final WechatPayConfig wechatPayConfig;
    private final UserMapper userMapper;
    private final GameCompanionUserMapper companionUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayCreateVo createOrder(CompanionOrderCreateBo bo) {
        // 校验参数
        if (bo == null) {
            throw new ServiceException("下单参数不能为空");
        }
        if (bo.getCompanionUserId() == null) {
            throw new ServiceException("请选择陪玩");
        }
        if (bo.getTotalAmount() == null || bo.getTotalAmount() <= 0) {
            throw new ServiceException("订单金额不合法");
        }

        // 根据当前登录用户ID查库获取openid，前端无需传code
        String openid = getStoredOpenid();

        // 1. 创建陪玩订单
        CompanionOrder companionOrder = new CompanionOrder();
        companionOrder.setOrderNo(generateOrderNo());
        companionOrder.setUserId(LoginHelper.getUserId());
        companionOrder.setCompanionUserId(bo.getCompanionUserId());
        companionOrder.setGameId(bo.getGameId());
        companionOrder.setGameLevelId(bo.getGameLevelId());
        companionOrder.setDuration(bo.getDuration());
        companionOrder.setUnitPrice(bo.getUnitPrice());
        companionOrder.setTotalAmount(bo.getTotalAmount());
        companionOrder.setPaidAmount(0L);
        companionOrder.setRefundAmount(0L);
        companionOrder.setOrderStatus(CompanionOrderStatus.PENDING_PAYMENT);
        companionOrder.setRemark(bo.getRemark());
        companionOrderMapper.insert(companionOrder);

        // 2. 创建支付订单，关联陪玩订单
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderNo("GC" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS") + RandomUtil.randomNumbers(6));
        payOrder.setUserId(LoginHelper.getUserId());
        payOrder.setOpenid(openid);
        payOrder.setCompanionOrderId(companionOrder.getId());
        payOrder.setBizType(BIZ_TYPE_COMPANION_ORDER);
        payOrder.setBizId(companionOrder.getId());
        payOrder.setTitle(getOrderTitle(bo));
        payOrder.setAmount(bo.getTotalAmount());
        payOrder.setStatus(PayOrderStatus.WAITING);
        payOrder.setExpireTime(calcExpireTime(bo.getExpireMinutes()));
        payOrderService.createOrder(payOrder);

        // 3. 调用微信支付 JSAPI 预下单
        PrepayRequest prepayRequest = new PrepayRequest();
        prepayRequest.setAppid(wechatPayConfig.getAppid());
        prepayRequest.setMchid(wechatPayConfig.getMerchantId());
        prepayRequest.setDescription(payOrder.getTitle());
        prepayRequest.setOutTradeNo(payOrder.getOrderNo());
        prepayRequest.setTimeExpire(formatExpireTime(payOrder.getExpireTime()));
        prepayRequest.setNotifyUrl(wechatPayConfig.getNotifyUrl());

        Amount amount = new Amount();
        amount.setTotal(Math.toIntExact(bo.getTotalAmount()));
        prepayRequest.setAmount(amount);

        Payer payer = new Payer();
        payer.setOpenid(openid);
        prepayRequest.setPayer(payer);

        // 调微信支付预下单
        PrepayWithRequestPaymentResponse response = wechatPayConfig.getJsapiServiceExtension().prepayWithRequestPayment(prepayRequest);

        // 4. 封装返回参数
        PayCreateVo vo = new PayCreateVo();
        vo.setOrderNo(payOrder.getOrderNo());
        vo.setAppId(response.getAppId());
        vo.setTimeStamp(response.getTimeStamp());
        vo.setNonceStr(response.getNonceStr());
        vo.setPackageVal(response.getPackageVal());
        vo.setSignType(response.getSignType());
        vo.setPaySign(response.getPaySign());

        log.info("陪玩订单创建成功，orderNo={}, amount={}分, companionUserId={}",
            companionOrder.getOrderNo(), bo.getTotalAmount(), bo.getCompanionUserId());
        return vo;
    }

    @Override
    public List<CompanionOrder> listOrders(String status) {
        Long userId = LoginHelper.getUserId();
        LambdaQueryWrapper<CompanionOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CompanionOrder::getUserId, userId);
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(CompanionOrder::getOrderStatus, status);
        }
        wrapper.orderByDesc(CompanionOrder::getCreateTime);
        return companionOrderMapper.selectList(wrapper);
    }

    @Override
    public CompanionOrder getOrderDetail(String orderNo) {
        LambdaQueryWrapper<CompanionOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CompanionOrder::getOrderNo, orderNo);
        wrapper.eq(CompanionOrder::getUserId, LoginHelper.getUserId());
        CompanionOrder order = companionOrderMapper.selectOne(wrapper);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(String orderNo) {
        LambdaQueryWrapper<CompanionOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CompanionOrder::getOrderNo, orderNo);
        wrapper.eq(CompanionOrder::getUserId, LoginHelper.getUserId());
        CompanionOrder order = companionOrderMapper.selectOne(wrapper);
        if (order == null) {
            throw new ServiceException("订单不存在");
        }
        if (order.getOrderStatus() != CompanionOrderStatus.PENDING_PAYMENT) {
            throw new ServiceException("当前订单状态不允许取消");
        }
        // 更新陪玩订单状态为已取消
        order.setOrderStatus(CompanionOrderStatus.CANCELLED);
        order.setCancelTime(new Date());
        companionOrderMapper.updateById(order);

        // 同时关闭关联的支付订单
        payOrderService.markClosed(order.getOrderNo());

        log.info("陪玩订单已取消，orderNo={}", orderNo);
        return true;
    }

    /**
     * 从数据库获取当前登录用户的 openid.
     * 根据用户类型（普通用户 / 陪玩用户）分别查询对应表.
     */
    private String getStoredOpenid() {
        Long userId = LoginHelper.getUserId();
        UserType userType = LoginHelper.getUserType();
        String openid;
        if (userType == UserType.COMPANION_USER) {
            GameCompanionUser companionUser = companionUserMapper.selectById(userId);
            if (companionUser == null) {
                throw new ServiceException("陪玩用户不存在,无法下单");
            }
            openid =companionUser.getOpenid();
        } else {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new ServiceException("用户不存在,无法下单");
            }
            openid =user.getOpenid();
        }
        if (StrUtil.isBlank(openid)) {
            throw new ServiceException("用户未绑定微信openid，请重新登录");
        }
        return openid;
    }

    /**
     * 生成陪玩订单号：CO + 时间戳 + 6位随机数
     */
    private String generateOrderNo() {
        return "CO" + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS") + RandomUtil.randomNumbers(6);
    }

    /**
     * 生成订单标题
     */
    private String getOrderTitle(CompanionOrderCreateBo bo) {
        if (bo.getDuration() != null) {
            return "游戏陪玩服务 - " + bo.getDuration() + "小时";
        }
        return "游戏陪玩服务";
    }

    /**
     * 计算订单过期时间，默认15分钟
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

}
