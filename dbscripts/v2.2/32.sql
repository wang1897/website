ALTER TABLE `qbao_schema`.`withdraw`
DROP INDEX `Index_status_unit` ,
ADD INDEX `Index_status_unit` (`status` ASC, `unit` ASC, `exchange_time` ASC);

ALTER TABLE `qbao_schema`.`guess_record`
ADD INDEX `index_guess_number_account` (`guess_number_id` ASC, `account_no` ASC),
ADD INDEX `index_draw_level` (`draw_level` ASC);

ALTER TABLE `qbao_schema`.`guess_rank`
ADD UNIQUE INDEX `index_game_account` (`game_id` ASC, `account_no` ASC),
ADD INDEX `index_amount` (`amount` ASC);

ALTER TABLE `qbao_schema`.`guess_number_game`
CHANGE COLUMN `game_id` `game_id` BIGINT(11) NOT NULL ,
ADD INDEX `index_game_id` (`game_id` ASC);

ALTER TABLE `qbao_schema`.`ticket`
ADD INDEX `index_type` (`type` ASC);

ALTER TABLE `qbao_schema`.`token_calendar`
ADD INDEX `index_language_status` (`language_type` ASC, `is_delete` ASC, `status` ASC);

ALTER TABLE `qbao_schema`.`group_notice`
ADD INDEX `index_group_no` (`group_no` ASC, `is_delete` ASC);
