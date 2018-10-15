package com.aethercoder.core.controller;

import com.aethercoder.core.service.URLDecipheringService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/26
 * @modified By:
 */
@RestController
@RequestMapping("csUrl")
@Api(tags = "csUrl", description = "客服界面URL")
public class URLDecipheringController {
    private static Logger logger = LoggerFactory.getLogger(URLDecipheringController.class);

    @Autowired
    URLDecipheringService urlDecipheringService;

    /***
     * Qbao 客服端工作台访客信息展示
     * @return
     */
    @ApiOperation(value = "Qbao 后台 客服端工作台访客信息展示", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getCustomerInfo", method = RequestMethod.POST, produces = "application/json")
    public Map getCustomerInfo(HttpServletRequest request) {
        logger.info("getCustomerInfo");

        return urlDecipheringService.getCustomerInfo(request);
    }
}