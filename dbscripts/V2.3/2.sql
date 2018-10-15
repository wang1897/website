CREATE TABLE `qbao_schema`.`account_subsidiary` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  `is_receive` INT NOT NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `qbao_schema`.`account_subsidiary`
CHANGE COLUMN `is_receive` `is_receive` INT NOT NULL DEFAULT "0000" COMMENT '是否领取\n0000 都未领取\n1111 都已领取。 以此类推' ;