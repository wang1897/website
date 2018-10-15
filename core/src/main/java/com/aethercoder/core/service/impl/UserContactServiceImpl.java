package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.AccountDao;
import com.aethercoder.core.dao.UserContactDao;
import com.aethercoder.core.entity.social.SocialMessage;
import com.aethercoder.core.entity.social.UserContact;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.UserContactService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import io.rong.RongCloud;
import io.rong.messages.ContactNtfMessage;
import io.rong.messages.QbaoSocialMessage;
import io.rong.models.CodeSuccessResult;
import io.rong.models.QueryBlacklistUserResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Qbao2.0 社交-我的好友
 *
 * @auther Guo Feiyan
 * @date 2017/10/12 下午12:02
 */
@Service
public class UserContactServiceImpl implements UserContactService {

    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Autowired
    private UserContactDao userContactDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${rongCloud.appKey}")
    private String appKey;

    @Value("${rongCloud.appSecret}")
    private String appSecret;

    private static Logger logger = LoggerFactory.getLogger(UserContactServiceImpl.class);

    @Override
    public UserContact makeFriend(String accountNo, String targetUserNo) {

        //不能自己加自己为好友
        if (accountNo.equals(targetUserNo)) {
            throw new AppException(ErrorCode.NOT_FRIEND_YOURSELF);
        }
        //发送好友邀请
        UserContact sendUserContact = addFriendRelationship(accountNo, targetUserNo, WalletConstants.SEND_FRIEND_INVITATION);
        //对方收到好友邀请
        UserContact receiveUserContact = addFriendRelationship(targetUserNo, accountNo, WalletConstants.RECEIVE_FRIEND_INVITATION);

        return sendUserContact;
    }

    /**
     * 向融云发送好友请求消息
     * 1。判断该好友和被邀请好友的关系 判断双方在服务器是否已经存在好友关系
     * 若存在：
     * 好友关系=10：发出好友邀请 可重新加为好友 只更新该数据
     * 好友关系=11：收到好友邀请 可重新加为好友 只更新该数据
     * 好友关系=12：拒绝好友邀请 可重新加为好友 只更新该数据
     * 好友关系=20：同意加为好友 已是好友关系，不能再添加为好友
     * 好友关系=30：删除好友 删除成功后，可重新添加好友，服务器端只是更新两方的好友关系，只更新该数据
     * 好友关系=40：拉黑好友 已是好友关系，不能再添加为好友
     * 2.不存在 建立邀请好友关系并推送好友邀请消息
     *
     * @param contactNtfMessage
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> addFriend(ContactNtfMessage contactNtfMessage) {
        logger.info("addFriend：TargetUserId:"+contactNtfMessage.getTargetUserId()+" SourceUserId:"+contactNtfMessage.getSourceUserId());
        Map<String, Object> map = new HashMap<String, Object>();
        UserContact userContact = findByAccountNoAndTargetUserNo(contactNtfMessage.getSourceUserId(), contactNtfMessage.getTargetUserId());
        //已发送发送好友请求=10
        Set codeCustomer = redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_USERCONTRACT_REQUEST + contactNtfMessage.getSourceUserId()+contactNtfMessage.getTargetUserId());
        if (codeCustomer != null && codeCustomer.size() != 0) {
            return map;
        }else {
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations)
                        throws DataAccessException {
                    operations.multi();
                    //add redis
                    operations.opsForSet().add(RedisConstants.REDIS_NAME_USERCONTRACT_REQUEST + contactNtfMessage.getSourceUserId()+contactNtfMessage.getTargetUserId(), contactNtfMessage.getTargetUserId());
                    operations.expire(RedisConstants.REDIS_NAME_USERCONTRACT_REQUEST + contactNtfMessage.getSourceUserId()+contactNtfMessage.getTargetUserId(), 3, TimeUnit.DAYS);
                    operations.exec();
                    return null;

                }
            });
        }
        //双方在服务器是否已经存在好友关系
        //若存在 则判断好友关系
        if (null != userContact) {
            if (WalletConstants.SHIELDING_FRIEND.equals(userContact.getRelationship()) || WalletConstants.ADD_FRIEND.equals(userContact.getRelationship())) {
                //如果好友关系是已经拉黑=40／20 已是好友 不能加对方为好友
                map.put("result", "You are already friends");
                return map;
            } else if (WalletConstants.DELETE_FRIEND.equals(userContact.getRelationship())
                    || WalletConstants.NO_FRIEND_INVITATION.equals(userContact.getRelationship())
                    || WalletConstants.RECEIVE_FRIEND_INVITATION.equals(userContact.getRelationship())
                    || WalletConstants.SEND_FRIEND_INVITATION.equals(userContact.getRelationship())) {
                //如果好友关系是已删除好友=30／拒绝好友邀请=12／收到好友邀请=11 /发送好友请求=10
                //修改邀请de好友关系为10、
                //修改被邀请de好友关系为11、
                //同时发送好友邀请
              /* //如果是删除好友关系getRelationship=30 解除黑名单关系 可重新发送好友请求
               if(WalletConstants.DELETE_FRIEND.equals(userContact.getRelationship())){
                   //获取黑名单 查询该好友是否在黑名单中 是-解除
                   QueryBlacklistUserResult queryBlacklistUserResult = queryBlacklist(contactNtfMessage.getSourceUserId());
                   for (String s : queryBlacklistUserResult.getUsers()) {
                       //如果黑名单中有该好友 解除黑名单关系
                       if (s.equals(contactNtfMessage.getTargetUserId())){
                           removeBlacklist(contactNtfMessage.getSourceUserId(),contactNtfMessage.getTargetUserId());
                       }
                   }
               }*/

                UserContact userContact1 = updateFriendRelationship(contactNtfMessage.getSourceUserId(), contactNtfMessage.getTargetUserId(), WalletConstants.SEND_FRIEND_INVITATION);
                Locale locale = accountService.getLocaleByAccount(userContact1.getContactNo());
                updateFriendRelationship(contactNtfMessage.getTargetUserId(), contactNtfMessage.getSourceUserId(), WalletConstants.RECEIVE_FRIEND_INVITATION);
                String message = combineFriendMessage(localeMessageUtil.getLocalMessage("ADD_FRIEND", locale, new String[]{userContact1.getAccountName()}), userContact1.getAccountNo(), WalletConstants.OPERATION_STATUS_PENDING);
                    map = sendInVite(userContact1, map, message);
                return map;
            }
        }
        //建立邀请好友关系并推送好友邀请消息
        userContact = makeFriend(contactNtfMessage.getSourceUserId(), contactNtfMessage.getTargetUserId());
        if (null != userContact) {
            Locale locale = accountService.getLocaleByAccount(userContact.getContactNo());
            String message = combineFriendMessage(localeMessageUtil.getLocalMessage("ADD_FRIEND", locale, new String[]{userContact.getAccountName()}), userContact.getAccountNo(), WalletConstants.OPERATION_STATUS_PENDING);

            map = sendInVite(userContact, map, message);

        } else {
            map.put("result", "Failed to send out a friend invitation");
        }
        return map;
    }

    private String combineFriendMessage(String content, String accountNo, String operationStatus) {

        Account account = accountService.findAccountByAccountNo(accountNo);

        SocialMessage message = new SocialMessage();
        message.setDescriptions(content);
        message.setAccountNo(account.getAccountNo());
        message.setUserPortrait(account.getHeader());
        message.setUserName(account.getAccountName());
        message.setUserLevel(account.getLevel() == null ? "0" : account.getLevel().toString());
        message.setUserLevelStatus(account.getLevelStatus() ? "1" : "0");
        message.setOperationStatus(operationStatus);
        message.setSocialType(WalletConstants.QBAO_ADMIN);

        return message.toString();
    }

    // 发送好友请求消息
    private Map sendInVite(UserContact userContact1, Map map, String message) {
        logger.info("sendFriendMessage content:" + message);

        String[] toUserId = {userContact1.getContactNo()};
        QbaoSocialMessage txtMessage = new QbaoSocialMessage(message);
        //发送好友请求--toUser
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        CodeSuccessResult userGetTokenResult;
        try {
            userGetTokenResult = rongCloud.message.publishPrivate(WalletConstants.QBAO_ADMIN,
                    toUserId,
                    txtMessage,
                    localeMessageUtil.getLocalMessage("ADD_FRIEND_CONTENT", null),
                    null,
                    null,
                    null, null, null, null);
            logger.info("----sendFriendMessage :" + userGetTokenResult.toString());
            map.put("result", "Successful friend invitation");
        } catch (Exception e) {
            logger.error("---makeFriend error----", e);
            throw new RuntimeException("sendInVite error:" + e.getMessage());
        }
        return map;
    }

    @Override
    public Map<String, Object> friendOperation(UserContact userContact) {
        logger.info("friendOperation: AccountNo:"+userContact.getAccountNo()+" ContactNo:"+userContact.getContactNo());
        Map<String, Object> map = new HashMap<String, Object>();
        String msg = "";
        String message = "";
        userContact = dealWithFriends(userContact.getAccountNo(), userContact.getContactNo(), userContact.getRelationship());
        if (userContact != null) {
            Locale locale = accountService.getLocaleByAccount(userContact.getContactNo());
            //操作联系人消息。同意／拒绝
            if (WalletConstants.NO_FRIEND_INVITATION.equals(userContact.getRelationship())) {
                msg = localeMessageUtil.getLocalMessage("ACCEPT_FRIEND", locale, new String[]{userContact.getAccountName()});
                message = combineFriendMessage(msg, userContact.getAccountNo(), WalletConstants.OPERATION_STATUS_REFUSED);

            } else if (WalletConstants.ADD_FRIEND.equals(userContact.getRelationship())) {
                msg = localeMessageUtil.getLocalMessage("AGREE_FRIEND", locale, new String[]{userContact.getAccountName()});
                message = combineFriendMessage(msg, userContact.getAccountNo(), WalletConstants.OPERATION_STATUS_AGREED);

            }
            //向融云发送推送消息
            try {
                sendInVite(userContact, map, message);
            } catch (Exception e) {
                logger.error("---friendOperation error----", e);
                throw new RuntimeException("friendOperation error:" + e.getMessage());
            }
            Set codeCustomer = redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_USERCONTRACT_REQUEST +userContact.getContactNo()+userContact.getAccountNo());
            if (codeCustomer!=null&&codeCustomer.size()!=0){
                redisTemplate.opsForSet().getOperations().delete(RedisConstants.REDIS_NAME_USERCONTRACT_REQUEST +userContact.getContactNo()+ userContact.getAccountNo());
            }
            Set codeCustomerFriend = redisTemplate.opsForSet().members(RedisConstants.REDIS_NAME_USERCONTRACT_REQUEST +userContact.getAccountNo()+userContact.getContactNo());
            if (codeCustomerFriend!=null&&codeCustomerFriend.size()!=0){
                redisTemplate.opsForSet().getOperations().delete(RedisConstants.REDIS_NAME_USERCONTRACT_REQUEST +userContact.getAccountNo()+userContact.getContactNo());
            }

        } else {
            map.put("result", "The operation failure");
        }
        return map;
    }

    @Override
    @javax.transaction.Transactional
    public UserContact dealWithFriends(String accountNo, String targetUserNo, Integer friendship) {
        //不能自己加自己为好友
        if (accountNo.equals(targetUserNo)) {
            throw new AppException(ErrorCode.NOT_FRIEND_YOURSELF);
        }
        UserContact userContact = userContactDao.findUserContactByAccountNoAndContactNo(accountNo, targetUserNo);
        //未找到与该好友关系的信息记录
        if (null == userContact) {
            throw new AppException(ErrorCode.UNKNOWN_FRIEND);
        }
        //relationship=30 删除
        if (WalletConstants.DELETE_FRIEND.equals(friendship)) {
            //必须是好友关系
            if (!WalletConstants.ADD_FRIEND.equals(userContact.getRelationship()) && !WalletConstants.SHIELDING_FRIEND.equals(userContact.getRelationship())) {
                throw new AppException(ErrorCode.UNKNOWN_FRIEND);
            }
            //加入黑名单
            //addBlacklist(accountNo, targetUserNo);

        } else if (WalletConstants.ADD_FRIEND.equals(friendship) || WalletConstants.NO_FRIEND_INVITATION.equals(friendship)) {
            //是否是好友关系
            if (WalletConstants.ADD_FRIEND.equals(userContact.getRelationship()) || WalletConstants.SHIELDING_FRIEND.equals(userContact.getRelationship())) {
                throw new AppException(ErrorCode.IS_FRIEND);
            }
            //必须是被邀请好友关系
            if (!WalletConstants.RECEIVE_FRIEND_INVITATION.equals(userContact.getRelationship())) {
                throw new AppException(ErrorCode.UNKNOWN_FRIEND);
            }
        }
        UserContact userContactFrom = updateFriendRelationship(accountNo, targetUserNo, friendship);
        UserContact userContactTo = updateFriendRelationship(targetUserNo, accountNo, friendship);
        return userContactFrom;
    }

    @Override
    public List<UserContact> getChatFriendsList(String accountNo) {
        List<UserContact> userContacts = userContactDao.findUserContactsByAccountNo(accountNo);

        userContacts.forEach(userContact -> {
                    Account account = accountDao.findByAccountNo(userContact.getContactNo());
                    userContact.setHeader(account.getHeader());
                    userContact.setAccountName(account.getAccountName());
                }
        );

        return userContacts;
    }

    @Override
    public List<UserContact> syncAccountFriends(String accountNo, Date date) {

        List<UserContact> userContacts = null;
        userContacts = userContactDao.findByAccountNoAndUpdateTimeAfter(accountNo, date);
        userContacts.forEach(userContact -> {
                    Account account = accountDao.findByAccountNo(userContact.getContactNo());
                    userContact.setHeader(account.getHeader());
                    userContact.setAccountName(account.getAccountName());
                }
        );
        return userContacts;
    }

    @Override
    public UserContact addFriendRelationship(String accountNo, String targetUserNo, Integer relationship) {
        //该用户是否真实存在
        Account account = accountService.findAccountByAccountNo(accountNo);
        UserContact userContact = new UserContact();
        userContact.setAccountName(account.getAccountName());
        userContact.setAccountNo(accountNo);
        userContact.setContactNo(targetUserNo);
        userContact.setRelationship(relationship);
        return userContactDao.save(userContact);
    }

    /**
     * 12：拒绝好友邀请
     * 20：加为好友
     *
     * @param accountNo
     * @param targetUserNo
     * @param relationship
     * @return
     */
    @Override
    @Transactional
    public UserContact updateFriendRelationship(String accountNo, String targetUserNo, Integer relationship) {
        Account account = accountService.findAccountByAccountNo(accountNo);
        UserContact userContact = userContactDao.findUserContactByAccountNoAndContactNo(accountNo, targetUserNo);
        userContact.setRelationship(relationship);
        userContact.setAccountName(account.getAccountName());
        return userContactDao.save(userContact);
    }

    @Override
    public UserContact findByAccountNoAndTargetUserNo(String accountNo, String targetUserNo) {
        return userContactDao.findUserContactByAccountNoAndContactNo(accountNo, targetUserNo);
    }

    /**
     * 加入黑名单
     *
     * @param accountNo
     * @param targetUserNo
     * @param friendship
     * @return
     */
    @Override
    @Transactional
    public Boolean ShieldingFriends(String accountNo, String targetUserNo, Integer friendship) {
        UserContact userContact = userContactDao.findUserContactByAccountNoAndContactNo(accountNo, targetUserNo);
        //未找到与该好友关系的信息记录
        if (null == userContact) {
            throw new AppException(ErrorCode.UNKNOWN_FRIEND);
        }
        if (userContact.getRelationship().equals(friendship)) {
            throw new AppException(ErrorCode.IS_BLOCK_FRIEND);
        }
        //relationship=40拉黑
        if (WalletConstants.SHIELDING_FRIEND.equals(friendship)) {
            //必须是好友关系
            if (!WalletConstants.ADD_FRIEND.equals(userContact.getRelationship())) {
                throw new AppException(ErrorCode.UNKNOWN_FRIEND);
            }
        }
        UserContact userContactFrom = updateFriendRelationship(accountNo, targetUserNo, friendship);
        if (userContactFrom == null) {
            return false;
        }
        //加入黑名单
        addBlacklist(accountNo, targetUserNo);
        return true;
    }

    //加入黑名单
    public void addBlacklist(String accountNo, String targetUserNo) {
        //向融云添加用户到黑名单
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        try {
            CodeSuccessResult userGetTokenResult = rongCloud.user.addBlacklist(accountNo, targetUserNo);
            logger.info("用户：" + accountNo + "向融云添加用户：" + targetUserNo + "到黑名单：" + userGetTokenResult.toString());
        } catch (Exception e) {
            logger.error("用户：" + accountNo + "向融云添加用户：" + targetUserNo + "到黑名单 失败", e);
            throw new RuntimeException("ShieldingFriends error:" + e.getMessage());
        }
    }

    //移除黑名单
    public void removeBlacklist(String accountNo, String targetUserNo) {
        //向融云添加用户到黑名单
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        try {
            CodeSuccessResult userGetTokenResult = rongCloud.user.removeBlacklist(accountNo, targetUserNo);
            logger.info("用户：" + accountNo + "向融云移除黑名单中用户：" + targetUserNo + "，" + userGetTokenResult.toString());
        } catch (Exception e) {
            logger.error("用户：" + accountNo + "向融云移除黑名单中用户" + targetUserNo + " 失败", e);
            throw new RuntimeException("removeBlacklist error:" + e.getMessage());
        }
    }

    /**
     * 获得黑名单列表
     *
     * @param accountNo
     * @param accountNo
     * @return
     */
    public QueryBlacklistUserResult queryBlacklist(String accountNo) {
        //向融云添加用户到黑名单
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        QueryBlacklistUserResult queryBlacklist = null;
        try {
            queryBlacklist = rongCloud.user.queryBlacklist(accountNo);
            logger.info("获取用户：" + accountNo + "的黑名单列表" + queryBlacklist.toString());
        } catch (Exception e) {
            logger.error("用户：" + accountNo + "的黑名单列表 失败", e);
            throw new RuntimeException("queryBlacklist error:" + e.getMessage());
        }
        return queryBlacklist;
    }


}
