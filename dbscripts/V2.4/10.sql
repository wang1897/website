CREATE TABLE `qbao_schema`.`withdraw_log` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `withdraw_id` bigint(11) NOT NULL ,
  `account_no` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `exchange_time` datetime DEFAULT NULL,
  `amount` decimal(18,6) DEFAULT NULL,
  `to_address` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `from_address` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit` bigint(11) DEFAULT NULL,
  `fee_unit` bigint(11) DEFAULT NULL,
  `fee` decimal(18,6) DEFAULT NULL,
  `transaction_hash` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '0:applied\n1:wating\n2:exchanged\n3:failed\n4:pending',
  `apply_time` datetime DEFAULT NULL,
  `confirm_time` datetime DEFAULT NULL,
  `service_pool` bigint(11) DEFAULT NULL,
  `exchange_no` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `fee_per_kb` decimal(18,8) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Index_status_unit` (`status`,`unit`,`exchange_time`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;