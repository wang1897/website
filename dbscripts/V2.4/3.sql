/*exchange_log类型配置表*/
CREATE TABLE `qbao_schema`.`t_exchange_type` (
  `id` BIGINT(11) NOT NULL,
  `create_time` DATETIME NULL DEFAULT NULL,
  `update_time` DATETIME NULL DEFAULT NULL,
  `type_name` VARCHAR(10) NULL DEFAULT NULL,
  `symbol` VARCHAR(1) NULL DEFAULT NULL,
  `icon` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY (`id`));

INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('0', '提币 ', '-');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('1', '充值', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('2', '发红包', '-');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('3', '收红包', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('4', '活动', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('5', '新人奖励', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('6', '红包退款', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('7', '新人红包', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('8', '手续费', '-');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('9', '建群支付', '-');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('10', 'QBE兑换扣除', '-');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('11', 'QBE兑换QBT', '-');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('12', '提币手续费入账', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('13', '提币复式记账用', '-');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('14', '充值复式记账用', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('15', '游戏中奖', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('16', '每日答题奖励', '+');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('17', '投注金额', '-');
INSERT INTO `qbao_schema`.`t_exchange_type` (`id`, `type_name`, `symbol`) VALUES ('18', '投注退款', '+');
