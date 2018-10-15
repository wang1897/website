package com.aethercoder.core.controller;

import com.aethercoder.core.entity.event.Investigation;
import com.aethercoder.core.entity.result.AccountInvitingInfo;
import com.aethercoder.core.entity.result.InvitingRewardInfo;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.InvitingReward;
import com.aethercoder.core.service.InviteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/8
 * @modified By:
 */
@RestController
@RequestMapping( "invite" )
@Api( tags = "invite", description = "邀请注册" )
public class InviteController {

    private static Logger logger = LoggerFactory.getLogger(InvestigationController.class);

    @Autowired
    private InviteService inviteService;

    /**
     * Qbao H5用户邀请注册信息
     *
     *
     */
    @ApiOperation( value = "Qbao H5用户邀请注册信息", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "accountInvitingInfo", method = RequestMethod.GET, produces = "application/json" )
    public AccountInvitingInfo accountInvitingInfo() {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        return inviteService.accountInvitingInfo(accountNo);
    }

    /**
     * Qbao H5受邀请注册信息
     *
     *
     */
    @ApiOperation( value = "Qbao H5受邀请注册信息", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "accountInvitedInfo", method = RequestMethod.GET, produces = "application/json" )
    public Account accountInvitedInfo(@RequestParam String shareCode) {
        Assert.notNull(shareCode, "shareCode not null");
        return inviteService.accountInvitedInfo(shareCode);
    }

    /**
     * Qbao H5邀请注册奖励信息
     *
     *
     */
    @ApiOperation( value = "Qbao H5邀请奖励", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "invitingRewardInfo", method = RequestMethod.GET, produces = "application/json" )
    public InvitingRewardInfo invitingRewardInfo() {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        return inviteService.invitingRewardInfo(accountNo);
    }

    /**
     * Qbao H5邀请奖励
     *
     *
     */
    @ApiOperation( value = "Qbao H5邀请奖励", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "invitingReward", method = RequestMethod.POST, produces = "application/json" )
    public Map  invitingReward() {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        inviteService.invitingReward(accountNo);
        Map map = new HashMap();
        map.put("massage","success");
        return map;
    }

    /**
     * Qbao H5上传头像奖励
     *
     *
     */
    @ApiOperation( value = "Qbao H5上传头像奖励", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "uploadHeaderReward", method = RequestMethod.POST, produces = "application/json" )
    public Map uploadHeaderReward() {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        inviteService.uploadHeaderReward(accountNo);
        Map map = new HashMap();
        map.put("massage","success");
        return map;
    }

    /**
     * Qbao H5出师奖励
     *
     *
     */
    @ApiOperation( value = "Qbao H5出师奖励", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "gainedReward", method = RequestMethod.POST, produces = "application/json" )
    public Map gainedReward() {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        inviteService.gainedReward(accountNo);
        Map map = new HashMap();
        map.put("massage","success");
        return map;
    }

    /**
     * Qbao H5 钱包成就奖励
     *
     *
     */
    @ApiOperation( value = "Qbao H5钱包成就奖励", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "walletAchievingReward", method = RequestMethod.POST, produces = "application/json" )
    public Map walletAchievingReward() {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        inviteService.walletAchievingReward(accountNo);
        Map map = new HashMap();
        map.put("massage","success");
        return map;
    }

    /**
     * Qbao H5幸运用户奖励
     *
     *
     */
    @ApiOperation( value = "Qbao H5幸运用户奖励", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "luckyAccountReward", method = RequestMethod.POST, produces = "application/json" )
    public Map luckyAccountReward() {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        return inviteService.luckyAccountReward(accountNo);
    }

    /**
     * 后台 邀请注册活动配置显示
     *
     *
     */
    @ApiOperation( value = "后台 邀请注册活动配置显示", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/admin/invitingRegisteredInfo", method = RequestMethod.GET, produces = "application/json" )
    public List<InvitingReward> invitingRegisteredInfo() {

        return inviteService.invitingRegisteredInfo();

    }

    /**
     * 后台 邀请注册活动配置修改
     *
     *
     */
    @ApiOperation( value = "后台 邀请注册活动配置修改", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "/admin/invitingRegistered", method = RequestMethod.POST, produces = "application/json" )
    public Map invitingRegistered(@RequestBody List<InvitingReward> invitingRewardList) {

        inviteService.invitingRegistered(invitingRewardList);
        Map map = new HashMap();
        map.put("massage","success");
        return map;
    }
}
