-- created by lilangfeng 2018／2／1
CREATE TABLE `media` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `language_type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notes` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `status` int(1) DEFAULT NULL COMMENT '0表示归档\n1表示使用中，ios和Android',
  `click_number` bigint(11) DEFAULT '0',
  `icon` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `url` varchar(256) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `sequence` int(3) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci

