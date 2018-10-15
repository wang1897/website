
INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ( 'invite_rewards', '2000');

INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ( 'invite_number', '30');


ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `new_type` TINYINT(1) NULL COMMENT 'False : 老用户 \nTrue : 新用户' AFTER `receive_number`;
