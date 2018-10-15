package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.admin.AdminAccount;
import com.aethercoder.core.entity.json.Data;
import com.aethercoder.core.entity.pay.Order;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.CountryInformation;
import com.aethercoder.core.entity.wallet.Customer;
import com.aethercoder.core.security.JwtTokenUtil;
import com.aethercoder.core.service.CountryInformationService;
import com.aethercoder.core.service.CustomerService;
import com.aethercoder.core.service.OrderService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Jiawei.Tao
 * @Description:
 * @Date: Created in 2018/3/18
 * @modified By:
 */
@RestController
@RequestMapping("order")
@Api(tags = "order", description = "扫码支付接口")
public class OrderController {

    private static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CountryInformationService countryInformationService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String tokenHeader = CommonConstants.HEADER_WEB_TOKEN_KEY;

    /***
     * Qbao App 扫码请求生成订单
     * @return
     */
    @ApiOperation(value = "Qbao App 扫码请求生成订单", notes = "返回Order")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Page.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/preOrder", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Order preOrder(@RequestBody Customer customer){
        logger.info("preOrder");

//        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
        return orderService.preCreateOrder(customer,accountNo);
    }

    /***
     * Qbao App 提交支付
     * @return
     */
    @ApiOperation(value = "Qbao App 提交支付", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/payOrder", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map payOrder(@RequestBody Order customer){
        logger.info("/payOrder");
        Assert.notNull(customer,"customer not null");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
        customer.setAccountNo(accountNo);
        String orderNo = orderService.payOrder(customer);

        Map map = new HashMap();
        map.put("result","success");
        map.put("orderId",orderNo);
        return map;
    }

    /***
     * Qbao App 查询交易
     * @return
     */
    @ApiOperation(value = "Qbao App 查询交易", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/queryOrder", method = RequestMethod.GET, produces = "application/json")
    public Order queryOrder(@RequestParam String orderNo){
        logger.info("/queryOrder");
        Assert.notNull(orderNo,"orderNo not null");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");

        return orderService.queryOrder(orderNo, accountNo);
    }

    /***
     * Qbao App 查询用户可否支付
     * @return
     */
    @ApiOperation(value = "Qbao App 查询用户可否支付", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/queryUser", method = RequestMethod.GET, produces = "application/json")
    public Map queryUser(){
        logger.info("/queryUser");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");

        Boolean canOrder = orderService.queryUser(accountNo);

        Map map = new HashMap();
        map.put("result","success");
        map.put("canOrder",canOrder);
        return map;
    }

    /***
     * Qbao App 上传用户words
     * @return
     */
    @ApiOperation(value = "Qbao App 上传用户words", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/setNickWords", method = RequestMethod.POST, produces = "application/json")
    public Map setNickWords(@RequestBody Map words){
        logger.info("/setNickWords");
        String payWords = words.get("words").toString();
        Assert.notNull(payWords,"words not null");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");

        orderService.setNickWords(accountNo,payWords);

        Map map = new HashMap();
        map.put("result","success");
        return map;
    }

}
