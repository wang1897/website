package com.aethercoder.core.controller;

import com.aethercoder.core.entity.ticket.TicketType;
import com.aethercoder.core.service.TicketTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *@author lilangfeng
 * @date  2018/01/05
 **/

@RestController
@RequestMapping("ticketType")
@Api(tags ="ticketType", description= "问题类型")
 public class TicketTypeController {
    private static Logger logger = LoggerFactory.getLogger(TicketController.class);
    @Autowired
    private TicketTypeService ticketTypeService;
    @ApiOperation(value = "问题类型 ", notes = " ")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "成功", response = TicketType.class),
        @ApiResponse(code = 400, message = "输入不正确"),
        @ApiResponse(code = 401, message = "没有权限访问"),
        @ApiResponse(code = 500, message = "系统错误")
    })
    @RequestMapping(value = "/getTicketTypeList", method = RequestMethod.GET)
    public List<TicketType> getTicketTypeList() {
            return  ticketTypeService.findTicketTypeAll();
        }


}
