package com.aethercoder.core.controller;


import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.admin.AdminAccount;
import com.aethercoder.core.security.JwtTokenUtil;
import com.aethercoder.core.service.AdminAcountService;
import com.aethercoder.foundation.contants.CommonConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("adminAccount")
@Api(tags = "adminAccount", description = "管理员接口管理")
public class AdminAccountController {

    private static Logger logger = LoggerFactory.getLogger(AdminAccountController.class);


    @Autowired
    private AdminAcountService adminAcountService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String tokenHeader = CommonConstants.HEADER_WEB_TOKEN_KEY;

    /***
     * Qbao_后台 管理员登录
     * 1。判断是邮箱／accountNo登陆
     * 2。判断该用户是否存在
     * 3。判断该用户是否激活
     * @param user
     * @return
     */
    @ApiOperation(value="Qbao_后台 管理员登录", notes="用户名不存在 or 密码有误：返回message=用户名或密码输入有误 ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/login",method = RequestMethod.POST,produces = "application/json")
    public AdminAccount loginAdminAccount(@RequestBody Map user, HttpServletResponse response) {
        logger.info("login");
        Assert.notNull(user.get("name"),"name not null");
        Assert.notNull(user.get("password"),"password not null");
        logger.debug("login ", user.get("name").toString() + "," + user.get("password").toString());
        AdminAccount adminAccount = adminAcountService.loginAdminAccount(user.get("name").toString(),user.get("password").toString());
       //result==null表示登陆用户存在且密码输入正确——login success ，返回token
        response.setHeader(tokenHeader, jwtTokenUtil.generateToken(adminAccount.getName(), WalletConstants.JWT_EXPIRATION));
        return adminAccount;

    }

    /***
     * Qbao_后台 根据token获得用户信息
     * @param token
     * @return
     */
    @ApiOperation(value="Qbao_后台 根据token获得用户信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/checkAdminAccountByToken",method = RequestMethod.GET,produces = "application/json")
    public AdminAccount checkAdminAccountByToken(@RequestParam String token){
        logger.info("/admin/checkAdminAccountByToken");
        Assert.notNull(token, "token not null");
        return adminAcountService.getAdminAccountByToken(token);
    }

    /***
     * Qbao_后台 根据ID查询管理员信息
     * @param id
     * @return
     */
    @ApiOperation(value="Qbao_后台 根据ID查询管理员信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAdminAccountById",method = RequestMethod.GET,produces = "application/json")
    public AdminAccount findAdminAccountById(@RequestParam long id){
        logger.info("findAdminAccountById");
        Assert.notNull(id, "id not null");
        return adminAcountService.findById(id);
    }

    /***
     * Qbao_后台 添加后台管理员
     * @param adminAccount
     * @return
     */
    @ApiOperation(value="Qbao_后台 添加后台管理员", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveAdminAccount",method = RequestMethod.POST,consumes = "application/json",produces = "application/json")
    @Transactional
    public AdminAccount saveAdminAccount(@RequestBody AdminAccount adminAccount){
        logger.info("/admin/saveAdminAccount");
        return adminAcountService.saveAdminAccount(adminAccount);

    }

    /***
     * Qbao_后台 查询所有管理员用户
     * @return
     */
    @ApiOperation(value="Qbao_后台 查询所有管理员用户", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAllAdminAccount",method = RequestMethod.GET,produces = "application/json")
    public List<AdminAccount> findAllAdminAccount(){
        logger.info("findAllAdminAccount");
        return adminAcountService.findAllAdminAccount();
    }

    /***
     * Qbao 修改管理员信息
     * @return
     */
    @ApiOperation(value="Qbao 修改管理员信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateAdminAccount",method = RequestMethod.GET,produces = "application/json")
    public AdminAccount updateAdminAccount(AdminAccount adminAccount){
        logger.info("/admin/updateAdminAccount");
        Assert.notNull(adminAccount.getId(),"id not null");
        return adminAcountService.updateAdminAccount(adminAccount);
    }

    /***
     * Qbao_后台 删除管理员
     * @return
     */
    @ApiOperation(value = "Qbao_后台 删除管理员", notes = "参数：id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteAdminAccount", method = RequestMethod.GET, produces = "application/json")
    public Map deleteAdminAccount(@RequestParam Long id) {
        logger.info("/admin/deleteAdminAccount");
        Assert.notNull(id, "id not null");
        adminAcountService.deleteAdminAccount(id);
        Map<String, Object> map = new HashMap<>();
        map.put("result","success");
        return map;
    }


    /***
     * Qbao_后台 检验密码
     * @return
     */
    @ApiOperation(value = "Qbao_后台  检验密码", notes = "参数：id/用户输入密码")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/checkAdminAccountPassword", method = RequestMethod.GET, produces = "application/json")
    public Map checkAdminAccountPassword(@RequestParam Long id ,@RequestParam String password) {
        logger.info("/admin/checkAdminAccountPassword");
        Assert.notNull(id, "id not null");
        Assert.notNull(password, "password not null");

        Map<String, Object> map = new HashMap<>();
        adminAcountService.checkPasswordIsTrue(password, id);
        map.put("result","success");
        return map;
    }

    @GetMapping("/admin/getAdminByName")
    public AdminAccount findAdminByName(@RequestParam String name) {
        return adminAcountService.findByName(name);
    }

}
