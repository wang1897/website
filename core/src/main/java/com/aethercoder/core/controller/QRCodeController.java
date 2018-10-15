/*
package com.aethercoder.core.controller;

import com.aethercoder.core.entity.pay.Order;
import com.aethercoder.core.entity.wallet.Customer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

*/
/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/28
 * @modified By:
 *//*

@RestController
@RequestMapping("qrCode")
@Api(tags = "qrCode", description = "二维码接口")
public class QRCodeController {

    private static Logger logger = LoggerFactory.getLogger(QRCodeController.class);
    */
/***
     * Qbao App 扫码请求生成订单
     * @return
     *//*

    @ApiOperation(value = "Qbao App 扫码请求生成订单", notes = "返回Order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Page.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/preOrder", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Order preOrder(@RequestBody Customer customer){
        logger.info("getCustomerList");

        return orderService.preCreateOrder(customer);
    }
}
*/
