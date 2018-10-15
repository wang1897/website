package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.core.service.ContractService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author guofeiyan
 * @date 2017/12/8 下午2:42
 */

@RestController
@RequestMapping("accountBalance")
@Api(tags = "accountBalance", description = "Qbao余额接口管理")
public class AccountBalanceController {
    private static Logger logger = LoggerFactory.getLogger(AccountBalanceController.class);

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private ContractService contractService;

    /***
     * Qbao 获得所有Qbao余额
     * @return
     */
    @ApiOperation(value = "Qbao 获得所有Qbao余额", notes = "返回List<contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AccountBalance.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getAllBalance", method = RequestMethod.GET, produces = "application/json")
    public Page<AccountBalance> getAllBalance(Integer page, Integer size, String accountNo, Long unit, BigDecimal maxAmount,BigDecimal minAmount,String accountName) {
        logger.info("getAllBalance");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return accountBalanceService.getAllAccountBalance( page, size, accountNo, unit, maxAmount, minAmount, accountName);
    }

    /***
     * Qbao 获得个人Qbao余额
     * @return
     */
    @ApiOperation(value = "Qbao 获得个人Qbao余额", notes = "返回List<contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AccountBalance.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getBalanceByAccountNo", method = RequestMethod.GET, produces = "application/json")
    public List<AccountBalance> getBalanceByAccountNo() {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
        logger.info("getBalanceByAccountNo");
        return accountBalanceService.findByAccountNo(accountNo);
    }

    /***
     * Qbao_后台 报表——充值余额概况
     * @return
     */
    @ApiOperation(value="Qbao_后台 报表——充值余额概况", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getRechargeMoneyProfile",method = RequestMethod.GET,produces = "application/json")
    public Map getExchangeLogProfile(){
        logger.info("getRechargeMoneyProfile");
        return accountBalanceService.getRechargeMoneyProfile();
    }
/*
    *//***
     * Qbao 获得个人QBE余额及兑换QBT个数
     * @return
     *//*
    @ApiOperation(value = "Qbao 获得个人QBE余额及兑换QBT个数", notes = "返回List<contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AccountBalance.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getDefaultExchangeQBE", method = RequestMethod.GET, produces = "application/json")
    public Map getDefaultExchangeQBE(@RequestParam String accountNo, @RequestParam Long id) {
        logger.info("getDefaultExchangeQBE");
        Contract contract = contractService.findContractByName(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        return accountBalanceService.getDefaultExchangeQBE(accountNo,contract.getId(),id);
    }

    *//***
     * Qbao 兑换QBT(2w qbe = 1 qbt)
     * @return
     *//*
    @ApiOperation(value = "Qbao 兑换QBT(2w qbe = 1 qbt)", notes = "返回List<contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AccountBalance.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getExchangeQBE", method = RequestMethod.GET, produces = "application/json")
    public Map getExchangeQBE(@RequestParam String  accountNo, Long id) {
        logger.info("getExchangeQBE："+accountNo);
        Assert.notNull(accountNo,"accountNo not  null");
        Contract contract = contractService.findContractByName(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        return accountBalanceService.getExchangeQBE(accountNo,contract.getId(),id);
    }*/

}
