UPDATE `qbao_schema`.`t_country_information` SET `country_name`='中国' WHERE `id`='1';
UPDATE `qbao_schema`.`t_country_information` SET `country_name`='韩国' WHERE `id`='2';
UPDATE `qbao_schema`.`t_country_information` SET `country_name`='美国' WHERE `id`='3';

INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ('INVITEING_REWARD_END', '2018-04-02 00:00:00');