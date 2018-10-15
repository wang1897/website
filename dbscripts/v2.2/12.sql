CREATE TABLE `qbao_schema`.`sys_wallet_address` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `address` VARCHAR(128) NULL COMMENT '提币供应地址',
  `key` VARCHAR(300) NULL,
  `max_minutes` INT(3) NULL COMMENT '失败交易时长',
  `contract_id` BIGINT(11) NULL COMMENT '特定小币种',
  `last_left_amount` DECIMAL(18,6) NULL COMMENT '特定币种的剩余量',
  `qbt_left_amount` DECIMAL(18,6) NULL COMMENT 'QBT的剩余量',
  `keep_service` TINYINT(1) NULL COMMENT '1:on\n0:off',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`))
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_unicode_ci;

ALTER TABLE `qbao_schema`.`sys_wallet_address`
CHANGE COLUMN `id` `id` BIGINT(11) NOT NULL AUTO_INCREMENT ;

ALTER TABLE `qbao_schema`.`token_calendar`
ADD COLUMN `event_time` DATETIME NULL AFTER `end_time`;

ALTER TABLE `qbao_schema`.`token_calendar`
DROP COLUMN `event_time`;



ALTER TABLE `qbao_schema`.`smart_contract`
ADD COLUMN `sequence` INT(4) NULL AFTER `api_address`;


INSERT INTO `qbao_schema`.`sys_config` ( `name`,`value`) VALUES ( 'EXCHANGE_QBT_END_TIME','2018-01-29 00:00:00');




