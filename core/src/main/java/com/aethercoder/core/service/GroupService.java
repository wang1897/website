package com.aethercoder.core.service;

import com.aethercoder.core.entity.json.Data;
import com.aethercoder.core.entity.social.*;
import com.aethercoder.core.entity.wallet.Account;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by jiawei.tao on 2017/10/10.
 */
public interface GroupService {
    Group createGroup(Group group);

    Group buildGroup(BuildGroup buildGroup);

    Group updateGroup(Group group);

    GroupInfo updateGroupInfo(GroupInfo groupInfo);

    void updateGroupSequence(String[] groupNoList);

    void deleteGroup(String groupNo);

    void dismissGroup(String groupNo, String accountNo);
//    Group findGroupByGroupID(Long id);

    String joinGroupWithEx(String groupNo, String[] accountNoList, String operator,String confirmInfo,Integer confirmStatus,Boolean flag);

    Map<Integer, List<String>> joinGroup(String groupNo, String[] accountNoList, String operator,String confirmInfo,Integer confirmStatus,Boolean flag);

    void moveBlackListMember(List<GroupMember> accountNoList, String operator);

    void updateGroupMember(List<GroupMember> accountNoList, String operator);

    void ejectGroup(String groupNo, String[] accountNoList, String operator, String isGap,Boolean flag);

    void ejectGroups(String[] groupNoList, String[] accountNoList, String operator);

    Group findAvailableGroupByGroupNo(String groupNo);

    Group findByGroupNo(String groupNo);

    List<Group> findGroupAll();

    List<GroupMember> findGroupMembersByRoleAndIsDeletedFalse(Integer role);

    List<Group> findGroupsByHoster(String accountNo);

    Page<Group> findGroupsByPage(Integer page, Integer size,String groupNo,String groupName, Integer level, Boolean isDeleted, Long limitUnit,String tag);

    List<Group> findGroupsByPage(Integer page, Integer size);

    Page<Group> findGroupsByPage(Integer page, Integer size, String groupNoOrName);

    List<GroupMember> queryGroupMember(String groupNo);

    Integer queryGroupAdminNumber(String groupNo);

    UserInfo syncGroupMember(String groupNo, String timestamp);

    GroupInfo queryGroupByGroupNoAndAccountNo(String groupNo, String accountNo);

    Page<GroupMember> queryGroupByPage(Integer page, Integer size, String groupNo, String groupNoOrName, Integer role, Boolean isGap);

    UserInfo syncUserGroupInfo(String accountNo, String timestamp);

    Page<Account> queryAddGroupUsersByPage(Integer page, Integer size, String groupNo, String accountNoOrName);

    List<Group> getGroupsByLevel(Integer level);

    List<Data> queryGroupBuildFee();

    void transferOfGroup(String group,String oldGroupLord, String newGroupLord);

    void sendInVite(String fromUserNo, String[] toUserNoList, String message, String pushContent, String pushData);

    GroupMember findByGroupNoAndRole(String groupNo);

    GroupMember getGroupMemberInfo(String groupNo, String memberNo);

    void setGroupConfirmStatus(String groupNo,String accountNo,Integer type,String commandInfo);

    void dealWithIdentityInfo(String groupNo,String accountNo,Integer type,String operator);

    String getGroupShareUrl(String groupNo,String language);

    Map getGroupShareUrlInfo(String groupNo);
}
