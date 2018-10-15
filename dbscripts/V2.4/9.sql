ALTER TABLE `d_qbao_activity`.`t_join_gamble`
ADD INDEX `idx_gambleId` (`gamble_id` ASC);


ALTER TABLE `d_qbao_activity`.`t_gameble_rank`
DROP INDEX `idx_createTime` ,
ADD INDEX `idx_accountNo` (`account_no` ASC);