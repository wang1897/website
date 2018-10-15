package com.aethercoder.core.service;


import com.aethercoder.core.entity.wallet.Customer;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/8
 * @modified By:
 */
public interface CustomerService {

    Page<Customer> getCustomerList(Integer page, Integer size,String customerNo,String customerName,Integer status,String language);

    void saveCustomer(Customer customer);

    void cancelCustomer(String customerNo);

    void updateCustomer(Customer customer);

    String queryCustomerQrCode(String customerId);

    //String getCustomerQrCode(String customerNo);

    Customer loginCustomer(String customerNo,String password);

    Customer findCustomerByCustomerNo(String customerNo);

    Customer updateCustomerPwd(Customer customer);

    Customer resetCustomerPwd(Customer customer);

    Map getCodeSms(Customer customer);
}
