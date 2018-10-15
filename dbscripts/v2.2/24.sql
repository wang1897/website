ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `authority` TINYINT(1) NULL DEFAULT '0' COMMENT '是否有内侧权限（默认没有）\n有 1\n没有 0' AFTER `bigger_header`;
ALTER TABLE `qbao_schema`.`sys_wallet_address`
ADD COLUMN `qtum_left_amount` DECIMAL(18,6) NULL AFTER `qbt_left_amount`;
