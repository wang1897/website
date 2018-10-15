package com.aethercoder.core.entity.social;

import java.util.List;

/**
 * Created by jiawei.tao on 2017/10/19.
 */
public class GroupInfo {

    private Group group;

    private UserGroup userGroup;

    private GroupMember groupMember;

    private List<GroupMember> groupMembersList;

    public List<GroupMember> getGroupMembersList() {
        return groupMembersList;
    }

    public void setGroupMembersList(List<GroupMember> groupMembersList) {
        this.groupMembersList = groupMembersList;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public UserGroup getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }

    public GroupMember getGroupMember() {
        return groupMember;
    }

    public void setGroupMember(GroupMember groupMember) {
        this.groupMember = groupMember;
    }
}
