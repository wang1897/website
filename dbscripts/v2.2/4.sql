ALTER TABLE `qbao_schema`.`wallet_address`
DROP FOREIGN KEY `FKe4or4gp182i0t3563w33f8fdv`;
ALTER TABLE `qbao_schema`.`wallet_address`
CHANGE COLUMN `account_id` `account_id` BIGINT(11) NULL ;
ALTER TABLE `qbao_schema`.`wallet_address`
ADD CONSTRAINT `FKe4or4gp182i0t3563w33f8fdv`
  FOREIGN KEY (`account_id`)
  REFERENCES `qbao_schema`.`wallet_account` (`id`);


ALTER TABLE `qbao_schema`.`batch_task`
DROP INDEX `batch_task_index_3` ,
ADD INDEX `batch_task_index_3` (`resource_table` ASC, `resource_id` ASC, `name` ASC);


