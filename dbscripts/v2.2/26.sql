ALTER TABLE `qbao_schema`.`sys_wallet_address`
CHANGE COLUMN `key` `seed` VARCHAR(300) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ;
