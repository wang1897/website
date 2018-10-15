CREATE TABLE `qbao_schema`.`investigation` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `investigation_no` INT(10) UNSIGNED NOT NULL,
  `account_no` VARCHAR(10) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL,
  `question_answer` VARCHAR(1000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `question_no_UNIQUE` (`account_no` ASC, `investigation_no` ASC));

ALTER TABLE `qbao_schema`.`investigation`
CHANGE COLUMN `id` `id` BIGINT(11) NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN `account_no` `account_no` VARCHAR(10) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL ,
ADD COLUMN `user_name` VARCHAR(100) NULL AFTER `account_no`,
ADD COLUMN `email` VARCHAR(255) NULL AFTER `user_name`,
ADD COLUMN `phone` VARCHAR(20) NULL AFTER `email`,
ADD COLUMN `id_no` VARCHAR(20) NULL AFTER `phone`,
ADD COLUMN `nationality` VARCHAR(2) NULL AFTER `id_no`,
ADD COLUMN `gender` VARCHAR(1) NULL COMMENT 'F:女\nM:男' AFTER `nationality`,
ADD COLUMN `age` INT(3) NULL AFTER `gender`;

INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`, `create_time`, `update_time`) VALUES ('IS_IOS_VERIFY', '1', '2017-10-31 00:00:00', '2017-10-31 00:00:00');

ALTER TABLE `qbao_schema`.`investigation`
CHANGE COLUMN `id_no` `card_no` VARCHAR(20) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
ADD COLUMN `card_type` INT(1) NULL COMMENT '0: 身份证\n1: 护照' AFTER `phone`,
ADD COLUMN `is_emailed` TINYINT(1) NULL DEFAULT 0 COMMENT '0: no\n1: did' AFTER `email`;

ALTER TABLE `qbao_schema`.`chat_group`
ADD COLUMN `group_no` VARCHAR(10) NOT NULL AFTER `member_num`;

ALTER TABLE `qbao_schema`.`chat_group_member`
CHANGE COLUMN `group_id` `group_no` VARCHAR(10) NOT NULL COMMENT '群No' ;

ALTER TABLE `qbao_schema`.`user_group`
CHANGE COLUMN `group_id` `group_no` VARCHAR(10) NOT NULL COMMENT '群No' ;

ALTER TABLE `qbao_schema`.`investigation`
CHANGE COLUMN `age` `age` VARCHAR(1) NULL DEFAULT NULL ;

ALTER TABLE `qbao_schema`.`investigation`
CHANGE COLUMN `investigation_no` `investigation_no` INT(10) NOT NULL ,
DROP INDEX `question_no_UNIQUE` ;


ALTER TABLE `qbao_schema`.`investigation`
ADD COLUMN `img1` VARCHAR(256) NULL AFTER `is_emailed`,
ADD COLUMN `img2` VARCHAR(256) NULL AFTER `img1`,
ADD COLUMN `img3` VARCHAR(256) NULL AFTER `img2`;

CREATE TABLE `qbao_schema`.`fund_user` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `activate_type` varchar(1) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` timestamp NULL DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `qbao_schema`.`fund_user`
CHANGE COLUMN `email` `email` VARCHAR(128) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL ,
ADD UNIQUE INDEX `email_UNIQUE` (`email` ASC);

ALTER TABLE `qbao_schema`.`android_package`
ADD COLUMN `source` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '0=ios\n1=android' ;

ALTER TABLE `qbao_schema`.`investigation`
ADD COLUMN `status` VARCHAR(1) NULL DEFAULT 0 AFTER `img3`;

ALTER TABLE `qbao_schema`.`fund_user`
ADD COLUMN `ico_qualification` VARCHAR(1) NULL DEFAULT 0 COMMENT '0:未申请\n1:审核中\n2:审查失败\n3:审查成功' AFTER `update_time`;

CREATE TABLE `qbao_schema`.`user_address` (
  `id` BIGINT(11) NOT NULL,
  `user_address` VARCHAR(128) NULL,
  `qtum_address` VARCHAR(128) NULL,
  `btc_address` VARCHAR(128) NULL,
  `eth_address` VARCHAR(128) NULL,
  `amount` DECIMAL(12,2) NULL,
  `user_id` BIGINT(11) NULL,
  PRIMARY KEY (`id`));
ALTER TABLE `qbao_schema`.`user_address`
ADD COLUMN `create_time` DATETIME NULL AFTER `user_id`,
ADD COLUMN `update_time` DATETIME NULL AFTER `create_time`;
ALTER TABLE `qbao_schema`.`user_address`
CHANGE COLUMN `id` `id` BIGINT(11) NOT NULL AUTO_INCREMENT ;
ALTER TABLE `qbao_schema`.`user_address`
ADD UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC);

INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ('ico_address', 'd4bbf43760d6bf03c185b5c864d104f02b6322ac');

ALTER TABLE `qbao_schema`.`fund_user`
CHANGE COLUMN `password` `password` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL ;

INSERT INTO `qbao_schema`.`wallet_account` (`account_name`, `account_no`, `header`, `activate_type`, `source_type`) VALUES ('customerOne', '1001', '1510797488722.png', '1', '2');
INSERT INTO `qbao_schema`.`wallet_account` (`account_name`, `account_no`, `header`, `activate_type`, `source_type`) VALUES ('groupSystem', '1002', '1510797558870.png', '1', '2');





