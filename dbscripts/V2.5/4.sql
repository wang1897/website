ALTER TABLE `qbao_schema`.`t_qtum_tx_error`
ADD COLUMN `fee` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
ADD COLUMN `gas_limit` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
ADD COLUMN `gas_price` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
ADD COLUMN `fee_per_kb` varchar(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
ADD COLUMN `from_utxo` varchar(15000) COLLATE utf8mb4_unicode_ci DEFAULT NULL;