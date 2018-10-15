package com.aethercoder.core.controller;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.admin.AdminAccount;
import com.aethercoder.core.entity.social.UserContact;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.UserContactService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.DateUtil;
import io.rong.messages.ContactNtfMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2017/10/12 下午2:24
 */

@RestController
@RequestMapping("userContract")
@Api(tags = "userContract", description = "我的好友接口管理")
public class UserContactController {

    private static Logger logger = LoggerFactory.getLogger(UserContactController.class);

    @Autowired
    private UserContactService userContactService;


    /**
     * 陌生人加为好友—id/扫码方式
     * @param contactNtfMessage
     * @return UserContact
     * @throws Throwable
     */
    @ApiOperation(value = "陌生人加为好友—id/扫码方式 ", notes = "参数：1。sourceUserId-本人的accountNo 2.targetUserId-好友的accountNo ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ContactNtfMessage.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/makeFriend", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map<String,Object> makeFriend(@RequestBody ContactNtfMessage contactNtfMessage) {
        logger.info("makeFriend");
        Assert.notNull(contactNtfMessage.getSourceUserId(),"sourseUser id not null");
        Assert.notNull(contactNtfMessage.getTargetUserId(),"targetUser id not null");
        Map<String, Object> map = new HashMap<>();
        map = userContactService.addFriend(contactNtfMessage);
        return map;
    }

    /**
     * 删除好友（双向删除）
     * @param userContact
     * @return map
     * @throws Throwable
     */
    @ApiOperation(value = "删除好友（双向删除）", notes = "参数：1.accountNo-本人的accountNo 2.contactNo-好友的accountNo  ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = UserContact.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/renounceFriend", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map<String,Object> renounceFriend(@RequestBody UserContact userContact) {
        logger.info("renounceFriend");
        Assert.notNull(userContact.getAccountNo(),"accountNo not null");
        Assert.notNull(userContact.getContactNo(),"contactNo not null");
        Map<String, Object> map = new HashMap<>();
        if(null!= userContactService.dealWithFriends(userContact.getAccountNo(),userContact.getContactNo(),WalletConstants.DELETE_FRIEND))
            map.put("result","Successful deletion of friends");
        else map.put("result","Delete friend failed");
        return map;
    }

    /**
     12：拒绝好友邀请
     20：加为好友
     * @param userContact
     * @return map
     * @throws Throwable
     */
    @ApiOperation(value = "12：拒绝好友邀请\n" +
            "     20：加为好友\n" , notes = "参数：1.accountNo-本人的accountNo\n 2.contactNo-好友的accountNo\n 3.relationship-好友关系(拒绝好友邀请-12／加为好友-20)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = UserContact.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/friendOperation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map<String, Object> friendOperation(@RequestBody UserContact userContact) {
        logger.info("friendOperation");
        Map<String, Object> map = new HashMap<>();
        Assert.notNull(userContact.getAccountNo(),"accountNo not null");
        Assert.notNull(userContact.getContactNo(),"contactNo not null");
        Assert.notNull(userContact.getRelationship(),"relationship not null");
        //该好友关系只能是拒绝好友请求=12或加为好友=20
        if(!WalletConstants.NO_FRIEND_INVITATION.equals(userContact.getRelationship()) && !WalletConstants.ADD_FRIEND.equals(userContact.getRelationship())){
            throw new AppException(ErrorCode.INCORRECT_PARAM );
        }
        map = userContactService.friendOperation(userContact);
        return  map;
    }

    /**
     40：拉黑好友
     * @param userContact
     * @return map
     * @throws Throwable
     */
    @ApiOperation(value = "40：拉黑好友" , notes = "参数：1.accountNo-本人的accountNo\n 2.contactNo-好友的accountNo\n 3.relationship-好友关系(拉黑好友-40)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = UserContact.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/shieldingFriend", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map<String, Object> shieldingFriend(@RequestBody UserContact userContact) {
        logger.info("shieldingFriend");
        Map<String, Object> map = new HashMap<>();
        Assert.notNull(userContact.getAccountNo(),"accountNo not null");
        Assert.notNull(userContact.getContactNo(),"contactNo not null");
        if(userContactService.ShieldingFriends(userContact.getAccountNo(),userContact.getContactNo(),WalletConstants.SHIELDING_FRIEND)){
            map.put("result","Block buddy success");
        }else{map.put("","Blocking your friends is a failure");}
        return  map;
    }

    /***
     * 获取好友列表
     * @return
     */
    @ApiOperation(value="获取所有好友列表", notes="参数：1. accountNo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getChatFriendsList",method = RequestMethod.GET,produces = "application/json")
    public List<UserContact> getChatFriendsList(){
        logger.info("getChatFriendsList");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        return userContactService.getChatFriendsList(accountNo);
    }

    /***
     * 同步好友列表
     * @return
     */
    @ApiOperation(value="同步好友列表", notes="参数：1. accountNo 2。timestamp-时间戳")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/syncAccountFriends",method = RequestMethod.GET,produces = "application/json")
    public Map<String,Object> syncAccountFriends(@RequestParam(required = false) String timestamp){
        logger.info("syncAccountFriends");
        Map<String, Object> map = new HashMap<>();
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        logger.info("syncAccountFriends");
        List<UserContact> userContacts = null;
        Date date = null;
        if( null == timestamp || "".equals(timestamp)){
            userContacts = userContactService.getChatFriendsList(accountNo);
        }else {
            try {
                date = DateUtil.stringToDate(timestamp);
            }catch (RuntimeException e){
                throw new AppException(ErrorCode.WRONG_TIME_FORMAT);
            }
            userContacts = userContactService.syncAccountFriends(accountNo,date);
        }
        map.put("userContacts",userContacts);
        map.put("version", DateUtil.dateToString(new Date()));
        return map;

    }

}
