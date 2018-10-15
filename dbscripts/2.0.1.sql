ALTER TABLE `qbao_schema`.`chat_group`
ADD COLUMN `limit_unit` BIGINT(11) NULL AFTER `group_no`,
ADD COLUMN `limit_amount` INT NULL AFTER `limit_unit`;

ALTER TABLE `qbao_schema`.`chat_group`
CHANGE COLUMN `lever` `level` INT(1) NULL DEFAULT NULL COMMENT '群级别 0:普通, 1:高级, 2:超级, 3:企业, 4:置顶推广, 9:系统' ;


INSERT INTO `qbao_schema`.`wallet_account` (`account_name`, `account_no`, `header`) VALUES ('customerOne', '1001', '1510797488722.png');
INSERT INTO `qbao_schema`.`wallet_account` (`account_name`, `account_no`, `header`) VALUES ('groupSystem', '1002', '1510797558870.png');

ALTER TABLE `qbao_schema`.`chat_group`
CHANGE COLUMN `comment` `comment` VARCHAR(255) CHARACTER SET 'utf8' NULL DEFAULT NULL COMMENT '群备注' ;

ALTER TABLE `qbao_schema`.`chat_group_member`
ADD UNIQUE INDEX `UNIQUE_GROUPNO_MEMBERNO` (`group_no` ASC, `member_no` ASC);

INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ('CUSTOMER_SERVICE_URL_ZN', 'http://m.tb.cn/x.Ctlkw');

INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ('CUSTOMER_SERVICE_URL_EN', 'http://m.tb.cn/x.B3jg8');

INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ('CUSTOMER_SERVICE_URL_KO', 'http://m.tb.cn/x.B3gkM');

