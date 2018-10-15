
ALTER TABLE `qbao_schema`.`group_notice`
CHANGE COLUMN `title` `title` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ,
CHANGE COLUMN `content` `content` VARCHAR(2000) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ;

ALTER TABLE `qbao_schema`.`games`
ADD COLUMN `banner` VARCHAR(256) NULL AFTER `update_time`;

