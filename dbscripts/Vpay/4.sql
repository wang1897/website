ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `words` VARCHAR(256) NULL COMMENT '支付密码' AFTER `authority`;


INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('19', '扫码支付', '-');