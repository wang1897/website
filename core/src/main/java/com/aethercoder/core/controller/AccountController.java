package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.social.Group;
import com.aethercoder.core.entity.social.UserInfo;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.security.JwtTokenUtil;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.RecordPasswordService;
import com.aethercoder.core.service.WalletService;
import com.aethercoder.foundation.contants.CommonConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("account")
@Api(tags = "account", description = "钱包接口管理")
public class AccountController {

    private static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;
    @Autowired
    private RecordPasswordService recordPasswordService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private WalletService walletService;

    private String tokenHeader = CommonConstants.HEADER_WEB_TOKEN_KEY;


    /**
     * Qbao-创建钱包
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/createAccount", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ApiOperation(value="Qbao 创建用户（钱包）", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    public Account createAccount(@RequestBody Account account, HttpServletResponse response){
        logger.info("createAccount");
        Account account1 = accountService.createAccount(account);
        response.setHeader(CommonConstants.HEADER_APP_TOKEN_KEY, accountService.initToken(account1.getAccountNo()));
        return account1;
    }

    /**
     * Qbao-导入钱包
     * @return
     * @throws Throwable
     */
    @ApiOperation(value="Qbao 导入钱包", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/importAccount", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Account importAccount(@RequestBody Account account, HttpServletResponse response){
        logger.info("importAccount");
        Account account1 = accountService.importAccount(account);
        response.setHeader(CommonConstants.HEADER_APP_TOKEN_KEY, accountService.initToken(account1.getAccountNo()));
        return account1;
    }

    /**
     * Qbao-修改缺省地址
     * @return
     * @throws Throwable
     */
    @ApiOperation(value="Qbao 修改缺省地址", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/changeAddress", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public Account changeAddress(@RequestParam String address){
        logger.info("changeAddress");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
        return accountService.changeDefaultAddress(accountNo, address);
    }


    /**
     * Qbao-修改缺省地址
     * @return
     * @throws Throwable
     */
    @ApiOperation(value="Qbao 修改缺省地址", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/changeAddressWithBody", method = RequestMethod.POST, produces = "application/json")
    @Transactional
    public Account changeAddressWithBody(@RequestBody Map<String, String> map){
        String address = map.get("address");
        logger.info("changeAddress");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
        return accountService.changeDefaultAddress(accountNo, address);
    }

    /**
     * Qbao-修改钱包信息
     * @return
     * @throws Throwable
     */
    @ApiOperation(value="Qbao 修改钱包信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/updateAccount", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Account updateAccount(@RequestBody Account account,HttpServletRequest request){

        logger.info("updateAccount");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        account.setAccountNo(accountHeader.getAccountNo());

        String language = request.getHeader(WalletConstants.REQUEST_HEADER_LANGUAGE);
        account.setLanguage(language);
        return accountService.updateAccount(account);
    }

    /**
     * Qbao-修改钱包信息(只更新每日邀请次数,邀请奖励,移除系统黑名单)
     * @return
     * @throws Throwable
     */
    @ApiOperation(value="Qbao 修改钱包信息(每日邀请次数,邀请奖励)", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateAccountByInviteDaily", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Account updateAccountByInviteDaily(@RequestBody Account account){
        logger.info("/admin/updateAccountByInviteDaily");
        return accountService.updateAccountByInviteDaily(account);
    }


/*
    *//***
     * Qbao_io 用户注册
     * @param account
     * @return
     *//*
    @ApiOperation(value="Qbao_io 用户注册", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/saveUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Account saveUser(@RequestBody Account account){
        logger.info("saveUser");
        Assert.notNull(account.getEmail(),"email not  null");
        Assert.notNull(account.getAccountName(),"accountName not null");
        Assert.notNull(account.getPassword(),"password not null");
        logger.debug("saveUser", account);
        Account u = accountService.saveUser(account);
        return u;

    }*/

    /**
     * Qbao 获取指定等级的成员信息
     *
     * @param level
     * @return number
     */
    @ApiOperation(value = "Qbao 获取指定等级的成员信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @org.springframework.transaction.annotation.Transactional
    @RequestMapping(value = "/queryAccountsByLevel", method = RequestMethod.GET, produces = "application/json")
    public List<Account> queryAccountsByLevel(@RequestParam("level") Long level) {
        logger.info("queryAccountsByLevel");
        return accountService.queryAccountsByLevel(level);
    }


    /**
     * Qbao 获取有内测权限的成员信息
     *
     * @param authority
     * @return number
     */
    @ApiOperation(value = "Qbao 获取有内测权限的成员信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @org.springframework.transaction.annotation.Transactional
    @RequestMapping(value = "/queryAccountsByAuthority", method = RequestMethod.GET, produces = "application/json")
    public List<Account> queryAccountsByAuthority(@RequestParam("authority") Boolean authority) {
        logger.info("queryAccountsByAuthority");
        return accountService.queryAccountsByAuthority(authority);
    }

    /**
     * Qbao 修改成员的内侧权
     *
     * @param authority
     * @return number
     */
    @ApiOperation(value = "Qbao 修改成员的内侧权", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @org.springframework.transaction.annotation.Transactional
    @RequestMapping(value = "/updateAccountsByAccountNoAndAuthority", method = RequestMethod.GET, produces = "application/json")
    public String updateAccountsByAccountNoAndAuthority(@RequestParam("accountNo") String accountNo, @RequestParam("authority") Boolean authority) {
        logger.info("updateAccountsByAccountNoAndAuthority");
        accountService.updateAccountsByAccountNoAndAuthority(accountNo,authority);
        return "success";
    }


    /***
     * Qbao 钱包用户注册_根据no查找account用户信息
     * @param accountNo
     * @return
     */
    @ApiOperation(value="Qbao 校验根据accountNo查找钱包用户信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/findAccountName",method = RequestMethod.GET,produces = "application/json")
    public Account findAccountName(@RequestParam("accountNo") String accountNo, HttpServletRequest request){
        logger.info("findAccountName");
        Assert.notNull(accountNo,"accountNo not null");
        logger.debug("findAccountName", accountNo);
        return accountService.findAccountByAccountNo(accountNo);

    }

    /***
     * Qbao 获取好友信息
     * @return
     */
    @ApiOperation(value="Qbao 获取好友信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/getFriendInfo",method = RequestMethod.GET,produces = "application/json")
    public Account getFriendInfo(@RequestParam String accountNo){

        logger.info("getFriendInfo");
        Assert.notNull(accountNo,"accountNo not null");
        logger.debug("getFriendInfo", accountNo);
        return accountService.getFriendInfo(accountNo);

    }

    /***
     * Qbao 获取好友名字
     * @return
     */
    @ApiOperation(value="Qbao 获取好友信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/getFriendName",method = RequestMethod.GET,produces = "application/json")
    public String getFriendName(@RequestParam String accountNo){

        logger.info("getFriendName");
        Assert.notNull(accountNo,"accountNo not null");
        logger.debug("getFriendName", accountNo);
        return accountService.getFriendName(accountNo);

    }

   /* *//***
     * Qbao_io 钱包用户注册
     * @param account
     * @return
     *//*
    @ApiOperation(value="Qbao_io 钱包用户注册", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/saveAccount",method = RequestMethod.POST,consumes = "application/json",produces = "application/json")
    @Transactional
    public Account saveAccount(@RequestBody Account account){
        logger.info("saveAccount");
        Assert.notNull(account.getEmail(),"email not null");
        Assert.notNull(account.getPassword(),"password not null");
        logger.debug("saveAccount", account);
        Account u = accountService.saveAccount(account);
        return u;

    }

    *//***
     * Qbao_io 用户验证激活
     * @param account
     * @return
     *//*
    @ApiOperation(value="Qbao_io 用户验证激活", notes="注册来源：0：IOS 1:安卓 2:Web  激活成功返回message：激活成功／激活失败返回message=该用户激活失败！")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/activateUser",method = RequestMethod.POST,consumes = "application/json",produces = "application/json")
    public Map activateUser(@RequestBody Account account){
        logger.info("activateUser");
        Assert.notNull(account.getAccountNo(),"accountNo not null");
        logger.debug("activateUser ", account);

        byte[] b  = UrlUtil.decodeBufferBase64(account.getAccountNo());

        String t = new String(b);
//        long l = Long.parseLong(t);
        Account account1 = accountService.activateUser(t);
        if(account1!=null){
            map.put("message","激活成功");
        }
        return map;
    }


    *//***
     * Qbao_io 检验邮箱是否存在
     * @param email
     * @return
     *//*
    @ApiOperation(value="Qbao_io 检验邮箱是否存在", notes="返回boolean(message=true :表示该邮箱可用／message=false :表示该邮箱不可用)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/checkIsEmail",method = RequestMethod.GET,produces = "application/json")
    public Map<String,Object> checkIsEmail(@RequestParam("email") String email){
        logger.info("checkIsEmail");
        logger.debug("checkIsEmail ", email);
         Map<String, Object> map = new HashMap<>();
        if(accountService.findByEmail(email)==null){
            map.put("result", true);
        }else {
            map.put("result", false);
        }

        return map;
    }

    *//***
     * Qbao_io 用户登录
     * 1。判断是邮箱／accountNo登陆
     * 2。判断该用户是否存在
     * 3。判断该用户是否激活
     * @param user
     * @return
     *//*
    @ApiOperation(value="Qbao_io 用户登录", notes="邮箱不存在 or accountNo不存在 or 密码有误：返回message=用户名或密码输入有误 ／ 用户未激活：返回message=抱歉！该用户没有激活！")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/login",method = RequestMethod.POST,produces = "application/json", consumes = "application/json")
    public Account login(@RequestBody Map user, HttpServletResponse response) {
        logger.info("login");
        logger.debug("login ", user.get("noOrEmail").toString() + "," + user.get("password").toString());
        Account account = accountService.login(user.get("noOrEmail").toString() , user.get("password").toString());
        response.setHeader(tokenHeader, jwtTokenUtil.generateToken(account.getAccountNo()));
        return account;
    }


    *//***
     * Qbao_io 用户重置密码_发送邮箱
     * @param email
     * @return
     *//*
    @ApiOperation(value="Qbao_io 用户重置密码_发送邮箱", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/resetPwdEmail",method = RequestMethod.GET,produces = "application/json")
    @Transactional
    public Account resetPwdEmail(@RequestParam("email") String email){
        logger.info("resetPwdEmail");
        Assert.notNull(email,"email not null");
        logger.debug("resetPwdEmail ", email);
        Account account = accountService.passwordResetEamil(email);
        return account;
    }

    *//***
     * Qbao_io 用户重置密码
     * @param accountNo
     * @param password
     * @return
     *//*
    @ApiOperation(value="Qbao_io 用户重置密码", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/resetPwd",method = RequestMethod.GET,produces = "application/json")
    @Transactional
    public Account resetPwd(@RequestParam("accountNo") String accountNo,@RequestParam("password")String password,@RequestParam("code")String code){
        logger.info("resetPwd");
        Assert.notNull(accountNo,"accountNo not null");
        Assert.notNull(password,"password not null");
        Assert.notNull(code,"code not null");
        logger.debug("resetPwd ", accountNo+","+password+","+code);

        byte[] b  = UrlUtil.decodeBufferBase64(accountNo);

        String t = new String(b);

        return accountService.passwordReset(t,password,code);
    }

    *//***
     * Qbao_io 用户重置密码地址是否失效
     * @param code
     * @return
     *//*
    @ApiOperation(value="Qbao_io 用户重置密码地址是否失效", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = RecordPassword.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/resetPwdUrl",method = RequestMethod.GET,produces = "application/json")
    @Transactional
    public RecordPassword resetPwd(@RequestParam("code")String code){
        logger.info("resetPwdUrl");
        Assert.notNull(code,"code not null");
        logger.debug("resetPwdUrl ", code);

        return  recordPasswordService.checkUrl(code);
    }

    *//***
     * Qbao_io 获取账户余额
     * @param accountNo
     * @return
     */
    @ApiOperation(value="Qbao 获取账户余额", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getAccountBalance",method = RequestMethod.GET,produces = "application/json")
    public Map getAccountBalance(@RequestParam("accountNo") String accountNo){
        logger.info("getAccountBalance");
        Account account = accountService.findAccountByAccountNo(accountNo);
        BigDecimal amount = walletService.getUnspentAmount(account.getAddresses());
        Map<String, Object> map = new HashMap<>();
        map.put("result", amount);
        return map;
    }

    /***
     * Qbao_io 获取账户余额
     * @param accountNo
     * @return
     *//*
    @ApiOperation(value="Qbao 获取账户代币余额", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getAccountTokenBalance",method = RequestMethod.GET,produces = "application/json")
    public Map getAccountBalance(@RequestParam("accountNo") String accountNo, @RequestParam("contractAddress") String contractAddress){
        logger.info("getAccountTokenBalance");
        BigDecimal amount = walletService.getTokenAmountByAccountNo(accountNo, contractAddress);
         Map<String, Object> map = new HashMap<>();
        map.put("result", amount);
        return map;
    }*/
    /***
     * Qbao_io 获取账户RongToken
     * @param accountNo
     * @return
     */
    @ApiOperation(value="Qbao 获取账户融云Token", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getRongToken",method = RequestMethod.GET,produces = "application/json")
    public Map getNewRongToken(@RequestParam("accountNo") String accountNo){

        Account account = accountService.findAccountByAccountNo(accountNo);
        String rongToken = accountService.getNewRongToken(account);
        Map<String, Object> map = new HashMap<>();
        map.put("result", rongToken);
        return map;
    }

    /***
     * Qbao_后台 钱包用户查询+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台 钱包用户查询+分页", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAccountsBypage",method = RequestMethod.GET,produces = "application/json")
    public Page<Account> findAccountsBypage( Integer page, Integer size){
        logger.info("findAccountsBypage");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return accountService.findAccountsByPage(page,size);
    }

    /***
     * Qbao_后台 钱包用户分页+模糊查询
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台 钱包用户分页+模糊查询", notes="params:page=当前页(从0开始) size=当前显示数量 钱包用户模糊查询对象(邮箱email／名称accountName／钱包id accountNo/来源 sourcetype（0=IOS 1=安卓 2=Web/激活状态 activateType（0-未激活 1-已激活）  其他均可默认为空/null)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAccounts",method = RequestMethod.GET,produces = "application/json")
    public Page<Account> findAccountsBypage(Integer page, Integer size, String email, String accountName, String accountNo,
                                            String sourceType, String activateType, Integer inviteDailyMin, Integer inviteDailyMax,
                                            BigDecimal maxInviteAmount, BigDecimal minInviteAmount,Integer level,Boolean authority){
        logger.info("findAccounts");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return accountService.findAccountsByPage(page, size, email, accountName, accountNo, sourceType, activateType,null ,
                inviteDailyMin, inviteDailyMax,maxInviteAmount,minInviteAmount,level,authority,true);
    }

    /***
     * Qbao_后台 钱包用户(有地址)分页+模糊查询
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台 钱包用户(有地址)分页+模糊查询", notes="params:page=当前页(从0开始) size=当前显示数量 钱包用户模糊查询对象(accountNo)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAccountsAndAddress",method = RequestMethod.GET,produces = "application/json")
    public Page<Account> findAccountsAndAddress(Integer page, Integer size, String accountNo){
        logger.info("findAccountsAndAddress");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return accountService.findAccountsByPage(page, size, null, null, accountNo, null, null,WalletConstants.QBAO_ADMIN,null,null,null,null,null,null,false);
    }
    /***
     * Qbao_后台 用户信息增量获取
     * @param version
     * @return
     */
    @ApiOperation(value="Qbao_后台 用户信息增量获取", notes="accountNo 用户No, version 上次同期时刻")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = UserInfo.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/syncUserInfo",method = RequestMethod.GET,produces = "application/json")
    public UserInfo syncUserInfo( @RequestParam(required = false) String version){
        logger.info("syncUserInfo");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return accountService.syncUserInfo(accountNo, version);
    }

    /***
     * Qbao 获取该用户可抽奖次数
     * @param
     * @return
     */
    @ApiOperation(value="Qbao 获取该用户抽奖次数", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = UserInfo.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getReceiveNumber",method = RequestMethod.GET,produces = "application/json")
    public Map getReceiveNumber(){
        logger.info("getReceiveNumber");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Account account = accountService.findAccountByAccountNo(accountNo);
        Map<String, Object> map = new HashMap<>();
        map.put("result",account.getReceiveNumber());
        return map;
    }


    /***
     * Qbao 所有邀请排行
     * @return
     */
    @ApiOperation(value="Qbao 所有邀请排行", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = UserInfo.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getInviteRankingList",method = RequestMethod.GET,produces = "application/json")
    public List<Account>  getInviteRankingList(){
        logger.info("getInviteRankingList");
        return accountService.getInviteRankingList();

    }

    /***
     * Qbao 获取并更新分享码
     * @return
     */
    @ApiOperation(value="Qbao 获取并更新分享码", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = UserInfo.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getShareCode",method = RequestMethod.GET,produces = "application/json")
    public Map  getShareCode(){
        logger.info("getShareCode");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        String shareCode = accountService.getShareCode(accountNo);
        Map<String, Object> map = new HashMap<>();
        map.put("result",shareCode);
        return map;

    }

    /***
     * Qbao_后台 报表——用户概况
     * @return
     */
    @ApiOperation(value="Qbao_后台 报表——用户概况", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getAccountsProfile",method = RequestMethod.GET,produces = "application/json")
    public Account  getAccountsProfile(){
        logger.info("getAccountsProfile");
        return accountService.getAccountsProfile();
    }

    @GetMapping(value = "getAccountByAccountNoCache")
    public Account getAccountByAccountNoCache(@RequestParam String accountNo) {
        return accountService.findAccountByAccountNo(accountNo);
    }

    @PostMapping(value = "saveAccountLoginTime")
    public Account saveAccountLoginTime(@RequestBody Account account) {
        String accountNo = account.getAccountNo();
        Date loginTime = account.getLoginTime();
        return accountService.updateLoginTime(accountNo, loginTime);
    }

}
