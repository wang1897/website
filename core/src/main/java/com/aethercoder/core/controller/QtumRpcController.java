package com.aethercoder.core.controller;

import com.aethercoder.core.service.QtumService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.QtumMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hepengfei on 22/11/2017.
 */

@RestController
@RequestMapping( value = "qtumRPC", produces = "application/json")
@Api( tags = "qtumRPC", description = "qtum后台接口" )
public class QtumRpcController {
    private static Logger logger = LoggerFactory.getLogger(QtumRpcController.class);

    @Autowired
    private QtumService qtumService;

    @ApiOperation(value = "获取地址的utxo", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = List.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/outputs/unspent", method = RequestMethod.GET)
    public List getUnspent(@RequestParam String addresses) {
        logger.info("outputs/unspent");
        return qtumService.getUnspentByAddresses(addresses);
    }


    @ApiOperation(value = "获取地址的交易记录", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = List.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/history/transaction/{limit}/{offset}", method = RequestMethod.GET)
    public List getHistory(@RequestParam String addresses, @RequestParam(required=false) Long startBlock, @RequestParam(required=false) Long endBlock,
                           @RequestParam(required=false) String contractAddress, @PathVariable("limit") Integer limit, @PathVariable("offset") Integer offset) {
        logger.info("/history/transaction/{limit}/{offset}");
        return qtumService.getTransHistoryByAddress(addresses, contractAddress, limit, offset, startBlock, endBlock);
    }


    @ApiOperation(value = "调用合约（不广播）", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = List.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/contract/call", method = RequestMethod.POST, consumes = "application/json" )
    public Map<String, List> callContract(@RequestBody Map param) throws Exception{
        logger.info("contract/call");
        Map<String, List> map = new HashMap<>();String contractAddress = (String)param.get("contractAddress");
        map.put("result", qtumService.callContract(contractAddress, (List)param.get("param")));
        return map;
    }


    @ApiOperation(value = "获取区块交易费用", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/estimateFee", method = RequestMethod.GET)
    public Map estimateFee(@RequestParam Integer nBlocks) throws Exception{
        logger.info("estimateFee");
        Double result = qtumService.estimateFee(nBlocks);
        Map map = new HashMap();
        map.put("result", result);
        return map;
    }


    @ApiOperation(value = "发送交易请求", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/sendRawTransaction", method = RequestMethod.POST, consumes = "application/json" )
    public Map sendRawTransaction(@RequestBody Map param) throws Exception{
        logger.info("sendRawTransaction");
        String result = qtumService.sendRawTransaction((String)param.get("param"));
        Map map = new HashMap();
        map.put("result", result);
        return map;
    }


    @ApiOperation(value = "获取交易详情", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/getTransaction", method = RequestMethod.GET)
    public Map getTransaction(@RequestParam String txhash) throws Exception{
        logger.info("getTransaction");
        return qtumService.getTransaction(txhash);
    }

    @ApiOperation(value = "获取dgp信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/blockchain/dgpinfo", method = RequestMethod.GET)
    public Map getDgpInfo() throws Exception{
        logger.info("blockchain/dgpinfo");
        return qtumService.getDGPInfo();
    }


    @ApiOperation(value = "获取地址的sha160值", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/sha160", method = RequestMethod.GET)
    public Map sha160(String addressStr) throws Exception{
        logger.info("sha160");
        org.bitcoinj.core.Address address = Address.fromBase58(QtumMainNetParams.get(), addressStr);
        String result = Hex.toHexString(address.getHash160());
        Map map = new HashMap();
        map.put("result", result);
        return map;
    }
}
