ALTER TABLE `qbao_schema`.`clearance`
ADD COLUMN `account_day` DATETIME NULL COMMENT '对账日' AFTER `update_time`;

ALTER TABLE `qbao_schema`.`clearance`
ADD COLUMN `account_remark` VARCHAR(500) NULL COMMENT '对账备注' AFTER `account_day`;

ALTER TABLE `qbao_schema`.`clearance`
ADD COLUMN `account_status` INT(2) NULL COMMENT '对账状态 0:无需对账\n1:待处理\n2:已处理' AFTER `account_remark`;


INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ( 'CLEARANCE_WARNING_MOBILE_LIST', '13917547766');

ALTER TABLE `qbao_schema`.`t_customer`
ADD COLUMN `first_login` TINYINT(1) NOT NULL COMMENT '是否首次登陆 默认1=是 0=不是'  DEFAULT 1 AFTER `last_login_time`;

