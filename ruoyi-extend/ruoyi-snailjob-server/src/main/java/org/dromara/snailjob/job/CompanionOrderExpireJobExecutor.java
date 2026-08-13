package org.dromara.snailjob.job;

import cn.hutool.core.date.DateUtil;
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.business.domain.CompanionOrder;
import com.business.domain.CompanionOrderStatus;
import com.business.domain.PayOrder;
import com.business.domain.PayOrderStatus;
import com.business.mapper.CompanionOrderMapper;
import com.business.mapper.PayOrderMapper;
import com.business.mapper.GameCompanionUserMapper;
import org.dromara.common.redis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 陪玩订单过期定时任务.
 * <p>
 * 每1分钟扫描一次，将超过15分钟未支付的陪玩订单标记为 EXPIRED，
 * 同时关闭关联的支付订单.
 * 配合被动过期（用户支付时实时校验）一起使用，确保数据一致性.
 *
 * @author companion
 */
@RequiredArgsConstructor
@Component
@JobExecutor(name = "companionOrderExpireJobExecutor")
public class CompanionOrderExpireJobExecutor {

    private final CompanionOrderMapper companionOrderMapper;
    private final PayOrderMapper payOrderMapper;
    private final GameCompanionUserMapper companionUserMapper;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        // ---------- 1. 过期待支付订单 ----------
        LambdaQueryWrapper<CompanionOrder> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(CompanionOrder::getOrderStatus, CompanionOrderStatus.PENDING_PAYMENT);
        wrapper.lt(CompanionOrder::getCreateTime, DateUtil.offsetMinute(DateUtil.date(), -15));
        List<CompanionOrder> expiredOrders = companionOrderMapper.selectList(wrapper);

        int updateCount = 0;
        if (!expiredOrders.isEmpty()) {
            for (CompanionOrder order : expiredOrders) {
                // 1. 更新陪玩订单状态为 EXPIRED
                order.setOrderStatus(CompanionOrderStatus.EXPIRED);
                companionOrderMapper.updateById(order);

                // 2. 关闭关联的支付订单
                LambdaQueryWrapper<PayOrder> payWrapper = Wrappers.lambdaQuery();
                payWrapper.eq(PayOrder::getCompanionOrderId, order.getId());
                payWrapper.eq(PayOrder::getStatus, PayOrderStatus.WAITING);
                PayOrder payOrder = new PayOrder();
                payOrder.setStatus(PayOrderStatus.CLOSED);
                payOrder.setCloseTime(DateUtil.date());
                payOrderMapper.update(payOrder, payWrapper);

                // 3. 更新陪玩工作状态为 接单中
                com.business.domain.GameCompanionUser companion = new com.business.domain.GameCompanionUser();
                companion.setId(order.getCompanionUserId());
                companion.setWorkStatus(com.business.domain.WorkStatus.AVAILABLE);
                companionUserMapper.updateById(companion);

                // 4. 清理 Redis 预约记录，避免阻塞其他用户预约该时间段
                if (order.getAppointmentTime() != null) {
                    String scheduleKey = "companion_schedule:" + order.getCompanionUserId() + ":"
                        + cn.hutool.core.date.DateUtil.format(order.getAppointmentTime(), "yyyyMMdd");
                    String hKey = cn.hutool.core.date.DateUtil.format(order.getAppointmentTime(), "HH:mm:ss");
                    RedisUtils.delCacheMapValue(scheduleKey, hKey);
                }

                updateCount++;
                SnailJobLog.LOCAL.info("已处理过期订单 {}", order.getId());
            }
            SnailJobLog.LOCAL.info("过期订单处理完成，共处理 {} 条", expiredOrders.size());
        } else {
            SnailJobLog.LOCAL.info("没有待处理的过期订单");
        }

        // ---------- 2. 自动完成超时未结束的进行中订单 ----------
        LambdaQueryWrapper<CompanionOrder> inProgressWrapper = Wrappers.lambdaQuery();
        inProgressWrapper.eq(CompanionOrder::getOrderStatus, CompanionOrderStatus.IN_PROGRESS);
        inProgressWrapper.isNotNull(CompanionOrder::getAppointmentTime);
        inProgressWrapper.isNotNull(CompanionOrder::getDuration);
        List<CompanionOrder> inProgressOrders = companionOrderMapper.selectList(inProgressWrapper);

        int autoCompleteCount = 0;
        for (CompanionOrder order : inProgressOrders) {
            // 计算预计结束时间 = 预约时间 + 预约时长 + 30分钟宽限期
            Date expectedEndTime = DateUtil.offsetMinute(
                DateUtil.offsetSecond(order.getAppointmentTime(),
                    (int) (order.getDuration().doubleValue() * 3600)),
                30);
            if (new java.util.Date().after(expectedEndTime)) {
                // 超过预计结束时间 + 宽限期，自动完成
                order.setOrderStatus(CompanionOrderStatus.COMPLETED);
                order.setActualEndTime(new java.util.Date());
                companionOrderMapper.updateById(order);

                // 清理 Redis 预约记录
                if (order.getAppointmentTime() != null) {
                    String scheduleKey = "companion_schedule:" + order.getCompanionUserId() + ":"
                        + DateUtil.format(order.getAppointmentTime(), "yyyyMMdd");
                    String hKey = DateUtil.format(order.getAppointmentTime(), "HH:mm:ss");
                    RedisUtils.delCacheMapValue(scheduleKey, hKey);
                }

                // 更新陪玩工作状态为 接单中
                com.business.domain.GameCompanionUser companion = new com.business.domain.GameCompanionUser();
                companion.setId(order.getCompanionUserId());
                companion.setWorkStatus(com.business.domain.WorkStatus.AVAILABLE);
                companionUserMapper.updateById(companion);

                autoCompleteCount++;
                SnailJobLog.LOCAL.info("自动完成进行中订单 {}", order.getId());
            }
        }

        if (autoCompleteCount > 0) {
            SnailJobLog.LOCAL.info("自动完成订单处理完成，共 {} 条", autoCompleteCount);
        }

        return ExecuteResult.success("已处理 " + updateCount + " 条过期订单");
    }

}
