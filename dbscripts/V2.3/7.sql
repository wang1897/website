ALTER TABLE `qbao_schema`.`batch_task_complete`
ADD COLUMN `task_id` BIGINT(11) NULL AFTER `resource_id`;