package com.aethercoder.core.service;

import com.aethercoder.core.entity.social.UserContact;
import io.rong.messages.ContactNtfMessage;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Qbao2.0 社交-我的好友
 * @auther Guo Feiyan
 * @date 2017/10/12 下午12:02
 */
public interface UserContactService {

    /**
     * 陌生人加为好友—id方式/扫码方式
     * @param accountNo
     * @param targetUserNo
     * @return
     */
    UserContact makeFriend(String accountNo, String targetUserNo);

    Map<String,Object> addFriend(ContactNtfMessage contactNtfMessage);

    Map<String,Object> friendOperation(UserContact userContact);

    /**
     * 删除好友（双向删除）
     * @param accountNo
     * @param targetUserNo
     * @return
     */
    UserContact dealWithFriends(String accountNo, String targetUserNo, Integer friendship);


    /**
     * 获取好友列表
     * @param accountNo
     * @return
     */
    List<UserContact> getChatFriendsList(String accountNo);

    /**
     * 同步好友列表
     * @param accountNo
     * @return
     */
    List<UserContact> syncAccountFriends(String accountNo, Date timestamp);

    /**
     * 好友关系设置加为好友
     * @param accountNo
     * @param targetUserNo
     * @param relationship
     * @return
     */
    UserContact addFriendRelationship(String accountNo, String targetUserNo, Integer relationship);

    /**
     * 好友关系设置删除好友
     * @param accountNo
     * @param relationship
     * @return
     */
    UserContact updateFriendRelationship(String accountNo, String targetUserNo, Integer relationship);

    UserContact findByAccountNoAndTargetUserNo(String accountNo, String targetUserNo);

    Boolean ShieldingFriends(String accountNo, String targetUserNo, Integer friendship);

}
