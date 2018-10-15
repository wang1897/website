CREATE TABLE `qbao_schema`.`quiz` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `type` bigint(11) DEFAULT NULL COMMENT '类型',
  `question` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '题干',
  `option1` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选项1',
  `option2` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选项2',
  `option3` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选项3',
  `option4` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选项4',
  `answer` int(1) DEFAULT NULL COMMENT '答案',
  `comment` varchar(400) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '答题解释',
  `is_delete` tinyint(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `qbao_schema`.`quiz_answer` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `answer_time` datetime DEFAULT NULL,
  `quiz` bigint(11) DEFAULT NULL,
  `right_answer` int(1) DEFAULT NULL,
  `answer` int(1) DEFAULT NULL,
  `win_token` decimal(15,6) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_account_date` (`account_no`,`answer_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `qbao_schema`.`quiz_type` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '题库类型',
  `win_token` decimal(15,6) DEFAULT NULL COMMENT '获得金额',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


ALTER TABLE `qbao_schema`.`sys_config`
ADD UNIQUE INDEX `SYS_CONFIG_NAME` (`name` ASC);

INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ( 'QUIZ_SIZE', '5');
INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ( 'QUIZ_AWARD','50');

CREATE TABLE `qbao_schema`.`language` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `language_code` VARCHAR(10) NULL,
  `is_default` TINYINT(1) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `message` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `table_ref` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `field_ref` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `code` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `language` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `resource_id` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_message_field` (`language`,`table_ref`,`field_ref`,`resource_id`),
  KEY `idx_message_code` (`language`,`table_ref`,`code`)
);

INSERT INTO `qbao_schema`.`language` (`language_code`,`is_default`,`create_time`,`update_time`) VALUES ('en',0,now(),now());
INSERT INTO `qbao_schema`.`language` (`language_code`,`is_default`,`create_time`,`update_time`) VALUES ('zh',1,now(),now());
INSERT INTO `qbao_schema`.`language` (`language_code`,`is_default`,`create_time`,`update_time`) VALUES ('ko',0,now(),now());


CREATE TABLE `batch_task_complete` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `definition_id` bigint(11) DEFAULT NULL,
  `name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `execute_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL COMMENT '0: active, 1: completed, 2: cancelled, 3: running',
  `class_name` varchar(2000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter1` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter2` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter3` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter4` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter5` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `parameter6` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL,
  `resource_table` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `resource_id` bigint(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `batch_task_index_1` (`definition_id`)
);
