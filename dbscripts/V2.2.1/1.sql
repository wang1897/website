
/*添加区块设置*/
ALTER TABLE `qbao_schema`.`guess_number_game`
ADD COLUMN `begin_block` INT(8) NULL AFTER `update_time`,
ADD COLUMN `end_block` INT(8) NULL AFTER `begin_block`,
ADD COLUMN `luck_block` INT(8) NULL AFTER `end_block`;

/*添加竞猜多币种*/
CREATE TABLE `qbao_schema`.`guess_unit` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `guess_id` BIGINT(11) NULL,
  `unit` BIGINT(11) NULL,
  `amount` DECIMAL(15,6) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

/*因添加多币种，这些字段需重设一张表对应竞猜活动的币种*/
ALTER TABLE `qbao_schema`.`guess_number_game`
DROP COLUMN `third_amount`,
DROP COLUMN `second_amount`,
DROP COLUMN `first_amount`,
DROP COLUMN `special_amount`,
DROP COLUMN `third_prize`,
DROP COLUMN `second_prize`,
DROP COLUMN `frist_prize`,
DROP COLUMN `special_award`,
DROP COLUMN `status`,
DROP COLUMN `total_amount`;

/*删除竞猜总排行表*/
DROP TABLE `qbao_schema`.`guess_rank`;

/*竞猜对应多币种的奖池分配值*/
CREATE TABLE `qbao_schema`.`guess_award` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `guess_id` BIGINT(11) NULL,
  `unit` BIGINT(11) NULL,
  `special_number` INT(7) NULL,
  `frist_number` INT(7) NULL,
  `second_number` INT(7) NULL,
  `third_number` INT(7) NULL,
  `fourth_number` INT(7) NULL,
  `special_award` DECIMAL(15,6) NULL,
  `frist_award` DECIMAL(15,6) NULL,
  `second_award` DECIMAL(15,6) NULL,
  `third_award` DECIMAL(15,6) NULL,
  `fourth_award` DECIMAL(15,6) NULL,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

  /*删除竞猜记录币种和中奖金额*/
  ALTER TABLE `qbao_schema`.`guess_record`
DROP COLUMN `draw_amount`,
DROP COLUMN `unit`;



