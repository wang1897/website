package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.event.GetRedPacket;
import com.aethercoder.core.entity.event.RedPacketInfo;
import com.aethercoder.core.entity.event.SendRedPacket;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.GetRedPacketService;
import com.aethercoder.core.service.SendRedPacketService;
import com.aethercoder.foundation.service.LocaleMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.transaction.Transactional;

/**
 * @auther Jiawei.tao
 * @date 2017/12/7 下午2:40
 */
@RestController
@RequestMapping( "redPacket" )
@Api( tags = "redPacket", description = "红包收发管理" )
public class RedPacketController {

    private static Logger logger = LoggerFactory.getLogger(RedPacketController.class);

    @Autowired
    private SendRedPacketService sendRedPacketService;
    @Autowired
    private GetRedPacketService getRedPacketService;
    @Autowired
    public LocaleMessageService localeMessageUtil;

    /**
     * 创建红包
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/createRedPacket", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "创建红包", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SendRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    public SendRedPacket createRedPacket(@RequestBody SendRedPacket redPacket) {
        logger.info("createRedPacket");
        return sendRedPacketService.createRedPacket(redPacket);
    }

    /**
     * 抢红包
     * @return
     * @throws Throwable
     */
    @ApiOperation(value="抢红包", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GetRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getRedPacket", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public GetRedPacket getRedPacket(@RequestBody GetRedPacket redPacket){
    logger.info("getRedPacket");
        return getRedPacketService.getRedPacket(redPacket);
    }
    /***
     * Qbao 取得一个红包详细信息
     * @param redPacketId
     * @return
     */
    @ApiOperation( value = "Qbao 取得一个红包详细信息", notes = "返回RedPacketInfo" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = RedPacketInfo.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/getRedPacketInfo", method = RequestMethod.GET, produces = "application/json")
    public RedPacketInfo getRedPacketInfo(@RequestParam( "redPacketId" ) Long redPacketId) {
        logger.info("getRedPacketInfo");
        return sendRedPacketService.getRedPacketInfo(redPacketId);
    }

    /***
     * Qbao_后台 查询发红包信息+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台 查询发红包信息+分页", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SendRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findSendPacketsByAccountNoByPage",method = RequestMethod.GET,produces = "application/json")
    public Page<SendRedPacket> findSendPacketsByAccountNo( Integer page, Integer size, Long unit){
        logger.info("findSendPacketsByAccountNoByPage");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return sendRedPacketService.findSendPacketsByAccountNo(page,size, accountNo, unit);
    }

    /***
     * Qbao_后台 查询收红包信息+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台 查询收红包信息+分页", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GetRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findGetPacketsByAccountNoByPage",method = RequestMethod.GET,produces = "application/json")
    public Page<GetRedPacket> findGetPacketsByAccountNo( Integer page, Integer size, Long unit){
        logger.info("findGetPacketsByAccountNoByPage");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return getRedPacketService.findGetPacketsByAccountNo(page,size,accountNo,unit);
    }
}
