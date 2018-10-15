package com.aethercoder.core.entity.result;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/23
 * @modified By:
 */
public class InvitingRewardInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer invitedType;

    private BigDecimal invitedReward;

    private BigDecimal invitingReward;

    private Integer uploadHeaderType;

    private BigDecimal uploadHeaderReward;

    private Integer gainedType;

    private BigDecimal gainedReward;

    private BigDecimal gainingReward;

    private Integer walletAchievedType;

    private BigDecimal walletAchievedReward;

    private BigDecimal walletAchievingReward;

    private Integer luckyAccountType;

    private BigDecimal invitedFourToThree;

    private BigDecimal invitingFourToThree;

    private BigDecimal invitedSixToThree;

    private BigDecimal invitingSixToThree;

    private BigDecimal invitedSixToFour;

    private BigDecimal invitingSixToFour;

    private BigDecimal invitedSixToFive;

    private BigDecimal invitingSixToFive;

    private Boolean overdue;

    public Boolean getOverdue() {
        return overdue;
    }

    public void setOverdue(Boolean overdue) {
        this.overdue = overdue;
    }

    public Integer getInvitedType() {
        return invitedType;
    }

    public void setInvitedType(Integer invitedType) {
        this.invitedType = invitedType;
    }

    public BigDecimal getInvitedReward() {
        return invitedReward;
    }

    public void setInvitedReward(BigDecimal invitedReward) {
        this.invitedReward = invitedReward;
    }

    public BigDecimal getInvitingReward() {
        return invitingReward;
    }

    public void setInvitingReward(BigDecimal invitingReward) {
        this.invitingReward = invitingReward;
    }

    public Integer getUploadHeaderType() {
        return uploadHeaderType;
    }

    public void setUploadHeaderType(Integer uploadHeaderType) {
        this.uploadHeaderType = uploadHeaderType;
    }

    public BigDecimal getUploadHeaderReward() {
        return uploadHeaderReward;
    }

    public void setUploadHeaderReward(BigDecimal uploadHeaderReward) {
        this.uploadHeaderReward = uploadHeaderReward;
    }

    public Integer getGainedType() {
        return gainedType;
    }

    public void setGainedType(Integer gainedType) {
        this.gainedType = gainedType;
    }

    public BigDecimal getGainedReward() {
        return gainedReward;
    }

    public void setGainedReward(BigDecimal gainedReward) {
        this.gainedReward = gainedReward;
    }

    public BigDecimal getGainingReward() {
        return gainingReward;
    }

    public void setGainingReward(BigDecimal gainingReward) {
        this.gainingReward = gainingReward;
    }

    public Integer getWalletAchievedType() {
        return walletAchievedType;
    }

    public void setWalletAchievedType(Integer walletAchievedType) {
        this.walletAchievedType = walletAchievedType;
    }

    public BigDecimal getWalletAchievedReward() {
        return walletAchievedReward;
    }

    public void setWalletAchievedReward(BigDecimal walletAchievedReward) {
        this.walletAchievedReward = walletAchievedReward;
    }

    public BigDecimal getWalletAchievingReward() {
        return walletAchievingReward;
    }

    public void setWalletAchievingReward(BigDecimal walletAchievingReward) {
        this.walletAchievingReward = walletAchievingReward;
    }

    public Integer getLuckyAccountType() {
        return luckyAccountType;
    }

    public void setLuckyAccountType(Integer luckyAccountType) {
        this.luckyAccountType = luckyAccountType;
    }

    public BigDecimal getInvitedFourToThree() {
        return invitedFourToThree;
    }

    public void setInvitedFourToThree(BigDecimal invitedFourToThree) {
        this.invitedFourToThree = invitedFourToThree;
    }

    public BigDecimal getInvitingFourToThree() {
        return invitingFourToThree;
    }

    public void setInvitingFourToThree(BigDecimal invitingFourToThree) {
        this.invitingFourToThree = invitingFourToThree;
    }

    public BigDecimal getInvitedSixToThree() {
        return invitedSixToThree;
    }

    public void setInvitedSixToThree(BigDecimal invitedSixToThree) {
        this.invitedSixToThree = invitedSixToThree;
    }

    public BigDecimal getInvitingSixToThree() {
        return invitingSixToThree;
    }

    public void setInvitingSixToThree(BigDecimal invitingSixToThree) {
        this.invitingSixToThree = invitingSixToThree;
    }

    public BigDecimal getInvitedSixToFour() {
        return invitedSixToFour;
    }

    public void setInvitedSixToFour(BigDecimal invitedSixToFour) {
        this.invitedSixToFour = invitedSixToFour;
    }

    public BigDecimal getInvitingSixToFour() {
        return invitingSixToFour;
    }

    public void setInvitingSixToFour(BigDecimal invitingSixToFour) {
        this.invitingSixToFour = invitingSixToFour;
    }

    public BigDecimal getInvitedSixToFive() {
        return invitedSixToFive;
    }

    public void setInvitedSixToFive(BigDecimal invitedSixToFive) {
        this.invitedSixToFive = invitedSixToFive;
    }

    public BigDecimal getInvitingSixToFive() {
        return invitingSixToFive;
    }

    public void setInvitingSixToFive(BigDecimal invitingSixToFive) {
        this.invitingSixToFive = invitingSixToFive;
    }

    @Override
    public String toString() {
        return "InvitingRewardInfo{" +
                "invitedType=" + invitedType +
                ", invitedReward=" + invitedReward +
                ", invitingReward=" + invitingReward +
                ", uploadHeaderType=" + uploadHeaderType +
                ", uploadHeaderReward=" + uploadHeaderReward +
                ", gainedType=" + gainedType +
                ", gainedReward=" + gainedReward +
                ", gainingReward=" + gainingReward +
                ", walletAchievedType=" + walletAchievedType +
                ", walletAchievedReward=" + walletAchievedReward +
                ", walletAchievingReward=" + walletAchievingReward +
                ", luckyAccountType=" + luckyAccountType +
                ", invitedFourToThree=" + invitedFourToThree +
                ", invitingFourToThree=" + invitingFourToThree +
                ", invitedSixToThree=" + invitedSixToThree +
                ", invitingSixToThree=" + invitingSixToThree +
                ", invitedSixToFour=" + invitedSixToFour +
                ", invitingSixToFour=" + invitingSixToFour +
                ", invitedSixToFive=" + invitedSixToFive +
                ", invitingSixToFive=" + invitingSixToFive +
                '}';
    }
}

