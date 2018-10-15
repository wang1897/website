package com.aethercoder.core.entity.result;


import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/8
 * @modified By:
 */
public class AccountInvitingInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    private String shareCode;

    private Integer invitingNumber;

    private BigDecimal invitingAward;

    private Integer gainedNumber;

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public Integer getInvitingNumber() {
        return invitingNumber;
    }

    public void setInvitingNumber(Integer invitingNumber) {
        this.invitingNumber = invitingNumber;
    }

    public BigDecimal getInvitingAward() {
        return invitingAward;
    }

    public void setInvitingAward(BigDecimal invitingAward) {
        this.invitingAward = invitingAward;
    }

    public Integer getGainedNumber() {
        return gainedNumber;
    }

    public void setGainedNumber(Integer gainedNumber) {
        this.gainedNumber = gainedNumber;
    }

    @Override
    public String toString() {
        return "AccountInvitingInfo{" +
                "shareCode='" + shareCode + '\'' +
                ", invitingNumber=" + invitingNumber +
                ", invitingAward=" + invitingAward +
                ", gainedNumber=" + gainedNumber +
                '}';
    }
}
