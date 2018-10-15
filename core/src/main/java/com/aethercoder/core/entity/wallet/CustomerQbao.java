package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/8
 * @modified By:
 */
@Entity
@Table(name = "t_customer_qbao")
public class CustomerQbao extends BaseEntity {

    private static final long serialVersionUID = -1L;

    private String customerNo;

    private String qbaoId;

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getQbaoId() {
        return qbaoId;
    }

    public void setQbaoId(String qbaoId) {
        this.qbaoId = qbaoId;
    }
}
