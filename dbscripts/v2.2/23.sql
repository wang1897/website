ALTER TABLE `qbao_schema`.`games`
CHANGE COLUMN `name` `zh_name` VARCHAR(128) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '游戏名称' ,
ADD COLUMN `en_name` VARCHAR(128) NULL ,
ADD COLUMN `ko_name` VARCHAR(128) NULL ;

ALTER TABLE `qbao_schema`.`games`
ADD COLUMN `method` VARCHAR(256) NULL ;

ALTER TABLE `qbao_schema`.`guess_number_game`
CHANGE COLUMN `name` `zh_name` VARCHAR(45) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_unicode_ci' NULL DEFAULT NULL COMMENT '名称（二十字以内）' ,
ADD COLUMN `en_name` VARCHAR(45) NULL ,
ADD COLUMN `ko_name` VARCHAR(45) NULL ;

INSERT INTO `qbao_schema`.`sys_config` ( `name`, `value`) VALUES ('GAME_FLAG', 'true');

