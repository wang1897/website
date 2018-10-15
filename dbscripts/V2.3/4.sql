
INSERT INTO `qbao_schema`.`sys_config` (`name`, `value`) VALUES ('INVITEING_REWARD_START', '2018-03-01 00:00:00');

UPDATE `qbao_schema`.`inviting_reward` SET `reward_name`='用户六位中三个' WHERE `id`='9';
UPDATE `qbao_schema`.`inviting_reward` SET `reward_name`='用户四位中三个' WHERE `id`='8';
UPDATE `qbao_schema`.`inviting_reward` SET `reward_name`='用户六位中四个' WHERE `id`='10';
UPDATE `qbao_schema`.`inviting_reward` SET `reward_name`='用户六位中五个' WHERE `id`='11';
INSERT INTO `qbao_schema`.`inviting_reward` (`id`, `create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('12', '2018-02-03 19:33:15', '2018-02-03 19:33:15', '邀请人四位中三个', '25000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`, `create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('13', '2018-02-03 19:33:15', '2018-02-03 19:33:15', '邀请人六位中三个', '10000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`, `create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('14', '2018-02-03 19:33:15', '2018-02-03 19:33:15', '邀请人六位中四个', '30000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`, `create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('15', '2018-02-03 19:33:15', '2018-02-03 19:33:15', '邀请人六位中五个', '100000', '20');

CREATE TABLE `qbao_schema`.`quiz_rank` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `account_no` VARCHAR(10) NULL,
  `answer_quiz` INT NULL COMMENT '用户累计答题总数',
  `right_quiz` INT NULL COMMENT '用户累计答对题数',
  `win_token` DECIMAL(15,6) NULL COMMENT '累计获得qbe数量',
  `create_time` DATETIME NULL,
  `update_time` DATETIME NULL,
  PRIMARY KEY (`id`));

ALTER TABLE `qbao_schema`.`account_subsidiary`
ADD COLUMN `had_withdraw` TINYINT(1) NULL DEFAULT 0 COMMENT '是否提过币\n1:踢过\n0:没提' AFTER `update_time`;

