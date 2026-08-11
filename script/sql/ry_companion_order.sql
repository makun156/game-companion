-- ------------------------------------------------------------
-- 陪玩订单表
-- 用户下单找陪玩的核心业务表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_companion_order` (
    `id`                 bigint       NOT NULL COMMENT '主键',
    `order_no`           varchar(32)  NOT NULL COMMENT '订单号，如 CO202608091234560001',
    `user_id`            bigint       NOT NULL COMMENT '下单用户ID（t_user）',
    `companion_user_id`  bigint       NOT NULL COMMENT '陪玩用户ID（t_game_companion_user）',
    `game_id`            bigint       DEFAULT NULL COMMENT '游戏ID（t_game）',
    `game_level_id`      bigint       DEFAULT NULL COMMENT '游戏段位ID（t_game_level）',
    `duration`           decimal(5,1) DEFAULT NULL COMMENT '预约时长（小时，如1.5）',
    `unit_price`         bigint       DEFAULT NULL COMMENT '单价（分/小时）',
    `total_amount`       bigint       NOT NULL COMMENT '订单总金额（分）',
    `paid_amount`        bigint       DEFAULT 0 COMMENT '已支付金额（分）',
    `refund_amount`      bigint       DEFAULT 0 COMMENT '已退款金额（分）',
    `appointment_time`   datetime     DEFAULT NULL COMMENT '预约开始时间',
    `actual_start_time`  datetime     DEFAULT NULL COMMENT '实际上单时间',
    `actual_end_time`    datetime     DEFAULT NULL COMMENT '实际结束时间',
    `order_status`       varchar(20)  NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态：PENDING_PAYMENT-待支付 PAID-已支付 IN_PROGRESS-进行中 COMPLETED-已完成 CANCELLED-已取消 EXPIRED-已过期 REFUNDING-退款中 REFUNDED-已退款',
    `cancel_reason`      varchar(500) DEFAULT NULL COMMENT '取消原因',
    `cancel_time`        datetime     DEFAULT NULL COMMENT '取消时间',
    `remark`             varchar(500) DEFAULT NULL COMMENT '用户备注',
    `create_dept`        bigint       DEFAULT NULL,
    `create_by`          bigint       DEFAULT NULL,
    `create_time`        datetime     DEFAULT NULL,
    `update_by`          bigint       DEFAULT NULL,
    `update_time`        datetime     DEFAULT NULL,
    `del_flag`           char(1)      DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_companion_user_id` (`companion_user_id`),
    KEY `idx_order_status` (`order_status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='陪玩订单表';

-- ------------------------------------------------------------
-- 修改 t_pay_order：去掉 attach，增加 companion_order_id
-- ------------------------------------------------------------
ALTER TABLE `t_pay_order`
    DROP COLUMN `attach`,
    ADD COLUMN `companion_order_id` bigint DEFAULT NULL COMMENT '陪玩订单ID（t_companion_order）',
    ADD INDEX `idx_companion_order_id` (`companion_order_id`);

-- ------------------------------------------------------------
-- 退款记录表（可选，先预留）
-- ------------------------------------------------------------
-- CREATE TABLE IF NOT EXISTS `t_payment_refund` (
--     `id`                 bigint       NOT NULL COMMENT '主键',
--     `refund_no`          varchar(32)  NOT NULL COMMENT '退款单号',
--     `pay_order_no`       varchar(32)  NOT NULL COMMENT '原支付订单号',
--     `companion_order_id` bigint       NOT NULL COMMENT '陪玩订单ID',
--     `refund_amount`      bigint       NOT NULL COMMENT '退款金额（分）',
--     `refund_reason`      varchar(500) DEFAULT NULL COMMENT '退款原因',
--     `refund_status`      varchar(20)  NOT NULL DEFAULT 'PROCESSING' COMMENT '退款状态：PROCESSING/SUCCESS/FAILED',
--     `transaction_id`     varchar(64)  DEFAULT NULL COMMENT '微信退款单号',
--     `refund_time`        datetime     DEFAULT NULL COMMENT '退款完成时间',
--     `create_time`        datetime     DEFAULT NULL,
--     `update_time`        datetime     DEFAULT NULL,
--     PRIMARY KEY (`id`),
--     UNIQUE KEY `uk_refund_no` (`refund_no`),
--     KEY `idx_companion_order_id` (`companion_order_id`),
--     KEY `idx_pay_order_no` (`pay_order_no`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';
