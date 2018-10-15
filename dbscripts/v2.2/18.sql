CREATE TABLE `qbao_schema`.`games` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(128) NULL COMMENT '游戏名称',
  `zh_url` VARCHAR(256) NULL COMMENT '中文URL',
  `en_url` VARCHAR(256) NULL COMMENT '英文URL',
  `ko_url` VARCHAR(256) NULL COMMENT '韩文URL',
  `is_show` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否显示',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));


CREATE TABLE `qbao_schema`.`guess_number_game` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称（二十字以内）',
  `game_id` bigint(11) DEFAULT NULL,
  `game_start_time` datetime DEFAULT NULL COMMENT '活动开始时间',
  `game_end_time` datetime DEFAULT NULL COMMENT '活动结束时间',
  `luck_number` int(11) DEFAULT NULL COMMENT '开奖数字',
  `luck_time` datetime DEFAULT NULL COMMENT '开奖时间',
  `unit` bigint(11) DEFAULT NULL COMMENT '币种',
  `join_number` int(11) DEFAULT NULL COMMENT '参与人数',
  `total_amount` decimal(15,6) DEFAULT NULL COMMENT '总金额',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0-未开始\n1-进行中\n2-待开奖\n3-已结束',
  `is_show` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否显示 ',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否失效0-未失效／1失效',
  `special_award` int(4) DEFAULT NULL COMMENT '特等奖',
  `frist_prize` int(4) DEFAULT NULL COMMENT '一等奖',
  `second_prize` int(4) DEFAULT NULL COMMENT '二等奖',
  `third_prize` int(4) DEFAULT NULL COMMENT '三等奖',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `qbao_schema`.`guess_record` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '钱包id',
  `unit` bigint(11) DEFAULT NULL COMMENT '币种',
  `guess_number_id` bigint(11) DEFAULT NULL COMMENT '竞猜游戏id',
  `draw_number` int(11) DEFAULT NULL COMMENT '抽奖数字',
  `draw_time` datetime DEFAULT NULL COMMENT '抽奖时间',
  `draw_level` int(11) DEFAULT NULL COMMENT '中奖等级',
  `draw_amount` decimal(15,6) DEFAULT NULL COMMENT '中奖金额',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


ALTER TABLE `qbao_schema`.`withdraw`
ADD COLUMN `fee_per_kb` DECIMAL(18,8) NULL AFTER `update_time`;

ALTER TABLE `qbao_schema`.`smart_contract`
ADD COLUMN  `withdraw_day_limit` DECIMAL(18,6) NULL AFTER `withdraw_one_limit`,
ADD COLUMN `in_service` TINYINT(1) NULL AFTER `withdraw_day_limit`;


