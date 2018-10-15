CREATE TABLE `qbao_schema`.`token_calendar` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `language_type` varchar(10) CHARACTER SET utf8mb4 NOT NULL COMMENT '\n',
  `content` varchar(500) CHARACTER SET utf8mb4 NOT NULL,
  `title` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '\n',
  `status` int(1) NOT NULL COMMENT '已删除 0\n保存为草稿 1\n已发布 2',
  `is_delete` tinyint(1) DEFAULT '0',
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci