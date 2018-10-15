ALTER TABLE `qbao_schema`.`smart_contract`
CHANGE COLUMN `contract_decimal` `contract_decimal` INT(4) NULL DEFAULT NULL ;

ALTER TABLE `qbao_schema`.`smart_contract`
CHANGE COLUMN `contract_decimal` `contract_decimal` VARCHAR(4) NULL DEFAULT NULL ;
