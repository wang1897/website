package com.aethercoder.core.entity.social;

import io.rong.util.GsonUtil;

/**
 * Created by jiawei.tao on 2017/10/19.
 */
public class SocialMessage {

    private String groupNo;

    private String groupName;

    private String groupPortrait;

    private String accountNo;

    private String userName;

    private String userPortrait;

    private String userLevel;

    private String userLevelStatus;

    private String descriptions;

    private String operationStatus;

    private String socialType;

    private String joinGroupNote;

    public String getJoinGroupNote() {
        return joinGroupNote;
    }

    public void setJoinGroupNote(String joinGroupNote) {
        this.joinGroupNote = joinGroupNote;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPortrait() {
        return groupPortrait;
    }

    public void setGroupPortrait(String groupPortrait) {
        this.groupPortrait = groupPortrait;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserLevelStatus() {
        return userLevelStatus;
    }

    public void setUserLevelStatus(String userLevelStatus) {
        this.userLevelStatus = userLevelStatus;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(String operationStatus) {
        this.operationStatus = operationStatus;
    }

    public String getSocialType() {
        return socialType;
    }

    public void setSocialType(String socialType) {
        this.socialType = socialType;
    }
    @Override
    public String toString() {
        return GsonUtil.toJson(this, SocialMessage.class);
    }
}
