package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.event.EventApply;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.EventApplyService;
import com.aethercoder.core.service.VerifyIOSService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiawei.tao on 2017/9/14.
 */
@RestController
@RequestMapping( "eventApply" )
@Api( tags = "eventApply", description = "活动报名接口管理" )
public class EventApplyController {

    private static Logger logger = LoggerFactory.getLogger(EventApplyController.class);

    @Autowired
    private EventApplyService eventApplyService;

    @Autowired
    private VerifyIOSService verifyIOSService;

    /**
     * Qbao-申请报名
     *
     * @param eventApply
     * @return eventApply
     * @throws Throwable
     */
    @ApiOperation( value = "Qbao 申请报名", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = EventApply.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/createEventApply", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
    @Transactional
    public EventApply createEventApply(@RequestBody EventApply eventApply) {
        logger.info("createEventApply");
        return eventApplyService.createEventApply(eventApply);
    }

    /***
     * Qbao  取消报名
     * @param id
     */
    @ApiOperation( value = "Qbao 取消报名", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = String.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/cancelEventApply", method = RequestMethod.POST )
    public Map cancelEventApply(@RequestParam( "id" ) Long id) {
        logger.info("cancelEventApply");
        Map<String, Object> map = new HashMap<>();
        eventApplyService.cancelEventApply(id);
        map.put("result", "success");
        return map;
    }


    /***
     * Qbao 获取用户锁定活动时信息
     * @param eventID
     */
    @ApiOperation( value = "Qbao 获取用户锁定活动时信息", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = EventApply.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/getExpectedApplyInfo", method = RequestMethod.GET, produces = "application/json" )
    public EventApply getExpectedApplyInfo( @RequestParam( "eventId" ) Long eventID) {
        logger.info("getExpectedApplyInfo");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return eventApplyService.getExpectedApplyInfo(accountNo, eventID,null);
    }

    /***
     * Qbao 获取用户所参加的所有活动
     */
    @ApiOperation( value = "Qbao 获取用户所参加的所有活动", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = EventApply.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/getEventAppliesByAccount", method = RequestMethod.GET, produces = "application/json" )
    public List<EventApply> getEventAppliesByAccount() {
        logger.info("getEventAppliesByAccount");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return eventApplyService.getEventAppliesByAccount(accountNo);
    }

    /***
     * Qbao 获取用户可提取金额
     */
    @ApiOperation( value = "Qbao 获取用户可提取金额", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = EventApply.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/getAllActualAmount", method = RequestMethod.GET, produces = "application/json" )
    public Map<String, String> getAllActualAmount(HttpServletRequest request) {
        logger.info("getAllActualAmount");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        BigDecimal amount = eventApplyService.getAllActualAmount(accountNo);
        Map<String, String> result = new HashMap<>(2);
        result.put("result", amount.toString());
        boolean isVerifyIOS = verifyIOSService.isVerifyForIOS(request);
        String unitCode = "";
        if (!isVerifyIOS) {
            unitCode = "QBT";
        }
        result.put("resultUnit", unitCode);
        return result;
    }

    /***
     * Qbao 获取邀请分享活动类型的Url
     */
    @ApiOperation( value = "获取邀请分享活动类型的Url", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = String.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/getShareText", method = RequestMethod.GET, produces = "application/json" )
    public Map<String, String> getShareText(HttpServletRequest request) {
        logger.info("getShareText");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        String language = request.getHeader(WalletConstants.REQUEST_HEADER_LANGUAGE);
        String content = eventApplyService.getShareText(accountNo)+ "&lang=" + language;
        Map<String, String> result = new HashMap<>(1);
        result.put("result", content);
        return result;
    }
    /***
     * Qbao 点击侧边栏邀请好友是取得URL
     */
    @ApiOperation( value = "点击侧边栏邀请好友是取得URL", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = String.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/getShareCodeUrl", method = RequestMethod.GET, produces = "application/json" )
    public Map<String, String> getShareCodeUrl(HttpServletRequest request) {
        logger.info("getShareCodeUrl");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        String language = request.getHeader(WalletConstants.REQUEST_HEADER_LANGUAGE);
        String content = eventApplyService.getShareCodeUrl(accountNo)+ "&lang=" + language;
        Map<String, String> result = new HashMap<>(1);
        result.put("result", content);
        return result;
    }
}
