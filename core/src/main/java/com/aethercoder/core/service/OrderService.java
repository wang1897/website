package com.aethercoder.core.service;

import com.aethercoder.core.entity.pay.Order;
import com.aethercoder.core.entity.wallet.Customer;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2018/3/20 下午4:49
 */
public interface OrderService {
    Map findOrdersByCustomer(Integer page, Integer size, String customerId, String cleanTime, String beginDate, String endDate, Integer status, String orderId);

    Page<Customer> findAllOrdersByPage(Integer page, Integer size, String customerNo, String customerName, Date cleanTime);

    Map getOrdersDay(String customerId,String cleanTime);

    Order preCreateOrder(Customer customer,String accountNo);

    String payOrder(Order order);

    Boolean cancelOrder(String orderId,String accountNo);

    Order queryOrder(String orderId,String accountNo);

    Boolean queryUser(String accountNo);

    void setNickWords(String accountNo, String words);

    Page<Order> getOrderListByPage(Integer page,Integer size,String startTime,String endTime,String accountNo,String accountName,String customerId,
                                  String unit,String status,String orderId,String type);
}
