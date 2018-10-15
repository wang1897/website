CREATE TABLE `qbao_schema`.`t_customer_qbao` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  `customer_no` VARCHAR(10) NOT NULL COMMENT '商户编号',
  `qbao_id` VARCHAR(10) NOT NULL COMMENT 'QBAO ID',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_customer_no` (`customer_no` ASC));

