ALTER TABLE `qbao_schema`.`account_subsidiary`
ADD COLUMN `is_joined` TINYINT(1) NULL DEFAULT '0' COMMENT '是否参加过问卷调查\n1 参加过\n0 未参加过' AFTER `had_withdraw`;

CREATE TABLE `qbao_schema`.`questionnaire` (
  `id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `update_time` DATETIME NULL DEFAULT NULL,
  `create_time` DATETIME NULL DEFAULT NULL,
  `account_no` VARCHAR(10) NOT NULL,
  `question1` INT(1) NULL DEFAULT NULL COMMENT '是否第一次使用数字钱包\n1 是\n0 否',
  `question2` INT(1) NULL DEFAULT NULL COMMENT '是否了解去中心化的含义\n1 是\n0 否',
  `question3` INT(1) NULL DEFAULT NULL COMMENT '是否知道助记词重要性\n1 是\n0 否',
  `question4` INT(1) NULL DEFAULT NULL COMMENT '是否关注了我们的公众号\n1 是\n0 否',
  `question5` VARCHAR(20) NULL DEFAULT NULL COMMENT '参加了我们以下哪些社区（可多选）\n0 Qbao 中国社区微信群\n1 Qbao 讨论QQ群\n2 Telegram Qbao Community\n3 Facebook\n4 Twitter\n5 Kakao\n6 未参加任何社区',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `idx_account_no` (`account_no` ASC));