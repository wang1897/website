package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.wallet.Account;

import java.util.List;

/**
 * Created by jiawei.tao on 2017/10/19.
 */
public class UserInfo {
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    private Account account;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private String version;
    private List<UserGroup> userGroupsList;

    public List<Group> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    private List<Group> groupList;

    public List<GroupMember> getGroupMembersList() {
        return groupMembersList;
    }

    public void setGroupMembersList(List<GroupMember> groupMembersList) {
        this.groupMembersList = groupMembersList;
    }

    private List<GroupMember> groupMembersList;

    public List<UserGroup> getUserGroupsList() {
        return userGroupsList;
    }

    public void setUserGroupsList(List<UserGroup> userGroupsList) {
        this.userGroupsList = userGroupsList;
    }

    public List<UserContact> getUserContactsList() {
        return userContactsList;
    }

    public void setUserContactsList(List<UserContact> userContactsList) {
        this.userContactsList = userContactsList;
    }

    private List<UserContact> userContactsList;
}
