
ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `sys_black` TINYINT(1) NOT NULL DEFAULT 0 ;



INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ('DEFAULT_API_ADDRESS_END_TIME', '2020-01-01 00:00:00');

INSERT INTO `qbao_schema`.`sys_config` ( `name`,`value`) VALUES ( 'DEFAULT_EXCHANGE_QBE','10000');

INSERT INTO `qbao_schema`.`sys_config` ( `name`,`value`) VALUES ( 'SUM_EXCHANGE_QBT_AMOUNT','220000');




