ALTER TABLE `qbao_schema`.`t_customer`
CHANGE COLUMN `address` `address` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '地址信息' ;

ALTER TABLE `qbao_schema`.`t_country_information`
ADD COLUMN `country_name` VARCHAR(10) NULL DEFAULT NULL COMMENT '国家名称' AFTER `tel_number`;