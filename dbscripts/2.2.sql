CREATE TABLE `qbao_schema`.`group_notice` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `group_no` VARCHAR(10) NOT NULL,
  `title` VARCHAR(128) NULL,
  `content` VARCHAR(526) NULL,
  `create_by` VARCHAR(10) NOT NULL,
  `write_time` DATETIME NULL,
  `is_delete` TINYINT(1) NOT NULL DEFAULT 0,
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ( 'build_group_qbt_fee', '2');
INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ( 'build_group_qbe_fee', '500');

ALTER TABLE `qbao_schema`.`account_balance`
DROP INDEX `UNIQUE_ACCOUNTNO_UNIT` ,
ADD UNIQUE INDEX `UNIQUE_ACCOUNTNO_UNIT` (`account_no` ASC, `unit` ASC);