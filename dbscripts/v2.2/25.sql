ALTER TABLE `qbao_schema`.`token_calendar`
CHANGE COLUMN `content` `content` VARCHAR(500) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NOT NULL ,
CHANGE COLUMN `title` `title` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL ;

ALTER TABLE `qbao_schema`.`ticket`
CHANGE COLUMN `question` `question` VARCHAR(4000) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '问题' ,
CHANGE COLUMN `answer` `answer` VARCHAR(500) CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci' NULL DEFAULT NULL COMMENT '回复' ;