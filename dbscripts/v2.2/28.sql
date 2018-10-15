CREATE TABLE `qbao_schema`.`guess_rank` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `game_id` BIGINT(11) NULL,
  `account_no` VARCHAR(10) NULL,
  `amount` DECIMAL(15,6) NULL,
  `join_number` INT NULL,
  `award_number` INT NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));
