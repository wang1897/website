ALTER TABLE `qbao_schema`.`chat_group`
ADD COLUMN `confirm_status` INT(1) NULL DEFAULT 0 COMMENT '进群验证 开关\n0 关闭（默认）\n1 身份验证\n2 口令验证';

ALTER TABLE `qbao_schema`.`chat_group`
ADD COLUMN `command_info` VARCHAR(50) NULL DEFAULT NULL COMMENT '加群口令';
/*

INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'ko', '중국', '1');
INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'ko', '한국', '2');
INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'ko', '미국', '3');
INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'en', 'China', '1');
INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'en', 'Korea', '2');
INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'en', 'America', '3');
INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'zh', '中国', '1');
INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'zh', '韩国', '2');
INSERT INTO `qbao_schema`.`message` (`table_ref`, `field_ref`, `language`, `message`, `resource_id`) VALUES ('t_country_information', 'country_name', 'zh', '美国', '3');
*/

