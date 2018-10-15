
/*投注小游戏表*/
CREATE TABLE `d_qbao_activity`.`t_guess_gamble` (
  `id` bigint(11) NOT NULL DEFAULT '0',
  `title` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '题目',
  `content` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容',
  `option1` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选项A',
  `option2` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '选项B',
  `open_time` datetime DEFAULT NULL COMMENT '开盘时间',
  `close_time` datetime DEFAULT NULL COMMENT '封盘时间',
  `luck_time` datetime DEFAULT NULL COMMENT '开奖时间',
  `luck_option` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '中奖选项',
  `status` int(1) DEFAULT NULL COMMENT '状态（1=进行中／2=已封盘／3=已开奖／4=已流盘）',
  `amount` decimal(15,6) DEFAULT NULL COMMENT '竞猜额度qbe',
  `is_min` tinyint(1) DEFAULT NULL COMMENT '竞猜额度false=最大／true=最小',
  `option1_number` int(11) DEFAULT NULL COMMENT '选项A的参与人数',
  `option2_number` int(11) DEFAULT NULL COMMENT '选项B的参与人数',
  `option1_amount` decimal(15,6) DEFAULT NULL COMMENT '选项A的qbe量',
  `option2_amount` decimal(15,6) DEFAULT NULL COMMENT '选项B的qbe量',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `game_id` bigint(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*用户参与投注小游戏表*/
CREATE TABLE `d_qbao_activity`.`t_join_gamble` (
  `id` bigint(11) NOT NULL DEFAULT '0',
  `account_no` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参与用户',
  `gamble_id` bigint(11) DEFAULT NULL COMMENT '投注小游戏id',
  `option` char(1) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户投注选项',
  `amount` decimal(15,6) DEFAULT NULL COMMENT '用户投注金额',
  `is_luck` tinyint(1) DEFAULT NULL COMMENT '是否是中奖',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*投注小游戏表排行表*/
CREATE TABLE `d_qbao_activity`.`t_gameble_rank` (
  `id` bigint(11) NOT NULL DEFAULT '0',
  `account_no` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户no',
  `amount` decimal(15,6) DEFAULT NULL COMMENT '投注之后总金额\n注意：输掉投注，金额为负数',
  `gamble_id` bigint(11) DEFAULT NULL COMMENT '投注游戏id',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

  /*游戏分类表结构修改*//*
  ALTER TABLE `qbao_schema`.`games`
DROP COLUMN `unit`,
DROP COLUMN `ko_name`,
DROP COLUMN `en_name`,
DROP COLUMN `banner`,
DROP COLUMN `en_url`,
CHANGE COLUMN `zh_name` `name` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '游戏名称' ,
CHANGE COLUMN `zh_url` `banner_url` VARCHAR(256) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT 'bannerURL' ,
CHANGE COLUMN `ko_url` `game_url` VARCHAR(256) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '游戏URL' ;*/