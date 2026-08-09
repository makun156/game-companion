-- Wechat mini program openid cache for pay.
-- Run once on MySQL before enabling the new login/pay flow.
ALTER TABLE `t_user`
    ADD COLUMN `openid` varchar(64) DEFAULT NULL COMMENT 'Wechat mini program openid' AFTER `phonenumber`;

ALTER TABLE `t_game_companion_user`
    ADD COLUMN `openid` varchar(64) DEFAULT NULL COMMENT 'Wechat mini program openid' AFTER `phone`;
