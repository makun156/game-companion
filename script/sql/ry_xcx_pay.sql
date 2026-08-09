-- ------------------------------------------------------------
-- 微信小程序支付订单表
-- 启用 /xcx/pay/* 前请先执行此脚本
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `t_pay_order` (
    `id` bigint NOT NULL COMMENT '主键',
    `order_no` varchar(32) NOT NULL COMMENT '商户订单号',
    `user_id` bigint DEFAULT NULL COMMENT '买家用户ID',
    `biz_type` varchar(32) DEFAULT NULL COMMENT '业务类型',
    `biz_id` bigint DEFAULT NULL COMMENT '业务ID',
    `openid` varchar(64) DEFAULT NULL COMMENT '微信openid',
    `title` varchar(128) NOT NULL COMMENT '商品描述',
    `amount` bigint NOT NULL COMMENT '支付金额（单位：分）',
    `status` char(1) NOT NULL DEFAULT '0' COMMENT '0待支付 1已支付 2已关闭 3已退款',
    `attach` varchar(128) DEFAULT NULL COMMENT '商户自定义数据',
    `transaction_id` varchar(64) DEFAULT NULL COMMENT '微信支付交易号',
    `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
    `expire_time` datetime DEFAULT NULL COMMENT '订单过期时间',
    `close_time` datetime DEFAULT NULL COMMENT '关闭时间',
    `create_dept` bigint DEFAULT NULL,
    `create_by` bigint DEFAULT NULL,
    `create_time` datetime DEFAULT NULL,
    `update_by` bigint DEFAULT NULL,
    `update_time` datetime DEFAULT NULL,
    `remark` varchar(500) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status_expire` (`status`, `expire_time`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '微信小程序支付订单';