CREATE TABLE `qbao_schema`.`member_level` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  `level` INT NOT NULL,
  `money` INT NOT NULL,
  PRIMARY KEY (`id`));


