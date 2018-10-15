ALTER TABLE `qbao_schema`.`wallet_account`
DROP COLUMN `level`;

ALTER TABLE `qbao_schema`.`wallet_account`
CHANGE COLUMN `assets` `assets` DECIMAL(15,6) NULL DEFAULT NULL COMMENT '资产' ;


