ALTER TABLE `qbao_schema`.`guess_record`
CHANGE COLUMN `draw_number` `draw_number` VARCHAR(10) NULL DEFAULT NULL COMMENT '抽奖数字' ;
ALTER TABLE `qbao_schema`.`ticket`
CHANGE COLUMN `question` `question` VARCHAR(2000) CHARACTER SET 'utf8mb4' NOT NULL COMMENT '问题. ' ;

ALTER TABLE `qbao_schema`.`guess_number_game`
CHANGE COLUMN `luck_number` `luck_number` VARCHAR(10) NULL DEFAULT NULL COMMENT '开奖数字' ;
