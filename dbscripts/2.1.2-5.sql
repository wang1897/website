ALTER TABLE `qbao_schema`.`exchange_log` 
DROP INDEX `exchange_log_index1` ,
ADD INDEX `exchange_log_index1` (`type` ASC, `is_deleted` ASC),
ADD INDEX `exchange_log_index2` (`account_no` ASC);

set sql_safe_updates = 0;
update chat_group set max_member = 1000, level = 1 where group_no in('Q501327','Q026962','Q124628','Q119335','Q032797');
ADD INDEX `exchange_log_index2` (`account_no` ASC)


ALTER TABLE `qbao_schema1`.`wallet_account`
ADD INDEX `wallet_account_index1` (`invite_code` ASC),
ADD INDEX `wallet_account_index2` (`share_code` ASC);
