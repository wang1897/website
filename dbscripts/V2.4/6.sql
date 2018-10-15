ALTER TABLE `d_qbao_activity`.`t_join_gamble`
ADD COLUMN `win_amount` DECIMAL(15,6) NULL AFTER `update_time`;

ALTER TABLE `d_qbao_activity`.`t_join_gamble`
ADD INDEX `idx_accountNo` (`account_no` ASC);


ALTER TABLE `d_qbao_activity`.`t_gameble_rank`
ADD INDEX `idx_createTime` (`create_time` ASC);

