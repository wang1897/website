CREATE DATABASE  IF NOT EXISTS `qbao_schema` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `qbao_schema`;

CREATE TABLE `chat_group` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL COMMENT '群名',
  `logo_url` varchar(256) DEFAULT NULL COMMENT '群Logo保存Url',
  `comment` varchar(150) DEFAULT NULL COMMENT '群备注',
  `tag` varchar(45) DEFAULT NULL COMMENT '群标签',
  `update_admin_id` varchar(45) DEFAULT NULL COMMENT '更新Admin',
  `create_admin_id` varchar(45) DEFAULT NULL COMMENT '创建Admin',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '0：有效\n1：删除',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `chat_group_member` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(11) NOT NULL COMMENT '群ID',
  `member_no` varchar(10) NOT NULL COMMENT '群成员AccountNo',
  `display_name` varchar(45) DEFAULT NULL COMMENT '群显示名',
  `role` int(1) DEFAULT NULL COMMENT '成员权限\nNULL：群众\n1：群主\n2：管理\n9：ADMIN',
  `header_url` varchar(256) DEFAULT NULL COMMENT '成员的头像URL',
  `invite_source` varchar(11) DEFAULT NULL COMMENT '邀请人',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否已删除\n0：未删除\n1：删除',
  `is_gap` tinyint(1) DEFAULT '0' COMMENT '是否已禁言\n0：未禁言\n1：禁言',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_contact` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(10) NOT NULL COMMENT '用户accountNo',
  `relationship` int(2) NOT NULL COMMENT '关系列表\n10：发出好友邀请\n11：收到好友邀请\n12：拒绝好友邀请\n20：加为好友\n30：删除好友\n40：拉黑好友\n',
  `contact_no` varchar(10) NOT NULL COMMENT '关系人accountNo',
  `add_friend_date` datetime DEFAULT NULL COMMENT '添加好友的时间',
  `add_blacklist_date` datetime DEFAULT NULL COMMENT '添加黑名单的时间',
  `comment` varchar(100) DEFAULT NULL COMMENT '好友备注',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `display_name` varchar(45) DEFAULT NULL COMMENT '好友屏显名',
  `is_top` tinyint(1) DEFAULT NULL COMMENT '置顶\n1：置顶\n0：非置顶',
  `tag` varchar(100) DEFAULT NULL COMMENT '好友标签',
  `version` bigint(11) NOT NULL DEFAULT '0' COMMENT '更新版本号',
  `no_disturb` tinyint(1) DEFAULT '0' COMMENT '免打扰\n0：正常\n1：免打扰\n',
  PRIMARY KEY (`id`),
  KEY `account_relationship` (`account_no`,`relationship`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_group` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(10) NOT NULL,
  `group_id` bigint(11) NOT NULL,
  `display_name` varchar(45) DEFAULT NULL COMMENT '群本地显示名',
  `header_url` varchar(256) DEFAULT NULL COMMENT '群内显示头像',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否退出\n0：未退出\n1：已退出',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '置顶\n0：不置顶\n1：置顶',
  `version` bigint(11) NOT NULL DEFAULT '0' COMMENT '更新版本号',
  `no_distrub` tinyint(1) DEFAULT NULL COMMENT '免打扰\n1：免打扰\n',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


ALTER TABLE `qbao_schema`.`wallet_account`
  ADD COLUMN `rong_token` VARCHAR(128) NULL AFTER `source_type`,
  ADD COLUMN `rong_version` bigint(11) DEFAULT '0' COMMENT '更新版本号';

ALTER TABLE `qbao_schema`.`user_group`
CHANGE COLUMN `version` `version` BIGINT(11) NULL DEFAULT '0' COMMENT '更新版本号';

ALTER TABLE `qbao_schema`.`user_contact`
CHANGE COLUMN `version` `version` BIGINT(11) NULL DEFAULT '0' COMMENT '更新版本号' ;

ALTER TABLE `qbao_schema`.`chat_group`
ADD COLUMN `max_member` INT(4) NULL DEFAULT '0' COMMENT '最大成员数 上限3000' AFTER `tag`,
ADD COLUMN `lever` INT(1) NULL DEFAULT '0' COMMENT '群级别 0:普通, 1:高级, 2:超级, 3:企业, 4:置顶推广, 9:系统' AFTER `comment`,
ADD COLUMN `member_num` INT(4) NULL DEFAULT '0' COMMENT '群成员数' AFTER `lever`;

INSERT INTO `qbao_schema`.`wallet_account` ( `account_name`, `account_no`, `header`, `activate_type`, `source_type`) VALUES ( 'admin', '1000', '1506573188095.png', '0', '0');
