package com.aethercoder.core.controller;

import com.aethercoder.core.entity.fundUser.FundUserAddress;
import com.aethercoder.core.service.FundUserAddressService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2017/11/8 下午6:22
 */
@RestController
@RequestMapping( "fundUserAddress" )
@Api( tags = "fundUserAddress", description = "fundUser用户地址管理接口" )
public class FundUserAddressController {

    private static Logger logger = LoggerFactory.getLogger(FundUserAddressController.class);

    @Autowired
    private FundUserAddressService fundUserAddressService;


    /***
     * Qbao_fund 参与按钮按下-显示地址页面
     * @param id
     * @return
     */
    @ApiOperation(value="Qbao_fund 参与按钮按下-显示地址页面", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = FundUserAddress.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/partInOfFundUser",method = RequestMethod.GET,produces = "application/json")
    public FundUserAddress partInOfFundUSer(@RequestParam long id){
        logger.info("partInOfFundUser");
        Assert.notNull(id, "id not null");
        return fundUserAddressService.partIn(id);

    }

    /***
     * Qbao_fund 用户的交易地址表（保存4个地址）
     * @param fundUserAddress
     * @return
     */
    @ApiOperation(value="Qbao_fund 用户的交易地址表（保存4个地址）", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = FundUserAddress.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/confirmOfFundUser",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map<String, Object> confirmOfFundUser(@RequestBody FundUserAddress fundUserAddress){
        logger.info("confirmOfFundUser");
        Map<String, Object> map = new HashMap<>();
        FundUserAddress fundUserAddress1 = fundUserAddressService.save(fundUserAddress);
        if (null == fundUserAddress1){
            map.put("result","fail");
        }else{
            map.put("result",fundUserAddress1);
        }
        return map;
    }

}
