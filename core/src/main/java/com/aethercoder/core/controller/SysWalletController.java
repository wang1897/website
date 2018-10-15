package com.aethercoder.core.controller;


import com.aethercoder.core.entity.wallet.SysWallet;
import com.aethercoder.core.service.SysWalletService;
import com.aethercoder.core.service.WalletService;
import com.aethercoder.foundation.service.LocaleMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GUOFEIYAN on 2017/9/18.
 */

@RestController
@RequestMapping("wallet")
@Api(tags = "SysWallet", description = "钱包配置接口管理")
public class SysWalletController {

    private static Logger logger = LoggerFactory.getLogger(SysWalletController.class);
    @Autowired
    private SysWalletService sysWalletService;

    @Autowired
    private WalletService walletService;

    @Autowired
    public LocaleMessageService localeMessageUtil;

/*
    *//**
     * Qbao 添加钱包信息
     * @param sysWallet
     * @return event
     * @throws Throwable
     *//*
    @ApiOperation(value = "Qbao 添加钱包信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveSysWallet", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public SysWallet saveSysWallet(@RequestBody SysWallet sysWallet) {
        logger.info("admin/saveSysWallet");
        Assert.notNull(sysWallet.getName(),"name not null");
        Assert.notNull(sysWallet.getValue(),"value not null");
        return sysWalletService.saveSysWallet(sysWallet);
    }*/


    /***
     * Qbao 根据name查询钱包配置信息
     * @return
     */
    @ApiOperation(value="Qbao 根据name查询钱包配置信息", notes="返回SysWallet对象")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findSysWalletByName",method = RequestMethod.GET,produces = "application/json")
    public SysWallet findSysWalletByNameShow(@RequestParam("name") String name){
        logger.info("findSysWalletByName");
        Assert.notNull(name,"name not null");
        return sysWalletService.findByName(name);
    }

    /***
     * Qbao 启动提币批处理
     * @return
     */
    @ApiOperation(value="Qbao 启动提币批处理", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/startWithdrawBatch",method = RequestMethod.GET,produces = "application/json")
    public Map startWithdrawBatch(){
        logger.info("startWithdrawBatch");
        walletService.startWithdrawBatch();
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;
    }
/*
    *//***
     * Qbao 查询钱包配置信息
     * @return
     *//*
    @ApiOperation(value="Qbao 查询钱包配置信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findSysWallets",method = RequestMethod.GET,produces = "application/json")
    public List<SysWallet> findSysWallets(){
        logger.info("admin/findSysWallets");
        return sysWalletService.findSysWallets();
    }*/


/*

    */
/***
     * Qbao 修改钱包配置信息
     * @return
     *//*

    @ApiOperation(value="Qbao 修改钱包配置信息", notes="返回SysWallet对象")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateSysWallet",method = RequestMethod.POST,produces = "application/json",consumes = "application/json")
    public SysWallet updateSysWallet(SysWallet sysWallet){
        logger.info("admin/updateSysWallet");
        Assert.notNull(sysWallet.getId(),"id not null");
        return sysWalletService.updateSysWallet(sysWallet);
    }
*/


  /*  *//***
     * Qbao 转账
     * @return
     *//*
    @ApiOperation(value="Qbao 转账", notes="返回SysWallet对象")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/sendToken",method = RequestMethod.POST,produces = "application/json", consumes = "application/json")
    public void sendToken(@RequestBody java.util.Map<String,Object> map){
        logger.info("sendToken");
        //进行转账
        walletService.createAbiMethod(map.get("seed").toString(), map.get("fromAddress").toString(), map.get("toAddress").toString(),
                map.get("contractAddress").toString(),map.get("amount").toString(), map.get("fee").toString(), Integer.valueOf(map.get("gasLimit").toString()), new BigDecimal(map.get("bigDecimal").toString()),Integer.valueOf(map.get("gasPrice").toString()),new BigDecimal(map.get("decimalFeePerKb").toString()));
    }
*/
//    /***
//     * Qbao_提币
//     * @return
//     */
//    @ApiOperation(value="Qbao_提币", notes="参数：1。String accountNo 2。 BigDecimal actualAmount 3。 String toAddress 4。long unit  5。BigDecimal decimalFeePerKb ")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
//            @ApiResponse(code = 400, message = "输入不正确"),
//            @ApiResponse(code = 401, message = "没有权限访问"),
//            @ApiResponse(code = 500, message = "系统错误")
//    }
//    )
//    @RequestMapping(value = "/mentionMoney",method = RequestMethod.POST,produces = "application/json", consumes = "application/json")
//    public Map<String, Object> mentionMoney(@RequestBody java.util.Map<String,Object> map ){
//        logger.info("mentionMoney");
//        Map<String, Object> result = new HashMap<>();
//        walletService.mentionMoney(
//                map.get("accountNo").toString(), new BigDecimal(map.get("actualAmount").toString()), map.get("toAddress").toString(),Integer.parseInt(map.get("unit").toString()),
//                 new BigDecimal(map.get("decimalFeePerKb").toString()));
//        result.put("result", localeMessageUtil.getLocalMessage("MENTION_MSG",null));
//        return result;
//    }


}
