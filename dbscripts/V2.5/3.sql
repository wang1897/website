CREATE TABLE `t_qtum_tx_error` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `tx_hash` varchar(80) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tx_raw` longtext COLLATE utf8mb4_unicode_ci,
  `error_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `error_message` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `account_no` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL
  PRIMARY KEY (`id`),
  KEY `idx_tx_hash` (`tx_hash`),
  KEY `idx_account_no` (`account_no`)
)