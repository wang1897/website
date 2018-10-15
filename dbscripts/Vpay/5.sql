INSERT INTO `qbao_schema`.`t_country_information` (`create_time`,`update_time`,`country`,`tel_number`) VALUES ('2018-01-01','2018-01-01','zh','+86');
INSERT INTO `qbao_schema`.`t_country_information` (`create_time`,`update_time`,`country`,`tel_number`) VALUES ('2018-01-01','2018-01-01','ko','+82');
INSERT INTO `qbao_schema`.`t_country_information` (`create_time`,`update_time`,`country`,`tel_number`) VALUES ('2018-01-01','2018-01-01','en','+001');

ALTER TABLE `qbao_schema`.`t_order`
ADD INDEX `index_customer_ordertime` (`customer_id` ASC, `order_time` ASC),
ADD INDEX `index_orderId` (`order_id` ASC),
ADD INDEX `index_account_no` (`account_no` ASC);
