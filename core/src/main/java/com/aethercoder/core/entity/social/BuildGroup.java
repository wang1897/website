package com.aethercoder.core.entity.social;


import java.math.BigDecimal;

/**
 * @auther Guo Feiyan
 * @date 2017/12/18 下午1:35
 */
public class BuildGroup extends Group {
    private static final long serialVersionUID = -1L;

    private String accountNo;

    private BigDecimal amount;

    private long unit;

    private Group group;


    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getUnit() {
        return unit;
    }

    public void setUnit(long unit) {
        this.unit = unit;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
