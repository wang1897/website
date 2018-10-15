
ALTER TABLE `qbao_schema`.`smart_contract`
ADD COLUMN `withdraw_limit` DECIMAL(18,6) NULL AFTER `withdraw_fee`,
ADD COLUMN `withdraw_one_limit` DECIMAL(18,6) NULL AFTER `withdraw_limit`;


