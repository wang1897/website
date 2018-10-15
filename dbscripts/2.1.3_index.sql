ALTER TABLE `qbao_schema`.`user_contact`
ADD INDEX `INDEX_ACCOUNTNO_CONTACT_NO` (`account_no` ASC, `contact_no` ASC, `update_time` ASC);

ALTER TABLE `qbao_schema`.`account_balance`
ADD INDEX `INDEX_ACCOUNTNO` (`account_no` ASC);


ALTER TABLE `qbao_schema`.`chat_group`
ADD INDEX `INDEX_GROUPNO` (`group_no` ASC, `update_time` ASC);

ALTER TABLE `qbao_schema`.`user_group`
ADD INDEX `INDEX_GROUPNO` (`group_no` ASC),
ADD INDEX `INDEX_ACCOUNTNO` (`account_no` ASC, `update_time` ASC);

ALTER TABLE `qbao_schema`.`get_red_packet`
ADD INDEX `INDEX_ACCOUNT` (`account_no` ASC);
