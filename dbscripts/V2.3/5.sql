ALTER TABLE `qbao_schema`.`quiz`
ADD INDEX `idx_type` (`type` ASC);


ALTER TABLE `qbao_schema`.`quiz_answer`
ADD INDEX `idx_account_no_answer` (`account_no` ASC, `answer` ASC);

ALTER TABLE `qbao_schema`.`quiz_rank`
ADD INDEX `idx_account_no` (`account_no` ASC);
