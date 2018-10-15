ALTER TABLE `d_qbao_activity`.`t_join_gamble`
CHANGE COLUMN `option` `account_option` CHAR(1) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '用户投注选项' ;
