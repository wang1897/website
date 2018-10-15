ALTER TABLE `qbao_schema`.`smart_contract`
ADD COLUMN `value` DECIMAL(15,2) NULL AFTER `contract_decimal`,
ADD COLUMN `api_address` VARCHAR(256) NULL AFTER `value`;


ALTER TABLE `qbao_schema`.`smart_contract`
CHANGE COLUMN `value` `value` DECIMAL(24,12) NULL DEFAULT NULL ;



