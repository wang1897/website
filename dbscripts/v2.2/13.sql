ALTER TABLE `qbao_schema`.`member_level`
ADD COLUMN `icon` VARCHAR(45) NULL DEFAULT NULL COMMENT '等级图标' AFTER `money`;
CREATE TABLE `qbao_schema`.`withdraw` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `account_no` VARCHAR(10) NULL,
  `exchange_time` DATETIME NULL,
  `amount` DECIMAL(18,6) NULL,
  `to_address` VARCHAR(128) NULL,
  `from_address` VARCHAR(128) NULL,
  `unit` BIGINT(11) NULL,
  `fee_unit` BIGINT(11) NULL,
  `fee` DECIMAL(18,6) NULL,
  `transaction_hash` VARCHAR(200) NULL,
  `status` INT(1) NULL COMMENT '0:applied\n1:wating\n2:exchanged\n3:confirmed',
  `apply_time` DATETIME NULL,
  `confirm_time` DATETIME NULL,
  `service_pool` BIGINT(11) NULL,
  `exchange_no` VARCHAR(18) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));


