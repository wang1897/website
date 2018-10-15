CREATE TABLE `qbao_schema`.`wallet_account_assets` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `account_no` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `type` int(2) DEFAULT NULL COMMENT '0=QTUM\n1=ETH',
  `assets` decimal(15,6) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;