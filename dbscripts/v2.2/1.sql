ALTER SCHEMA `qbao_schema`  DEFAULT CHARACTER SET utf8mb4  DEFAULT COLLATE utf8mb4_unicode_ci ;


/*群标签*/
CREATE TABLE `qbao_schema`.`group_tags` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `sequence` INT(4) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));

/*群标签*/
CREATE TABLE `qbao_schema`.`group_of_tags` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `group_no` VARCHAR(10) NOT NULL,
  `tag` BIGINT(11) NOT NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

/*群的标签索引*/
ALTER TABLE `qbao_schema`.`group_of_tags`
ADD INDEX `INDEX_GROUP_ID` (`group_no` ASC);

ALTER TABLE `qbao_schema`.`group_of_tags`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

ALTER TABLE `qbao_schema`.`group_tags`
CHARACTER SET = utf8mb4 , COLLATE = utf8mb4_unicode_ci ;

-- created by lilangfeng update 2018/2/1
CREATE TABLE `qbao_schema`.`ticket` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(10) CHARACTER SET utf8mb4 NOT NULL COMMENT '提问人员account_no,对应wallet_account表中的account_no',
  `type` bigint(11) NOT NULL COMMENT '更改进度类型',
  `title` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '标题',
  `question` varchar(4000) CHARACTER SET utf8mb4 NOT NULL COMMENT '问题. ',
  `answer` varchar(500) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '回复',
  `picture1` varchar(150) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '图片1',
  `picture2` varchar(150) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '图片2',
  `picture3` varchar(150) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '图片3',
  `picture4` varchar(150) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '图片4',
  `answer_by` varchar(10) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT ' 回复人员：对应admin_accout表中的accountNo',
  `question_time` datetime DEFAULT NULL COMMENT '提问时间',
  `answer_time` datetime DEFAULT NULL COMMENT '回复时间',
  `status` int(1) DEFAULT NULL COMMENT '状态：\n4.已处理\n3.待解决\n2.处理中\n1.不处理',
  `source` int(1) DEFAULT NULL COMMENT '来源 0:IOS / 1:安卓',
  `source_version` varchar(15) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '版本类型 ios／安卓',
  `qbao_version` varchar(30) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'qbao版本',
  `machine` varchar(20) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '安卓机型',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- created by lilangfeng update 2018/2/1
CREATE TABLE `qbao_schema`.`ticket_type` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '1.功能建议\n2.bug上报\n3.联系业务\n\n\n\n',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE `qbao_schema`.`group_tags`
CHANGE COLUMN `name` `name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL ;

CREATE TABLE `qbao_schema`.`red_packet_event` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `group_no` VARCHAR(10) NULL,
  `start_time` DATETIME NULL,
  `end_time` DATETIME NULL,
  `daily` INT NULL,
  `minutes` INT NULL,
  `number` INT NULL,
  `expire__time` DATETIME NULL,
  `unit` BIGINT(11) NULL,
  `amount` DECIMAL(15,6) NULL,
  `red_type` INT NULL,
  `red_number` INT(4) NULL,
  `red_comment` VARCHAR(50) NULL,
  `event_info` VARCHAR(128) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `qbao_schema`.`red_packet_event`
CHANGE COLUMN `expire__time` `expire_time` DATETIME NULL DEFAULT NULL ;

ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `invite_qbe` DECIMAL(15,6) NOT NULL DEFAULT 2000 AFTER `invited_daily`;
/*
ALTER TABLE `qbao_schema`.`red_packet_event`
CHANGE COLUMN `expire_time` `expire_time` TIME NULL DEFAULT NULL ;*/




