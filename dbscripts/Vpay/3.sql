CREATE TABLE `qbao_schema`.`t_order` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `order_id` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '订单号',
  `customer_id` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商家ID',
  `account_no` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '买家ID',
  `order_time` datetime DEFAULT NULL COMMENT '交易时间',
  `amount` decimal(18,6) DEFAULT NULL COMMENT '法币金额',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `type` int(1) DEFAULT NULL COMMENT '支付类型',
  `unit` varchar(4) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '法币币种',
  `token_amount` decimal(18,6) DEFAULT NULL COMMENT '代币币种',
  `token_unit` bigint(11) DEFAULT NULL COMMENT '代币币种',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `fee` decimal(18,6) DEFAULT NULL COMMENT '手续费',
  `fee_unit` bigint(11) DEFAULT NULL COMMENT '手续费币种',
  `pay_rate` decimal(18,6) DEFAULT NULL COMMENT '交易时代币转美元汇率',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `qbao_schema`.`customer_balance` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `customer_id` VARCHAR(10) NOT NULL COMMENT '商家ID',
  `amount` DECIMAL(18,6) NULL COMMENT '商家获取金额',
  `unit` BIGINT(11) NULL COMMENT '币种',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

  ALTER TABLE `qbao_schema`.`exchange_log`
ADD COLUMN `order_id` VARCHAR(30) NULL COMMENT '订单编号' AFTER `is_deleted`;

ALTER TABLE `qbao_schema`.`quiz`
CHANGE COLUMN `question` `question` VARCHAR(2000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '题干' ;

ALTER TABLE `qbao_schema`.`smart_contract`
ADD COLUMN `use_pay` TINYINT(1) NULL DEFAULT '0' COMMENT '是否可支付' ;

CREATE TABLE `qbao_schema`.`t_contract_currency_price` (
  `id` BIGINT(11) NOT NULL,
  `contract` BIGINT(11) NULL COMMENT '代币',
  `currency` VARCHAR(3) NULL COMMENT '法币',
  `rate` DECIMAL(18,6) NULL COMMENT '汇率',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

  ALTER TABLE `qbao_schema`.`t_contract_currency_price`
CHANGE COLUMN `id` `id` BIGINT(11) NOT NULL AUTO_INCREMENT ;


INSERT INTO `qbao_schema`.`batch_definition` ( `name`, `frequency`, `start_time`, `is_active`, `class_name`, `time_slot`,`expire_time`) VALUES ( 'GetPayPriceBatch', '5', '2018-02-11 18:39:30', '1', 'com.aethercoder.core.dao.batch.GetPayPriceBatch', '1','2018-01-01 00:03:00');
INSERT INTO `qbao_schema`.`batch_definition` ( `name`, `frequency`, `start_time`, `is_active`, `class_name`, `time_slot`,`expire_time`) VALUES ( 'GetPayPriceBatch', '5', '2018-02-11 18:39:30', '1', 'com.aethercoder.core.dao.batch.GetPayPriceBatch', '1','2018-01-01 00:03:00');

INSERT INTO `qbao_schema`.`sys_config` ( `name`,`value`) VALUES ( 'QBAO_PAY_RATE_URL','https://api.bithumb.com/public/orderbook/qtum');
INSERT INTO `qbao_schema`.`sys_config` ( `name`,`value`) VALUES ( 'QBAO_PAY_PHONE','17621396727');







