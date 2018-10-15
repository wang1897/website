ALTER TABLE `d_qbao_activity`.`t_guess_gamble`
DROP COLUMN `total_amount`,
DROP COLUMN `join_number`;


ALTER TABLE `d_qbao_activity`.`t_guess_gamble`
ADD COLUMN `option1_number` INT(11) NULL AFTER `game_id`,
ADD COLUMN `option2_number` INT(11) NULL AFTER `option1_number`,
ADD COLUMN `option1_amount` DECIMAL(15,6) NULL AFTER `option2_number`,
ADD COLUMN `option2_amount` DECIMAL(15,6) NULL AFTER `option1_amount`;
