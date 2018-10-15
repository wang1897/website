ALTER TABLE `qbao_schema`.`account_subsidiary`
ADD COLUMN `account_no` VARCHAR(10) CHARACTER SET 'utf8' NOT NULL DEFAULT '\"0000\"' AFTER `update_time`,
ADD UNIQUE INDEX `ind_unique_account_no` (`account_no` ASC);

ALTER TABLE `qbao_schema`.`account_subsidiary`
CHANGE COLUMN `is_receive` `is_receive` VARCHAR(10) CHARACTER SET 'utf8' NULL DEFAULT '"11111"' COMMENT '是否可以领取奖励（五个奖励为例）\n00000 都不可以领取 \n11111   都可以领取\n以此类推' ;

ALTER TABLE `qbao_schema`.`token_calendar`
ADD COLUMN `will_push` TINYINT(1) NULL DEFAULT 0 COMMENT '是否需要推送，默认0\n0：不需要\n1：需要' AFTER `end_time`;

CREATE TABLE `qbao_schema`.`inviting_reward` (
  `id` BIGINT(11) NOT NULL,
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  `reward_name` VARCHAR(10) CHARACTER SET 'utf8' NOT NULL,
  `reward_amount` DECIMAL(15,6) NULL DEFAULT NULL,
  `unit` BIGINT(11) NOT NULL,
  PRIMARY KEY (`id`));

INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('1','2018-02-03 19:33:15', '2018-02-03 19:33:15', '新用户邀请奖励', '1000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('2','2018-02-03 19:33:15', '2018-02-03 19:33:15', '邀请人奖励', '500', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('3','2018-02-03 19:33:15', '2018-02-03 19:33:15', '上传头像奖励', '500', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('4','2018-02-03 19:33:15', '2018-02-03 19:33:15', '用户出师奖励', '2000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('5','2018-02-03 19:33:15', '2018-02-03 19:33:15', '邀请人出师奖励', '1000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('6','2018-02-03 19:33:15', '2018-02-03 19:33:15', '用户钱包成就奖励', '5000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('7','2018-02-03 19:33:15', '2018-02-03 19:33:15', '邀请人钱包成就奖励', '2000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('8','2018-02-03 19:33:15', '2018-02-03 19:33:15', '四位中三个', '25000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('9','2018-02-03 19:33:15', '2018-02-03 19:33:15', '六位中三个', '10000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('10','2018-02-03 19:33:15', '2018-02-03 19:33:15', '六位中四个', '30000', '20');
INSERT INTO `qbao_schema`.`inviting_reward` (`id`,`create_time`, `update_time`, `reward_name`, `reward_amount`, `unit`) VALUES ('11','2018-02-03 19:33:15', '2018-02-03 19:33:15', '六位中五个', '100000', '20');
