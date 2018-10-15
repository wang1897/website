ALTER TABLE `qbao_schema`.`wallet_account`
CHANGE COLUMN `level` `level` INT(1) NOT NULL DEFAULT '0' ;

ALTER TABLE `qbao_schema`.`member_level`
CHANGE COLUMN `level` `level` INT(1) NOT NULL ;

ALTER TABLE `qbao_schema`.`chat_group_member`
ADD COLUMN `level` INT(1) NULL DEFAULT 0 COMMENT '会员等级',
ADD COLUMN `level_status` TINYINT(1) NULL DEFAULT 0 COMMENT '会员等级开关';
