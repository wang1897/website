CREATE TABLE `qbao_schema`.`t_customer` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `logo` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商户图片',
  `customer_name` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商户名称',
  `address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址信息',
  `country_info_id` bigint(11) DEFAULT NULL COMMENT '商户所在国家信息id',
  `customer_des` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商户描述',
  `status` int(1) DEFAULT NULL COMMENT '状态\n1 有效\n0 无效',
  `customer_no` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商户编号',
  `customer_uuid` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'uuid\n',
  `generate_string` varchar(16) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '随机字符串',
  `credit_card` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '银行卡',
  `time_zone_id` bigint(11) DEFAULT NULL COMMENT '关联时区id',
  `password` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商户默认密码',
  `phone_number` varchar(15) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `cancellation_time` DATETIME NULL DEFAULT NULL COMMENT '注销时间',
  `qr_code_pic` VARCHAR(200) NULL DEFAULT NULL COMMENT '二维码图片地址',
  `qr_code_url` VARCHAR(200) NULL DEFAULT NULL COMMENT '用于生成二维码的商户url',
  `last_login_time` DATETIME NULL DEFAULT NULL COMMENT '最近一次登陆时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_no` (`customer_no`),
  KEY `idx_customer_name` (`customer_name`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `qbao_schema`.`t_time_zone` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `name` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '时区名称',
  `offset` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '格林尼治时间 偏移量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `qbao_schema`.`t_time_zone` (`create_time`, `update_time`, `name`, `offset`) VALUES ('2018-01-24 18:01:00', '2018-01-24 18:01:00', 'Asia/Seoul', '+9:00');
INSERT INTO `qbao_schema`.`t_time_zone` (`create_time`, `update_time`, `name`, `offset`) VALUES ('2018-01-24 18:01:00', '2018-01-24 18:01:00', 'Asia/Shanghai', '+8:00');

CREATE TABLE `qbao_schema`.`t_country_information` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `country` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '国家',
  `tel_number` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '区号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


