ALTER TABLE `qbao_schema`.`smart_contract`
DROP COLUMN `type`;


ALTER TABLE `qbao_schema`.`smart_contract`
ADD COLUMN `type` INT(2) NOT NULL DEFAULT 0 AFTER `in_service`;
