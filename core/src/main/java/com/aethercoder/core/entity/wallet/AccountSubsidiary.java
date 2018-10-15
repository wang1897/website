package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/22
 * @modified By:
 */
@Entity
@Table(name = "account_subsidiary")
public class AccountSubsidiary extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "is_receive")
    private String isReceive;

    @Column(name = "had_withdraw")
    private Boolean hadWithdraw = false;

    @Column(name = "is_joined")
    private Boolean isJoined = false;

    @Column(name = "account_no")
    private String accountNo;

    public Boolean getIsJoined() {
        return isJoined;
    }

    public void setIsJoined(Boolean isJoined) {
        this.isJoined = isJoined;
    }

    public Boolean getHadWithdraw() {
        return hadWithdraw;
    }

    public void setHadWithdraw(Boolean hadWithdraw) {
        this.hadWithdraw = hadWithdraw;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getIsReceive() {
        return isReceive;
    }

    public void setIsReceive(String isReceive) {
        this.isReceive = isReceive;
    }
}
