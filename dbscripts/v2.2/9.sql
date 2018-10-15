ALTER TABLE `qbao_schema`.`wallet_account`
ADD COLUMN `level_status` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '等级控制开关' AFTER `level`;


