package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.ticket.Ticket;
import com.aethercoder.core.service.TicketService;
import com.aethercoder.foundation.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author lilangfeng
 * @date 2018/01/05
 **/
@RestController
@RequestMapping("ticket")
@Api(tags = "ticket", description = "问题详情接口管理")
public class TicketController {

    private static Logger logger = LoggerFactory.getLogger(TicketController.class);

    @Autowired
    private TicketService ticketService;

    /**
     * *创建工单
     **/
    @ApiOperation(value = "添加回复", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Ticket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/createTicket", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Ticket createTicket(@RequestBody Ticket ticket) {
        return ticketService.createTicket(ticket);
    }

    /***
     * Qbao_后台 问题详情模糊查询+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "Qbao_后台 问题详情模糊查询+分页", notes = "params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Ticket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findTicketsByPage", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    public Page<Ticket> findTicketsByPage(Integer page, Integer size, String question, Long type, Integer status, String startTime, String endTime) {
        logger.info("findTicketsByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return ticketService.findTicketsByPage(page, size, question, type, status, startTime, endTime);
    }
    /**
     * 更新问题详情
     */
    @ApiOperation(value = "问题更新", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Ticket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/updateTicket", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Ticket updateTicket(@RequestBody Ticket ticket) {
        logger.info("admin/updateTicket");
        StringUtil.isIllegalDBVercharThrowEx(ticket.toString(),WalletConstants.GROUP_NOTICE_CONTENT_LENGTH);
        Assert.notNull(ticket.getAnswer(), "回复内容不能为空");
        Assert.notNull(ticket.getStatus(), "进度不能为空");
        return ticketService.updateTicket(ticket);
    }
    /***
     * Qbao_后台 问题类型统计
     * @return
     */
    @ApiOperation(value = "Qbao_后台 问题类型统计", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Ticket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/countByType", method = RequestMethod.GET)
    public List<Ticket> findTicketsByPage() {
        logger.info("countByType");
        return ticketService.countByType();
    }

}

