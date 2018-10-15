-- created by lilangfeng update 2018/2/1
CREATE TABLE `qbao_schema`.`sys_notice` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `language_type` varchar(10) CHARACTER SET utf8mb4 NOT NULL COMMENT '公告语言：cn（中文公告）en（英文公告）ko（韩文公告）\n',
  `title` varchar(256) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '标题',
  `content` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容',
  `release_by` bigint(11) DEFAULT NULL COMMENT '发布人\n',
  `status` int(1) DEFAULT NULL COMMENT '状态',
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `is_delete` tinyint(1) DEFAULT '0',
  `issue_time` datetime DEFAULT NULL COMMENT '最近一次发布内容切换到保存并发布的时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci







/*我的群索引*/
ALTER TABLE `qbao_schema`.`user_group`
ADD INDEX `INDEX_ACCOUNTNO` (`account_no` ASC);
/*群索引*/
ALTER TABLE `qbao_schema`.`chat_group`
ADD INDEX `INDEX_GROUPNO` (`group_no` ASC);


ALTER TABLE `qbao_schema`.`red_packet_event`
ADD COLUMN `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 AFTER `update_time`;


ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `level` INT(1) NULL DEFAULT 0 COMMENT '等级' AFTER `invited_daily`,
ADD COLUMN `assets` DECIMAL(15,2) NULL COMMENT '资产' AFTER `level`;


ALTER TABLE `qbao_schema`.`wallet_account`
DROP COLUMN `qbt_balance`,
CHANGE COLUMN `share_code` `share_code` VARCHAR(10) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `invite_code` `invite_code` VARCHAR(10) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
ADD COLUMN `login_time` DATETIME NULL AFTER `invite_qbe`,
ADD COLUMN `language` VARCHAR(10) NULL AFTER `login_time`;

CREATE TABLE `qbao_schema`.`contract_history` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `price` DECIMAL(24,12) NOT NULL,
  `update_time` DATETIME NULL,
  `create_time` DATETIME NULL,
  `contract_id` BIGINT(11) NOT NULL,
  PRIMARY KEY (`id`));

INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ( 'DEFAULT_EXCHANGE_QBE', '20000');

CREATE TABLE `qbao_schema`.`clearance` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `unit` BIGINT(11) NULL,
  `type` INT(2) NULL COMMENT '交易类型和exchange_log表type一致\n充值\n提币',
  `qbao_amount` DECIMAL(20,6) NULL COMMENT 'Qbao内部资金',
  `chain_amount` DECIMAL(20,6) NULL COMMENT '链上资金',
  `clearance_day` DATETIME NULL COMMENT '清算日',
  `is_clear` TINYINT(1) NULL COMMENT '清算结果',
  `qbao_id` BIGINT(13) NULL COMMENT '最后一笔Qbao的流水号\n',
  `chain_block` BIGINT(20) NULL COMMENT '最后一笔链上的block数',
  `qbao_number` BIGINT(13) NULL COMMENT 'QBAO交易笔数',
  `chain_number` BIGINT(13) NULL COMMENT '链上交易笔数',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

CREATE TABLE `qbao_schema`.`clearance_detail` (
  `id` BIGINT(13) NOT NULL AUTO_INCREMENT,
  `clearance_id` BIGINT(13) NOT NULL,
  `qbao_txid` VARCHAR(128) NULL,
  `qbao_from_address` VARCHAR(64) NULL,
  `qbao_to_address` VARCHAR(64) NULL,
  `qbao_type` TINYINT(1) NULL COMMENT '0:out 提币\n1:in 充值\n',
  `qbao_exchange_time` DATETIME NULL,
  `qbao_unit` BIGINT(11) NULL,
  `qbao_amount` DECIMAL(20,6) NULL,
  `qbao_fee_unit` BIGINT(11) NULL,
  `qbao_fee_amount` DECIMAL(20,6) NULL,
  `chain_txid` VARCHAR(128) NULL,
  `chain_from_address` VARCHAR(64) NULL,
  `chain_to_address` VARCHAR(64) NULL,
  `chain_type` TINYINT(1) NULL COMMENT '0:out \n1:in',
  `chain_unit` BIGINT(11) NULL,
  `chain_amount` DECIMAL(20,6) NULL,
  `chain_fee_unit` BIGINT(11) NULL,
  `chain_fee_amount` DECIMAL(20,6) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `qbao_schema`.`clearance_detail`
ADD COLUMN `qbao_id` BIGINT(13) NULL AFTER `clearance_id`,
ADD COLUMN `chain_block` BIGINT(13) NULL AFTER `qbao_fee_amount`;

ALTER TABLE `qbao_schema`.`clearance_detail`
ADD COLUMN `chain_exchange_time` DATETIME NULL AFTER `chain_amount`;

ALTER TABLE `qbao_schema`.`exchange_log`
CHANGE COLUMN `fee_unit` `fee_unit` BIGINT(11) NULL DEFAULT NULL ;

ALTER TABLE `qbao_schema`.`clearance_detail`
ADD COLUMN `is_clear` TINYINT(1) NULL COMMENT '0:unclear\n1:clear' AFTER `chain_fee_amount`;

ALTER TABLE `qbao_schema`.`clearance_detail`
CHANGE COLUMN `qbao_type` `qbao_type` INT(1) NULL DEFAULT NULL COMMENT '0:out 提币\n1:in 充值\n' ,
CHANGE COLUMN `chain_type` `chain_type` INT(1) NULL DEFAULT NULL COMMENT '0:out \n1:in' ;






