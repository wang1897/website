ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `level` BIGINT(1) NOT NULL DEFAULT 0 AFTER `is_daka`;