ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `qbt_balance` DECIMAL(12,2) NULL DEFAULT 0 AFTER `rong_version`;


ALTER TABLE `qbao_schema`.`exchange_log`
CHANGE COLUMN `id` `id` BIGINT(16) NOT NULL ,
ADD COLUMN `unit` VARCHAR(15) NOT NULL DEFAULT 0 COMMENT '币种 QBT／QBP' AFTER `event_apply_id`,
ADD COLUMN `type` INT(1) NOT NULL DEFAULT 0 COMMENT '0-提币 ／ 1 -充值 ／2-红包' AFTER `unit`,
ADD COLUMN `account_no` VARCHAR(10) NULL AFTER `type`,
ADD COLUMN `status` INT(1) NOT NULL DEFAULT 0 COMMENT '交易状态 0-已转账／1-已确认／2-失败' AFTER `account_no`;

/*ALTER TABLE `qbao_schema`.`exchange_log`
CHANGE COLUMN `unit` `unit` VARCHAR(15) NOT NULL DEFAULT '0' COMMENT '币种 0-QBT／1-QBP' ;*/


UPDATE `qbao_schema`.`sys_wallet` SET `value`='0.09' WHERE `id`='3';

//扣除QBT fee
INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ( 'deduct_fee', '2');

INSERT INTO `qbao_schema`.`sys_wallet` (`name`, `value`) VALUES ('gas_price', '40');

ALTER TABLE `qbao_schema`.`exchange_log`
CHANGE COLUMN `id` `id` BIGINT(16) NOT NULL AUTO_INCREMENT ;

ALTER TABLE `qbao_schema`.`exchange_log`
CHANGE COLUMN `amount` `amount` DECIMAL(15,6) NOT NULL ;


ALTER TABLE `qbao_schema`.`event_apply`
CHANGE COLUMN `apply_amount` `apply_amount` DECIMAL(15,6) NOT NULL COMMENT '报名时兑换元代币金额',
CHANGE COLUMN `expected_income` `expected_income` DECIMAL(16,6) NOT NULL COMMENT '预期兑换后金额',
CHANGE COLUMN `actual_amount` `actual_amount` DECIMAL(15,6) NULL DEFAULT NULL COMMENT '实际划账兑换元代币金额' ,
CHANGE COLUMN `actual_income` `actual_income` DECIMAL(15,6) NULL DEFAULT NULL COMMENT '实际划账代币金额';

CREATE TABLE `qbao_schema`.`group_manager` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `account_no` VARCHAR(10) NOT NULL,
  `group_name` VARCHAR(45) NOT NULL,
  `address` VARCHAR(45) NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`));

  ALTER TABLE `qbao_schema`.`group_manager`
CHANGE COLUMN `id` `id` BIGINT(11) NOT NULL AUTO_INCREMENT ;

ALTER TABLE `qbao_schema`.`group_manager`
ADD COLUMN `transfer_status` INT(1) NOT NULL DEFAULT 0 COMMENT '转账状态 0-未转帐／1-已转账' AFTER `update_time`,
ADD COLUMN `gear_type` INT(1) NOT NULL DEFAULT 0 COMMENT '奖励档位\n0-无奖励\n1-100QBT\n2-300QBT\n3-500QBT\n4-1000QBT' AFTER `transfer_status`,
ADD COLUMN `bonus` DECIMAL(15,6) NULL DEFAULT 0 COMMENT '额外奖励金额 \\n若有多次 累加金额' AFTER `gear_type`;

ALTER TABLE `qbao_schema`.`chat_group`
ADD COLUMN `sequence` INT(4) NULL AFTER `limit_amount`;

ALTER TABLE `qbao_schema`.`exchange_log`
CHANGE COLUMN `address` `address` VARCHAR(128) CHARACTER SET 'utf8' NOT NULL ,
CHANGE COLUMN `unit` `unit` BIGINT(11) NOT NULL DEFAULT '0' COMMENT '币种 ' ,
CHANGE COLUMN `type` `type` INT(1) NOT NULL DEFAULT '0' COMMENT '0-提币 -／ 1 -充值 + ／2-发红包 -／3-收红包 +／4 活动 + / 5 新人奖励+ /6 红包退款 + ／7 新人红包 + ／8 手续费 -',
ADD COLUMN `from_address` VARCHAR(128) NULL AFTER `status`;

ALTER TABLE `qbao_schema`.`exchange_log`
CHANGE COLUMN `address` `address` VARCHAR(128) CHARACTER SET 'utf8' NULL ,
CHANGE COLUMN `transaction_hash` `transaction_hash` VARCHAR(128) CHARACTER SET 'utf8' NULL ,
ADD COLUMN `fee` DECIMAL(15,6) NULL AFTER `from_address`,
ADD COLUMN `fee_unit` DECIMAL(15,6) NULL AFTER `fee`;

ALTER TABLE `qbao_schema`.`smart_contract`
ADD COLUMN `is_show` TINYINT(1) NOT NULL DEFAULT 0 AFTER `is_delete`;

ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `share_code` VARCHAR(128) NULL AFTER `qbt_balance`,
ADD COLUMN `invite_code` VARCHAR(128) NULL AFTER `share_code`;


INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ( 'group_manager_end_time', '2017-12-14 00:00:00');


ALTER TABLE `qbao_schema`.`user_group`
ADD COLUMN `role` INT(1) NULL DEFAULT 0 COMMENT '成员权限\nNULL/0：群众\n1：群主\n2：管理\n9：ADMIN' ;AFTER `update_time`;

CREATE TABLE `qbao_schema`.`send_red_packet` (
  `id` BIGINT(13) NOT NULL,
  `account_no` VARCHAR(10) NOT NULL COMMENT '发送人accountNo\n',
  `amount` DECIMAL(15,6) NOT NULL COMMENT '金额',
  `unit` BIGINT(11) NOT NULL COMMENT '币种',
  `number` INT(4) NOT NULL DEFAULT 1 COMMENT '红包个数',
  `type` INT(1) NOT NULL COMMENT '红包类型\n0 普通\n1  拼手气',
  `send_time` DATETIME NULL COMMENT '发送时间',
  `is_available` TINYINT(1) NULL,
  `comment` VARCHAR(300) NULL,
  `theme` BIGINT(11) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

  CREATE TABLE `qbao_schema`.`get_red_packet` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `red_packet_id` BIGINT(13) NOT NULL COMMENT '发送红包ID',
  `sequence` INT(4) NULL COMMENT '红包顺序',
  `account_no` VARCHAR(10) NULL COMMENT '收取人NO',
  `amount` DECIMAL(15,6) NULL COMMENT '收取金额',
  `unit` BIGINT(11) NULL COMMENT '币种',
  `get_time` DATETIME NULL COMMENT '领取时间',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `qbao_schema`.`account_balance` (
  `id` BIGINT(11) NOT NULL,
  `account_no` VARCHAR(10) NOT NULL,
  `unit` BIGINT(11) NOT NULL,
  `amount` DECIMAL(15,6) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

  ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `receive_number` INT NOT NULL DEFAULT 1 AFTER `invite_code`;


CREATE TABLE `batch_definition` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `frequency` int(11) DEFAULT NULL COMMENT '0. Hourly 1. Daily 2. Weekly 3. Monthly 4. Yearly 5. Minutely',
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `class_name` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter_name1` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter_name2` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter_name3` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter_name4` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter_name5` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `time_slot` int(11) DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `batch_definition_index_1` (`expire_time`),
  KEY `batch_definition_index_2` (`is_active`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



CREATE TABLE `batch_task` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `definition_id` bigint(11) DEFAULT NULL,
  `name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `execute_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0: active, 1: completed, 2: cancelled, 3: running',
  `class_name` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter1` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter2` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter3` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter4` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter5` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL,
  `resource_table` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resource_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `batch_task_index_1` (`status`),
  KEY `batch_task_index_2` (`expire_time`),
  KEY `batch_task_index_3` (`resource_table`,`resource_id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `batch_result` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `result` text COLLATE utf8mb4_unicode_ci,
  `status` int(11) DEFAULT NULL COMMENT '0: success 1: fail',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `task_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


ALTER TABLE `qbao_schema`.`get_red_packet`
ADD COLUMN `comment` VARCHAR(50) NULL AFTER `update_time`;

ALTER TABLE `qbao_schema`.`send_red_packet`
CHANGE COLUMN `comment` `comment` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ;
ALTER TABLE `qbao_schema`.`send_red_packet`
CHANGE COLUMN `id` `id` BIGINT(13) NOT NULL AUTO_INCREMENT ;

ALTER TABLE `qbao_schema`.`send_red_packet`
CHANGE COLUMN `comment` `comment` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL ,
ADD COLUMN `balance` DECIMAL(15,6) NULL AFTER `theme`;

ALTER TABLE `qbao_schema`.`account_balance`
CHANGE COLUMN `id` `id` BIGINT(11) NOT NULL AUTO_INCREMENT ;

ALTER TABLE `qbao_schema`.`get_red_packet`
ADD COLUMN `best_luck` TINYINT(1) NULL COMMENT 'true:手气最佳' AFTER `comment`;

ALTER TABLE `qbao_schema`.`event`
ADD COLUMN `type` INT(4) NULL COMMENT '0:空投活动\n1:邀请好友活动' AFTER `update_time`;

ALTER TABLE `qbao_schema`.`event`
CHANGE COLUMN `event_total_amount` `event_total_amount` BIGINT(11) NULL COMMENT '活动总币量\n' ,
CHANGE COLUMN `original_currency` `original_currency` VARCHAR(10) CHARACTER SET 'utf8' NULL COMMENT '兑换元币种' ,
CHANGE COLUMN `dest_currency` `dest_currency` VARCHAR(10) CHARACTER SET 'utf8' NULL DEFAULT 'QBT' COMMENT '兑换币种' ,
CHANGE COLUMN `expression` `expression` VARCHAR(45) CHARACTER SET 'utf8' NULL COMMENT '兑换比率' ,
ADD COLUMN `start_date` DATETIME NULL AFTER `type`;

ALTER TABLE `qbao_schema`.`event`
CHANGE COLUMN `begin_date` `begin_date` DATETIME NOT NULL COMMENT '活动banner上挂时间' ,
CHANGE COLUMN `start_date` `start_date` DATETIME NULL DEFAULT NULL COMMENT '活动开始时间' ;

ALTER TABLE `qbao_schema`.`smart_contract`
ADD COLUMN `decimal` DECIMAL(15,6) NULL AFTER `is_show`;

CREATE TABLE `qbao_schema`.`currency_rate` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `currency` VARCHAR(3) NOT NULL,
  `rate` DECIMAL(20,12) NOT NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `last_check_time` DATETIME NULL,
  PRIMARY KEY (`id`));

INSERT INTO `qbao_schema`.`batch_definition` (`name`, `frequency`, `start_time`, `is_active`, `class_name`, `create_time`, `time_slot`, `expire_time`,`update_time`) VALUES ('getRatesBatch', '0', '2017-12-13 11:00:00', '1', 'com.aethercoder.core.dao.batch.GetCurrencyRateBatch', '2017-12-11 11:11:11', '1', '2017-12-13 12:06:11','2017-12-11 11:11:11');

ALTER TABLE `qbao_schema`.`smart_contract`
CHANGE COLUMN `decimal` `contract_decimal` DECIMAL(15,6) NULL DEFAULT NULL ;

ALTER TABLE `qbao_schema`.`batch_definition`
CHANGE COLUMN `parameter_name1` `parameter_name1` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `parameter_name2` `parameter_name2` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `parameter_name3` `parameter_name3` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `parameter_name4` `parameter_name4` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `parameter_name5` `parameter_name5` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ;
ALTER TABLE `qbao_schema`.`batch_definition`
ADD COLUMN `parameter_name6` VARCHAR(300) NULL AFTER `parameter_name5`;

ALTER TABLE `qbao_schema`.`batch_task`
CHANGE COLUMN `parameter1` `parameter1` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `parameter2` `parameter2` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `parameter3` `parameter3` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `parameter4` `parameter4` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `parameter5` `parameter5` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
ADD COLUMN `parameter6` VARCHAR(300) NULL AFTER `parameter5`;

ALTER TABLE `qbao_schema`.`chat_group_member`
CHANGE COLUMN `role` `role` INT(1) NULL DEFAULT NULL COMMENT '成员权限\nNULL/0：群众\n2：群主\n1：管理\n9：ADMIN' ;


ALTER TABLE `qbao_schema`.`fund_user`
ADD COLUMN `unique_id` VARCHAR(45) NULL AFTER `ico_qualification`,
ADD UNIQUE INDEX `unique_id_UNIQUE` (`unique_id` ASC);

INSERT INTO `smart_contract` (`name`,`address`,`icon`,`create_time`,`update_time`,`is_delete`,`is_show`,`contract_decimal`) VALUES ('Qbao Energy','QBE','1513080533427.png',NULL,'2017-12-13 14:55:01',0,1,NULL);

INSERT INTO `smart_contract` (`name`,`address`,`icon`,`create_time`,`update_time`,`is_delete`,`is_show`,`contract_decimal`) VALUES ('QTUM','QTUM','1512816431228.png','2017-12-09 18:47:25','2017-12-09 18:47:25',0,1,8.000000);

INSERT INTO `sys_config` (`name`,`value`,`create_time`,`update_time`) VALUES ('invite_html_url','https://qbao.fund/qbao/Activities/registerSendGifts.html',NULL,NULL);

INSERT INTO `sys_config` (`name`,`value`,`create_time`,`update_time`) VALUES ('user_rewards','2000',NULL,NULL);

INSERT INTO `sys_config` (`name`,`value`,`create_time`,`update_time`) VALUES ('INVITE_FRIEND_TEXT','INVITE_SHARE_CODE',NULL,'2017-12-12 18:16:48');

INSERT INTO `sys_config` (`name`,`value`,`create_time`,`update_time`) VALUES ('invite_friend_url','https://qbao.fund/qbao/Activities/envelopeCenter.html','2017-12-12 18:06:53','2017-12-12 18:06:53');

INSERT INTO `sys_config` (`name`,`value`,`create_time`,`update_time`) VALUES ('currency_rate_api_url','https://openexchangerates.org/api/latest.json','2017-12-12 20:00:09','2017-12-12 20:00:09');

INSERT INTO `sys_config` (`name`,`value`,`create_time`,`update_time`) VALUES ('RATE_API_APP_ID','3ced2398c3a040518db2c522aa3479da',NULL,'2017-12-12 20:53:57');
