package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.FundUserDao;
import com.aethercoder.core.entity.event.FundUser;
import com.aethercoder.core.entity.wallet.RecordPassword;
import com.aethercoder.core.security.JwtTokenUtil;
import com.aethercoder.core.service.FundUserService;
import com.aethercoder.core.service.RecordPasswordService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.util.UrlUtil;
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

import javax.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping( "user" )
@Api( tags = "user", description = "QBAO.FUND的用户登录接口" )
public class FundUserController {

    private static Logger logger = LoggerFactory.getLogger(FundUserController.class);

    @Autowired
    private FundUserService fundUserService;

    @Autowired
    private RecordPasswordService recordPasswordService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String tokenHeader = CommonConstants.HEADER_WEB_TOKEN_KEY;

    @Autowired
    private FundUserDao fundUserDao;

    /**
     * Qbao-修改钱包信息
     *
     * @return
     * @throws Throwable
     */
    @ApiOperation( value = "Qbao 修改钱包信息", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/updateFundUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
    @Transactional
    public FundUser updateFundUser(@RequestBody FundUser fundUser) {
        logger.info("updateFundUser");
        return fundUserService.updateFundUser(fundUser);
    }


    /***
     * Qbao_io 用户注册
     * @param fundUser
     * @return
     */
    @ApiOperation( value = "Qbao_io 用户注册", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/saveUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
    @Transactional
    public FundUser saveUser(@RequestBody FundUser fundUser) {
        logger.info("saveUser");
        Assert.notNull(fundUser.getEmail(), "email not  null");
        Assert.notNull(fundUser.getUserName(), "fundUserName not null");
        Assert.notNull(fundUser.getPassword(), "password not null");
        logger.debug("saveUser", fundUser);
        FundUser u = fundUserService.saveUser(fundUser);
        return u;

    }


    /***
     * Qbao_io 钱包用户注册_根据no查找fundUser用户信息
     * @param fundUserName
     * @return
     */
    @ApiOperation( value = "Qbao_io 校验根据fundUserNo查找钱包用户信息", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )

    @RequestMapping( value = "/findByFundUserName", method = RequestMethod.GET, produces = "application/json" )
    public FundUser findByFundUserName(@RequestParam( "fundUserName" ) String fundUserName) {
        logger.info("findByFundUserName");
        Assert.notNull(fundUserName, "fundUserNo not null");
        logger.debug("findFundUserName", fundUserName);
        return fundUserService.findFundUserByUserName(fundUserName);

    }

    /***
     * Qbao_io 校验根据fundUserNo查找钱包用户信息
     * @param fundUserId
     * @return
     */
    @ApiOperation( value = "Qbao_io 校验根据fundUserNo查找钱包用户信息", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )

    @RequestMapping( value = "/findByFundUserId", method = RequestMethod.GET, produces = "application/json" )
    public FundUser findByFundUserId(@RequestParam( "fundUserId" ) Long fundUserId) {
        logger.info("findByFundUserId");
        Assert.notNull(fundUserId, "fundUserNo not null");
        logger.debug("fundUserId", fundUserId);
        return fundUserService.findFundUserById(fundUserId);

    }

    /***
     * Qbao_io 用户验证激活
     * @param map
     * @return
     */
    @ApiOperation( value = "Qbao_fund 用户验证激活", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "/activateUser", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
    public Map activateUser(@RequestBody Map<String, String> map) {
        logger.info("activateUser");
        String id = map.get("id");
        Assert.notNull(id, "id not null");
        logger.debug("activateUser ", id);

        byte[] b = UrlUtil.decodeBufferBase64(id);
        String uniqueId = new String(b);
        FundUser fundUser1 = fundUserService.activateUser(uniqueId);
        if (fundUser1 != null) {
            map.put("message", "激活成功");
        }
        return map;
    }


    /***
     * Qbao_io 检验邮箱是否存在
     * @param email
     * @return
     */
    @ApiOperation( value = "Qbao_io 检验邮箱是否存在", notes = "返回boolean(message=true :表示该邮箱可用／message=false :表示该邮箱不可用)" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/checkIsEmail", method = RequestMethod.GET, produces = "application/json" )
    public Map<String, Object> checkIsEmail(@RequestParam( "email" ) String email) {
        logger.info("checkIsEmail");
        logger.debug("checkIsEmail ", email);
        Map<String, Object> map = new HashMap<>();
        if (fundUserService.findByEmail(email) == null) {
            map.put("result", true);
        } else {
            map.put("result", false);
        }

        return map;
    }

    /***
     * Qbao_io 用户登录
     * 1。判断是邮箱／fundUserNo登陆
     * 2。判断该用户是否存在
     * 3。判断该用户是否激活
     * @param user
     * @return
     */
    @ApiOperation( value = "Qbao_io 用户登录", notes = "邮箱不存在 or fundUserName不存在 or 密码有误：返回message=用户名或密码输入有误 ／ 用户未激活：返回message=抱歉！该用户没有激活！" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json" )
    public FundUser login(@RequestBody Map user, HttpServletResponse response) {
        logger.info("login");
        logger.debug("login ", user.get("email").toString() + "," + user.get("password").toString());
        FundUser fundUser = fundUserService.login(user.get("email").toString(), user.get("password").toString());
        response.setHeader(tokenHeader, jwtTokenUtil.generateToken(fundUser.getEmail(), WalletConstants.JWT_EXPIRATION));
        return fundUser;
    }


    /***
     * Qbao_io 用户重置密码_发送邮箱
     * @param email
     * @return
     */
    @ApiOperation( value = "Qbao_io 用户重置密码_发送邮箱", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/resetPwdEmail", method = RequestMethod.GET, produces = "application/json" )
    @Transactional
    public FundUser resetPwdEmail(@RequestParam( "email" ) String email) {
        logger.info("resetPwdEmail");
        Assert.notNull(email, "email not null");
        logger.debug("resetPwdEmail ", email);
        FundUser fundUser = fundUserService.passwordResetEamil(email);
        return fundUser;
    }

    /***
     * Qbao_io 用户重置密码
     * @param password
     * @return
     */
    @ApiOperation( value = "Qbao_fund 用户重置密码", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/resetPwd", method = RequestMethod.GET, produces = "application/json" )
    @Transactional
    public FundUser resetPwd(@RequestParam("id") String id, @RequestParam("password") String password, @RequestParam("code") String code) {
        logger.info("resetPwd");
        Assert.notNull(id, "id not null");
        Assert.notNull(password, "password not null");
        Assert.notNull(code, "code not null");
        logger.debug("resetPwd: ", id + "," + password + "," + code);

        byte[] b = UrlUtil.decodeBufferBase64(id);

        String uniqueId = new String(b);
        //String t = new String(b);

        return fundUserService.passwordReset(uniqueId, password, code);
    }

    /***
     * Qbao_io 用户重置密码地址是否失效
     * @param code
     * @return
     */
    @ApiOperation( value = "Qbao_io 用户重置密码地址是否失效", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = RecordPassword.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/resetPwdUrl", method = RequestMethod.GET, produces = "application/json" )
    @Transactional
    public RecordPassword resetPwd(@RequestParam( "code" ) String code) {
        logger.info("resetPwdUrl");
        Assert.notNull(code, "code not null");
        logger.debug("resetPwdUrl ", code);

        return recordPasswordService.checkUrl(code);
    }

    /***
     * Qbao_后台 钱包用户查询+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation( value = "Qbao_后台 钱包用户查询+分页", notes = "params:page=当前页(从0开始) size=当前显示数量" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/findFundUsersBypage", method = RequestMethod.GET, produces = "application/json" )
    public Page<FundUser> findFundUsersBypage( Integer page,  Integer size) {
        logger.info("findFundUsersBypage");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return fundUserService.findFundUsersByPage(page, size);
    }

    /***
     * Qbao_后台 钱包用户分页+模糊查询
     * @param page
     * @param size
     * @return
     */
    @ApiOperation( value = "Qbao_后台 钱包用户分页+模糊查询", notes = "params:page=当前页(从0开始) size=当前显示数量 钱包用户模糊查询对象(邮箱email／名称fundUserName／钱包id fundUserNo/来源 sourcetype（0=IOS 1=安卓 2=Web/激活状态 activateType（0-未激活 1-已激活）  其他均可默认为空/null)" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = FundUser.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/findFundUsers", method = RequestMethod.GET, produces = "application/json" )
    public Page<FundUser> findFundUsersBypage(Integer page, Integer size, String email, String fundUserName, String activateType, String icoType) {
        logger.info("findFundUsers");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return fundUserService.findFundUsersByPage(page, size, email, fundUserName, activateType, icoType);
    }

    /***
     * Qbao_后台 根据token获得Qbao_fund用户信息
     * @param token
     * @return
     */
    @ApiOperation(value="Qbao_后台 根据token获得Qbao_fund用户信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = FundUser.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/fund/checkFundUserByToken",method = RequestMethod.GET,produces = "application/json")
    public FundUser checkFundUserByToken(@RequestParam String token){
        logger.info("fund/checkFundUserByToken");
        Assert.notNull(token, "token not null");
        return fundUserService.getFundUserByToken(token);
    }


}
