ALTER TABLE `qbao_schema`.`t_customer`
CHANGE COLUMN `phone_number` `phone_number` VARCHAR(20) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '手机号码' ;