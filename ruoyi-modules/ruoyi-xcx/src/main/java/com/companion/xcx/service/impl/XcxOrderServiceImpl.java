package com.companion.xcx.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.CompanionOrder;
import com.business.domain.CompanionOrderStatus;
import com.business.domain.GameCompanionUser;
import com.business.domain.WorkStatus;
import com.business.domain.PayOrder;
import com.business.domain.PayOrderStatus;
import com.business.domain.User;
import com.business.mapper.CompanionOrderMapper;
import com.business.mapper.PayOrderMapper;
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
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    /**
     * 陪玩业务类型常量，用于 PayOrder.bizType
     */
    private static final String BIZ_TYPE_COMPANION_ORDER = "COMPANION_ORDER";

    private static final String PREPARE_ORDER_KEY = "prepare_order:";

    /**
     * 上海时区，用于格式化过期时间
     */
    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    private final CompanionOrderMapper companionOrderMapper;
    private final PayOrderMapper payOrderMapper;
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
        // 校验预约时间并检测冲突
        Date appointmentTime = parseAppointmentTime(bo.getAppointmentTime());
        checkTimeSlotConflict(bo.getCompanionUserId(), appointmentTime, bo.getDuration(), null);

        // 根据当前登录用户ID查库获取openid，前端无需传code
        String openid = getStoredOpenid();

        String buildOrderNo = generateOrderNo("CO");
        // 1. 创建陪玩订单
        CompanionOrder companionOrder = new CompanionOrder();
        companionOrder.setOrderNo(buildOrderNo);
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
        companionOrder.setAppointmentTime(appointmentTime);
        companionOrderMapper.insert(companionOrder);

        // 2. 创建支付订单，关联陪玩订单
        PayOrder payOrder = new PayOrder();
        payOrder.setOrderNo(buildOrderNo);
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


        // 3.5 写入预约时间到 Redis
        addSchedule(bo.getCompanionUserId(), appointmentTime, bo.getDuration(), companionOrder.getId());

        // 4. 调用微信支付 JSAPI 预下单
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
        RedisUtils.setCacheObject(PREPARE_ORDER_KEY + payOrder.getOrderNo(), JSONUtil.toJsonStr(vo), Duration.ofMinutes(15));
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

        // 从 Redis 移除预约时间
        removeSchedule(order.getCompanionUserId(), order.getAppointmentTime());
        RedisUtils.deleteObject(PREPARE_ORDER_KEY + order.getOrderNo());

        // 更新陪玩工作状态为 接单中
        GameCompanionUser companion = new GameCompanionUser();
        companion.setId(order.getCompanionUserId());
        companion.setWorkStatus(WorkStatus.AVAILABLE);
        companionUserMapper.updateById(companion);
        log.info("陪玩工作状态已更新为 AVAILABLE，companionUserId={}", order.getCompanionUserId());

        log.info("陪玩订单已取消，orderNo={}", orderNo);
        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public PayCreateVo repayOrder(String orderNo) {
        // 1. 查询陪玩订单，校验归属
        LambdaQueryWrapper<CompanionOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CompanionOrder::getOrderNo, orderNo);
        wrapper.eq(CompanionOrder::getUserId, LoginHelper.getUserId());
        CompanionOrder companionOrder = companionOrderMapper.selectOne(wrapper);
        if (companionOrder == null) {
            throw new ServiceException("订单不存在");
        }
        // 仅允许重新支付待支付状态的订单
        if (companionOrder.getOrderStatus() != CompanionOrderStatus.PENDING_PAYMENT) {
            throw new ServiceException("当前订单状态不允许重新支付");
        }

        // 2.5 重新检查预约时间是否有效（Redis 可能已过期，需要重新写入）
        // 先移除旧的预约记录（如果有冲突的会被清理）
        checkTimeSlotConflict(companionOrder.getCompanionUserId(), companionOrder.getAppointmentTime(), companionOrder.getDuration(), companionOrder.getId());
        // 重新写入预约时间（Redis TTL 可能已过期）
        addSchedule(companionOrder.getCompanionUserId(), companionOrder.getAppointmentTime(), companionOrder.getDuration(), companionOrder.getId());
        // 2. 查询关联的支付订单
        LambdaQueryWrapper<PayOrder> payWrapper = Wrappers.lambdaQuery();
        payWrapper.eq(PayOrder::getCompanionOrderId, companionOrder.getId());
        PayOrder payOrder = payOrderMapper.selectOne(payWrapper);
        if (payOrder == null) {
            throw new ServiceException("支付订单不存在");
        }
        if (payOrder.getStatus() == PayOrderStatus.PAID) {
            throw new ServiceException("订单已支付，请勿重复支付");
        }
        if (payOrder.getStatus() == PayOrderStatus.REFUNDED) {
            throw new ServiceException("订单已退款");
        }

        // 3. 检查订单是否已过期
        if (payOrder.getExpireTime() != null && payOrder.getExpireTime().before(new Date())) {
            // 过期了，更新支付订单状态为已关闭，提示用户重新下单
            payOrderService.markClosed(payOrder.getOrderNo());
            companionOrder.setOrderStatus(CompanionOrderStatus.EXPIRED);
            companionOrderMapper.updateById(companionOrder);
            throw new ServiceException("支付已过期，请重新下单");
        }

        // 4. 尝试从 Redis 获取缓存的预支付参数
        String cached = RedisUtils.getCacheObject(PREPARE_ORDER_KEY + payOrder.getOrderNo());
        if (StrUtil.isNotBlank(cached)) {
            PayCreateVo vo = JSONUtil.toBean(cached, PayCreateVo.class);
            log.info("重新支付：命中缓存，orderNo={}", orderNo);
            return vo;
        }

        // 5. 缓存过期，微信侧订单已失效，无法继续支付
        // 将陪玩订单标记为过期，提示用户重新下单
        companionOrder.setOrderStatus(CompanionOrderStatus.EXPIRED);
        companionOrderMapper.updateById(companionOrder);
        payOrderService.markClosed(payOrder.getOrderNo());
        throw new ServiceException("支付已过期，请重新下单");
    }










    /** 预约时间 KEY 前缀 */
    private static final String SCHEDULE_KEY_PREFIX = "companion_schedule:";

    /**
     * 解析预约时间字符串为 Date 对象.
     */
    private Date parseAppointmentTime(String appointmentTime) {
        if (StrUtil.isBlank(appointmentTime)) {
            throw new ServiceException("预约时间不能为空");
        }
        try {
            return DateUtil.parse(appointmentTime, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            throw new ServiceException("预约时间格式错误，请使用 yyyy-MM-dd HH:mm:ss");
        }
    }

    /**
     * 构建 Redis 预约时间 KEY：companion_schedule:{companionUserId}:{yyyyMMdd}
     */
    private String buildScheduleKey(Long companionUserId, Date appointmentTime) {
        return SCHEDULE_KEY_PREFIX + companionUserId + ":" + DateUtil.format(appointmentTime, "yyyyMMdd");
    }

    /**
     * 计算当天 23:59:59 的剩余秒数（用于 TTL）
     */
    private long calcDailyTtl() {
        Date now = new Date();
        Date endOfDay = DateUtil.endOfDay(now);
        return (endOfDay.getTime() - now.getTime()) / 1000;
    }

    /**
     * 检查陪玩在指定时间段是否有时间冲突（Redis 版）.
     */
    private void checkTimeSlotConflict(Long companionUserId, Date startTime, BigDecimal duration, Long excludeOrderId) {
        if (startTime == null || duration == null || duration.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        Date endTime = DateUtil.offsetSecond(startTime, (int) (duration.doubleValue() * 3600));
        String startTimeStr = DateUtil.format(startTime, "HH:mm:ss");
        String endTimeStr = DateUtil.format(endTime, "HH:mm:ss");

        String key = buildScheduleKey(companionUserId, startTime);
        // 获取当天所有已预约的时间段
        Map<String, String> allSchedules = RedisUtils.getCacheMap(key);
        if (allSchedules == null || allSchedules.isEmpty()) {
            return;
        }
        for (Map.Entry<String, String> entry : allSchedules.entrySet()) {
            String existedStart = entry.getKey();
            JSONObject existedValue = JSONUtil.parseObj(entry.getValue());
            String existedEnd = existedValue.getStr("end");
            String existedStatus = existedValue.getStr("status");
            // 只检查有效状态（PENDING_PAYMENT 和 PAID）
            if (!"PENDING_PAYMENT".equals(existedStatus) && !"PAID".equals(existedStatus)) {
                continue;
            }
            // 排除当前订单自身
            Long existedOrderId = existedValue.getLong("orderId");
            if (excludeOrderId != null && excludeOrderId.equals(existedOrderId)) {
                continue;
            }
            // 时间重叠判断：已有 start < 新 end AND 已有 end > 新 start
            if (existedStart.compareTo(endTimeStr) < 0 && existedEnd.compareTo(startTimeStr) > 0) {
                throw new ServiceException("该陪玩在该时段已被预约，请选择其他时间");
            }
        }
    }

    /**
     * 写入预约时间到 Redis.
     */
    private void addSchedule(Long companionUserId, Date startTime, BigDecimal duration, Long orderId) {
        Date endTime = DateUtil.offsetSecond(startTime, (int) (duration.doubleValue() * 3600));
        String key = buildScheduleKey(companionUserId, startTime);
        String hKey = DateUtil.format(startTime, "HH:mm:ss");

        // value: {"end":"11:00:00","orderId":123,"status":"PENDING_PAYMENT"}
        JSONObject value = JSONUtil.createObj()
            .set("end", DateUtil.format(endTime, "HH:mm:ss"))
            .set("orderId", orderId)
            .set("status", "PENDING_PAYMENT");

        RedisUtils.setCacheMapValue(key, hKey, value.toString());
        // 设置 TTL 为当天 23:59:59
        RedisUtils.expire(key, Duration.ofSeconds(calcDailyTtl()));
    }

    /**
     * 从 Redis 移除预约时间.
     */
    private void removeSchedule(Long companionUserId, Date startTime) {
        String key = buildScheduleKey(companionUserId, startTime);
        String hKey = DateUtil.format(startTime, "HH:mm:ss");
        RedisUtils.delCacheMapValue(key, hKey);
    }

    /**
     * 更新 Redis 预约时间状态.
     */
    private void updateScheduleStatus(Long companionUserId, Date startTime, String status) {
        String key = buildScheduleKey(companionUserId, startTime);
        String hKey = DateUtil.format(startTime, "HH:mm:ss");
        String cached = RedisUtils.getCacheMapValue(key, hKey);
        if (StrUtil.isNotBlank(cached)) {
            JSONObject value = JSONUtil.parseObj(cached);
            value.set("status", status);
            RedisUtils.setCacheMapValue(key, hKey, value.toString());
        }
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
            openid = companionUser.getOpenid();
        } else {
            User user = userMapper.selectById(userId);
            if (user == null) {
                throw new ServiceException("用户不存在,无法下单");
            }
            openid = user.getOpenid();
        }
        if (StrUtil.isBlank(openid)) {
            throw new ServiceException("用户未绑定微信openid，请重新登录");
        }
        return openid;
    }

    /**
     * 生成陪玩订单号：前缀 + 时间戳 + 6位随机数
     */
    private String generateOrderNo(String prefix) {
        return prefix + DateUtil.format(new Date(), "yyyyMMddHHmmssSSS") + RandomUtil.randomNumbers(6);
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
