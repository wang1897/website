package com.aethercoder.core.controller;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.WithdrawApply;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.ExchangeLogService;
import com.aethercoder.core.service.SysWalletService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2017/12/5 上午11:33
 */
@RestController
@RequestMapping("exchangeLog")
@Api(tags = "exchangeLog", description = "交易接口管理")
public class ExchangeLogController {

    private static Logger logger = LoggerFactory.getLogger(ExchangeLogController.class);

    @Autowired
    private ExchangeLogService exchangeLogService;
    @Autowired
    private AccountService accountService;

    @Autowired
    private SysWalletService sysWalletService;

    /***
     * Qbao_获取个人交易记录
     * @return
     */
    @ApiOperation(value = "Qbao_获取个人交易记录", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findExchangeLogByPage", method = RequestMethod.GET, produces = "application/json")
    public Page<ExchangeLog> findExchangeLogByPage(Integer page, Integer size, Long unit, Integer type, Integer status) {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
        logger.info("findExchangeLogByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return exchangeLogService.findExchangeLogsByPage(page, size, accountNo, unit, type, status, null,null,null,false);
    }
//
//    /***
//     * Qbao_查询提币请求接口
//     * @return
//     */
//    @ApiOperation(value = "Qbao_查询提币请求接口", notes = "")
//    @ApiResponses(value = {
//            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
//            @ApiResponse(code = 400, message = "输入不正确"),
//            @ApiResponse(code = 401, message = "没有权限访问"),
//            @ApiResponse(code = 500, message = "系统错误")
//    }
//    )
//    @RequestMapping(value = "/canWithdraw", method = RequestMethod.GET, produces = "application/json")
//    public WithdrawApply canWithdraw(@RequestParam Long unit) {
//        logger.info("canWithdraw");
//        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getCredentials();
//        // 获取该币种的资金池，和该用户本次能提笔的最大金额。
//
//        WithdrawApply withdrawApply = exchangeLogService.checkBeforeWithdraw(account.getAccountNo(),unit);
//        Assert.notNull(withdrawApply, "withdrawApply is null");
//        return withdrawApply;
//    }

    /***
     * Qbao_提币接口
     * @return
     */
    @ApiOperation(value = "Qbao_提币接口", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/withdrawApply", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public WithdrawApply withdrawApply(@RequestBody WithdrawApply withdrawApply,HttpServletRequest request) {
        logger.info("withdrawApply");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");


        Assert.notNull(accountNo, "accountNo is null");
        // 获取用户语言
        String language = request.getHeader(WalletConstants.REQUEST_HEADER_LANGUAGE);
        Account account = new Account();
        account.setAccountNo(accountNo);
        account.setLanguage(language);
        // 保存用户信息
        accountService.updateAccountsLanguage(account);
        withdrawApply.setAccountNo(account.getAccountNo());
        return exchangeLogService.withdrawApply(withdrawApply);
    }

    /***
     * Qbao_充值记录
     * @return
     */
    @ApiOperation(value = "Qbao_添加充值记录", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/rechargeMoney", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ExchangeLog rechargeMoney(@RequestBody ExchangeLog exchangeLog) {
        logger.info("rechargeMoney");
        //并添加充值表记录
        exchangeLog.setType(WalletConstants.RECHANGER_TYPE);
        //未确认状态
        exchangeLog.setStatus(WalletConstants.UNCONFIRMED);

        String address = sysWalletService.findByName(WalletConstants.SYS_CONFIG_WALLET_ADDRESS).getValue();
        if (!address.equals(exchangeLog.getAddress())) {
            throw new AppException(ErrorCode.NO_ADDRESS);
        }
        ExchangeLog exchangeLog1 = exchangeLogService.saveExchangeLogFromFront(exchangeLog);
        Assert.notNull(exchangeLog1, "exchangeLog1 is null");
        return exchangeLog1;
    }

    /**
     * Qbao-修改交易状态
     *
     * @return
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 修改交易状态", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/changeExchangeLog", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    @Transactional
    public ExchangeLog changeExchangeLog(@RequestBody ExchangeLog exchangeLog) {
        logger.info("changeExchangeLog");
        Assert.notNull(exchangeLog.getId(), "id not null");
        return exchangeLogService.updateExchangeLog(exchangeLog);
    }


    /***
     * Qbao_进行抽奖活动
     * @return
     */
    @ApiOperation(value = "Qbao_抽奖活动", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/takeBonus", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Map takeBonus(@RequestBody ExchangeLog exchangeLog) {
        logger.info("takeBonus");
        Map<String, Object> map = new HashMap<>();
        Assert.notNull(exchangeLog.getAccountNo(), "accountNo not null");
        exchangeLog.setType(WalletConstants.TAKE_BONUS);

        ExchangeLog exchangeLog1 = exchangeLogService.saveTakeBonus(exchangeLog);
        map.put("result",exchangeLog1.getAmount());
        return  map;
    }

    /***
     * 获得所有排行记录
     * @return
     */
    @ApiOperation(value = "获得所有排行记录", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/takeBonusRankingList", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> takeBonusRankingList() {
        logger.info("takeBonusRankingList");
        Map<String, Object> map = new HashMap<>();
        //获得所有排行记录
        List<ExchangeLog> exchangeLogs = exchangeLogService.takeBonusRankingList();
        map.put("exchangeLogs", exchangeLogs);
        return map;

    }

    /***
     * 获取自己的排行记录
     * @return
     */
    @ApiOperation(value = "获取自己的排行记录", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/takeBonusRankingListByAccount", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> takeBonusRankingListByAccount() {
        logger.info("takeBonusRankingListByAccount");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        //获取自己的排行记录
        return  exchangeLogService.getTakeBonusRankingListByCount(accountNo);
    }

    /***
     * Qbao_获取所有交易记录
     * @return
     */
    @ApiOperation(value = "Qbao_获取所有交易记录", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLog.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAllExchangeLog", method = RequestMethod.GET, produces = "application/json")
    public Page<ExchangeLog> findAllExchangeLog(Integer page, Integer size, String accountNo, Long unit, Integer type, Integer status, String beginDate, String endDate, String accountName,Boolean isDelete) {
        logger.info("findAllExchangeLog");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return exchangeLogService.findExchangeLogsByPage(page, size, accountNo, unit, type, status, beginDate, endDate, accountName,isDelete);
    }

    /***
     * Qbao_后台 报表——Qbao Energy概况
     * @return
     */
    @ApiOperation(value="Qbao_后台 报表——Qbao Energy概况", notes="QBE总发放额度  allAmount / QBE>0的用户数（占比）  userQBE / QBE>10,000的人的QBE总量/QBE总量  amountQBE /QBE>10,000的人数/累计用户量 userQBELessThousand / 人均Energy量 QBEAvg")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getExchangeLogProfile",method = RequestMethod.GET,produces = "application/json")
    public Map getExchangeLogProfile(){
        logger.info("getExchangeLogProfile");
        return exchangeLogService.getExchangeLogProfile();
    }

    /***
     * Qbao_后台 每天充值金额
     * @return
     */
    @ApiOperation(value="Qbao_后台 每天充值金额", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getEverydayAmount",method = RequestMethod.GET,produces = "application/json")
    public Map getEverydayAmount(){
        logger.info("getEverydayAmount");
        return exchangeLogService.getEverydayAmount();
    }

    /***
     * Qbao_后台 每周充值金额
     * @return
     */
    @ApiOperation(value="Qbao_后台 每周充值金额", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getEveryweekAmount",method = RequestMethod.GET,produces = "application/json")
    public Map getEveryweekAmount(){
        logger.info("getEveryweekAmount");
        return exchangeLogService.getEveryweekAmount();
    }

    /***
     * Qbao_后台 每月充值金额
     * @return
     */
    @ApiOperation(value="Qbao_后台 每月充值金额", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getEverymonthAmount",method = RequestMethod.GET,produces = "application/json")
    public Map getEverymonthAmount(){
        logger.info("getEverymonthAmount");
        return exchangeLogService.getEverymonthAmount();
    }

    /***
     * Qbao_后台 每天提币金额
     * @return
     */
    @ApiOperation(value="Qbao_后台 每天提币金额", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getEverydayDrawAmount",method = RequestMethod.GET,produces = "application/json")
    public Map getEverydayDrawAmount(){
        logger.info("getEverydayDrawAmount");
        return exchangeLogService.getEverydayDrawAmount();
    }

    /***
     * Qbao_后台 每周提币金额
     * @return
     */
    @ApiOperation(value="Qbao_后台 每周提币金额", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getEveryweekDrawAmount",method = RequestMethod.GET,produces = "application/json")
    public Map getEveryweekDrawAmount(){
        logger.info("getEveryweekDrawAmount");
        return exchangeLogService.getEveryweekDrawAmount();
    }

    /***
     * Qbao_后台 每月提币金额
     * @return
     */
    @ApiOperation(value="Qbao_后台 每月提币金额", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getEverymonthDrawAmount",method = RequestMethod.GET,produces = "application/json")
    public Map getEverymonthDrawAmount(){
        logger.info("getEverymonthDrawAmount");
        return exchangeLogService.getEverymonthDrawAmount();
    }
}
