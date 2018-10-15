CREATE DATABASE  IF NOT EXISTS `qbao_schema` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `qbao_schema`;


CREATE TABLE `password_record` (
  `account_id` bigint(11) DEFAULT NULL,
  `result` varchar(45) DEFAULT NULL COMMENT '该记录是否执行成功  成功=success ／ 失败=error',
  `lapse_type` varchar(45) DEFAULT NULL COMMENT '是否失效。 0= 失效 ／ 1=有效',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `code` varchar(45) NOT NULL,
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `coder_UNIQUE` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

CREATE TABLE `smart_contract` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `address` varchar(128) DEFAULT NULL,
  `icon` varchar(128) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `is_delete` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE `sys_config` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `value` varchar(45) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `wallet_account` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `account_name` varchar(45) NOT NULL,
  `account_no` varchar(6) NOT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `header` varchar(200) DEFAULT NULL,
  `user_name` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `activate_type` int(11) NOT NULL,
  `source_type` int(11) NOT NULL COMMENT '0=IOS 1=安卓 2=Web',
  PRIMARY KEY (`id`),
  UNIQUE KEY `account_no_UNIQUE` (`account_no`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=192 DEFAULT CHARSET=utf8;

CREATE TABLE `wallet_address` (
  `id` bigint(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(45) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `account_id` bigint(11) NOT NULL,
  `is_default` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `address_UNIQUE` (`address`),
  KEY `FKe4or4gp182i0t3563w33f8fdv` (`account_id`),
  CONSTRAINT `FKe4or4gp182i0t3563w33f8fdv` FOREIGN KEY (`account_id`) REFERENCES `wallet_account` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1119 DEFAULT CHARSET=utf8;

  /*添加数据 remoteUrl*/
INSERT INTO `qbao_schema`.`sys_config` (`id`, `name`, `value`) VALUES ('1', 'remote_url', 'http://163.172.251.4:5931/');

