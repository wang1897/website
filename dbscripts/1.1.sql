CREATE DATABASE  IF NOT EXISTS `qbao_schema` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `qbao_schema`;

CREATE TABLE `qbao_schema`.`admin_account` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `password` VARCHAR(45) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `is_delete` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `qbao_schema`.`wallet_account`
CHANGE COLUMN `account_no` `account_no` VARCHAR(10) NOT NULL ;

ALTER TABLE `qbao_schema`.`sys_config`
CHANGE COLUMN `value` `value` VARCHAR(128) NULL DEFAULT NULL ;

ALTER TABLE `qbao_schema`.`smart_contract`
CHANGE COLUMN `is_delete` `is_delete` TINYINT(1) NOT NULL ;

ALTER TABLE `qbao_schema`.`admin_account`
CHANGE COLUMN `is_delete` `is_delete` TINYINT(1) NOT NULL ;

ALTER TABLE `qbao_schema`.`wallet_account`
CHANGE COLUMN `activate_type` `activate_type` VARCHAR(5) NOT NULL ,
CHANGE COLUMN `source_type` `source_type` VARCHAR(5) NOT NULL COMMENT '0=IOS 1=安卓 2=Web' ;



CREATE TABLE `event` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `event_name` varchar(200) NOT NULL COMMENT '活动名称',
  `event_banner` varchar(200) DEFAULT NULL COMMENT '活动banner图片地址',
  `begin_date` datetime NOT NULL COMMENT '活动开始时间',
  `end_date` datetime NOT NULL COMMENT '活动结束时间',
  `event_total_amount` bigint(11) NOT NULL COMMENT '活动总币量\n',
  `event_available` tinyint(1) DEFAULT NULL COMMENT '活动有效性',
  `h5_url` varchar(150) NOT NULL COMMENT '活动报名H5页面',
  `upper_limit` bigint(11) DEFAULT NULL COMMENT '单笔最大限额',
  `lower_limit` bigint(11) DEFAULT NULL COMMENT '单笔最小限额',
  `original_currency` varchar(10) NOT NULL COMMENT '兑换元币种',
  `dest_currency` varchar(10) NOT NULL DEFAULT 'QBT' COMMENT '兑换币种',
  `expression` varchar(45) NOT NULL COMMENT '兑换比率',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `event_apply` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(6) NOT NULL COMMENT '账号NO',
  `apply_amount` decimal(12,2) NOT NULL COMMENT '报名时兑换元代币金额',
  `expected_income` decimal(12,2) NOT NULL COMMENT '预期兑换后金额',
  `apply_time` datetime NOT NULL COMMENT '报名时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `apply_status` INT(1) NOT NULL DEFAULT '0' COMMENT '报名状态\n1:报名\n2:取消报名\n3:允许兑换\n4:兑换完了',
  `event_id` bigint(11) NOT NULL COMMENT '活动ID',
  `actual_amount` decimal(12,2) DEFAULT NULL COMMENT '实际划账兑换元代币金额',
  `actual_income` decimal(12,2) DEFAULT NULL COMMENT '实际划账代币金额',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_EVENT_ACCOUNT_NO_idx` (`account_no`),
  KEY `FK_APPLY_EVENT_ID_idx` (`event_id`),
  CONSTRAINT `FK_APPLY_ACCOUNT_NO` FOREIGN KEY (`account_no`) REFERENCES `wallet_account` (`account_no`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_APPLY_EVENT_ID` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `exchange_log` (
  `id` bigint(16) NOT NULL AUTO_INCREMENT,
  `address` varchar(45) NOT NULL,
  `amount` decimal(12,2) NOT NULL,
  `exchange_time` datetime NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `qbao_schema`.`sys_wallet` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `value` VARCHAR(128) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));


  CREATE TABLE `qbao_schema`.`android_package` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `version_name` VARCHAR(128) NULL COMMENT '版本名称',
  `version_code` VARCHAR(128) NULL COMMENT '版本代码',
  `description` VARCHAR(128) NULL COMMENT '描述',
  `force_update` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否更新  默认0-否  1-是',
  `path` VARCHAR(128) NULL COMMENT '二维码地址',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `qbao_schema`.`exchange_log`
  ADD COLUMN `transaction_hash` VARCHAR(128) NOT NULL AFTER `update_time`;

INSERT INTO `qbao_schema`.`sys_wallet` (`name`, `value`) VALUES ('wallet_seed', 'bulk kiss impress enormous prohibit voter fear dig bee senior phase agent');
INSERT INTO `qbao_schema`.`sys_wallet` (`name`, `value`) VALUES ('wallet_address', 'QXihKBSdws7dzoYRHbP6piHwRumRqig8zY');
INSERT INTO `qbao_schema`.`sys_wallet` (`name`, `value`) VALUES ('fee', '0.1');
DELETE FROM `qbao_schema`.`sys_wallet` WHERE `id`='4';
INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ('gas_limit', '200000');

ALTER DATABASE qbao_schema CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

ALTER TABLE `qbao_schema`.`wallet_address`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`wallet_account`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`admin_account`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`android_package`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`event`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`event_apply`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`exchange_log`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`password_record`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`smart_contract`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`sys_config`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`sys_wallet`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`exchange_log`
  ADD COLUMN `event_apply_id` bigint(11) NULL AFTER `transaction_hash`;


SET SQL_SAFE_UPDATES = 0;
delete from `qbao_schema`.`event_apply`  where `id` in (select `xx`.`id` from(select `id` from `event_apply` group by `account_no`,`event_id` having count(1) >= 2) `xx`);

ALTER TABLE `qbao_schema`.`event_apply`
  add constraint `UQ_ACCOUNTNO_EVENTID` unique (`account_no`,`event_id`);