ALTER TABLE `qbao_schema`.`t_customer`
CHANGE COLUMN `customer_name` `customer_name` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '商户名称' ,
CHANGE COLUMN `customer_des` `customer_des` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '商户描述' ;
