package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.admin.AdminAccount;
import com.aethercoder.core.entity.pay.Order;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.CountryInformation;
import com.aethercoder.core.entity.wallet.Customer;
import com.aethercoder.core.security.JwtTokenUtil;
import com.aethercoder.core.service.ContractService;
import com.aethercoder.core.service.CountryInformationService;
import com.aethercoder.core.service.CustomerService;
import com.aethercoder.core.service.OrderService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/8
 * @modified By:
 */
@RestController
@RequestMapping("customer")
@Api(tags = "customer", description = "商户管理接口")
public class CustomerController {

    private static Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CountryInformationService countryInformationService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private ContractService contractService;

    private String tokenHeader = CommonConstants.HEADER_WEB_TOKEN_KEY;

    /***
     * Qbao 后台 商户界面显示
     * @return
     */
    @ApiOperation(value = "Qbao 后台 商户界面显示", notes = "返回List<Customer>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Page.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getCustomerList", method = RequestMethod.GET, produces = "application/json")
    public Page<Customer> getCustomerList(@RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                String customerNo,String customerName,Integer status,@RequestParam String language){
        logger.info("/admin/getCustomerList");

        return customerService.getCustomerList(page,size,customerNo,customerName,status,language);
    }

    /***
     * Qbao APP 商户界面显示
     * @return
     */
    @ApiOperation(value = "Qbao APP 商户界面显示", notes = "返回List<Customer>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Customer.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getCustomerListForAPP", method = RequestMethod.GET, produces = "application/json")
    public Page<Customer> getCustomerListForAPP(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                String customerNo,String customerName){
        logger.info("/getCustomerListForAPP");

        String language = LocaleContextHolder.getLocaleContext().getLocale().getLanguage();

        return customerService.getCustomerList(page,size,customerNo,customerName,WalletConstants.CUSTOMER_STATUS_ACTIVE,language);
    }

    /***
     * Qbao 后台 添加商户
     * @return
     */
    @ApiOperation(value = "Qbao 后台 添加商户", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @Transactional
    @RequestMapping(value = "/admin/addCustomer", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map addCustomer(@RequestBody Customer customer){
        logger.info("/admin/addCustomer");
        Assert.notNull(customer,"customer not null");
        Assert.notNull(customer.getAddress(),"customer.getAddress() not null");
        Assert.notNull(customer.getCustomerName(),"customer.getCustomerName() not null");
        Assert.notNull(customer.getLogo(),"customer.getLogo() not null");

        customerService.saveCustomer(customer);

        Map map = new HashMap();
        map.put("result","success");
        return map;
    }

    /***
     * Qbao 后台 修改商户
     * @return
     */
    @ApiOperation(value = "Qbao 后台 修改商户", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/updateCustomer", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map updateCustomer(@RequestBody Customer customer){
        logger.info("/admin/updateCustomer");
        Assert.notNull(customer,"customer not null");
        Assert.notNull(customer.getCustomerNo(),"CustomerNo not null");

        customerService.updateCustomer(customer);
        Map map = new HashMap();
        map.put("result","success");
        return map;
    }

    /***
     * Qbao_后台 根据token获得商户信息
     * @param token
     * @return
     */
    @ApiOperation(value="Qbao_后台 根据token获得商户信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/checkAdminCustomerByToken",method = RequestMethod.GET,produces = "application/json")
    public Map checkAdminCustomerByToken(@RequestParam String token){
        logger.info("/admin/checkAdminCustomerByToken");

        Assert.notNull(token, "token not null");

        String customerNo = jwtTokenUtil.getUsernameFromToken(token);

        Map map = new HashMap();
        map.put("result",customerNo);
        return map;
    }

    /***
     * Qbao 后台 注销商户
     * @return
     */
    @ApiOperation(value = "Qbao 后台 注销商户", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/cancelCustomer", method = RequestMethod.POST, produces = "application/json")
    public Map cancelCustomer(@RequestBody Customer customer){
        logger.info("/admin/cancelCustomer");
        Assert.notNull(customer.getCustomerNo(),"customerNo not null");
        customerService.cancelCustomer(customer.getCustomerNo());

        Map map = new HashMap();
        map.put("result","success");
        return map;
    }

    /***
     * Qbao 后台 获取商户所在国家及区号
     * @return
     */
    @ApiOperation(value = "Qbao 后台 获取商户所在国家及区号", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = List.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getCountryInfo", method = RequestMethod.GET, produces = "application/json")
    public List<CountryInformation> getCountryInfo(){
        logger.info("/admin/getCountryInfo");

        return countryInformationService.getCountryInfo();
    }

    /***
     * Qbao 后台 财务结算
     * @return
     */
    @ApiOperation(value = "Qbao 后台 财务结算", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = List.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findAllOrdersByPage", method = RequestMethod.GET, produces = "application/json")
    public Page<Customer> findAllOrdersByPage(Integer page, Integer size, String customerNo, String customerName, String cleanTime){
        logger.info("/admin/findAllOrdersByPage");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        Assert.notNull(cleanTime,"cleanTime not null");
        return orderService.findAllOrdersByPage(page, size, customerNo, customerName,DateUtil.stringToDateFormat(cleanTime));
    }

    /***
     * Qbao 后台 财务结算下载data
     * @return
     */
    @ApiOperation(value = "Qbao 后台 财务结算下载data", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = List.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getOrdersDay", method = RequestMethod.GET, produces = "application/json")
    public Map findAllOrdersByPage(@RequestParam String customerId,@RequestParam String cleanTime){
        logger.info("/admin/getOrdersDay");
        return orderService.getOrdersDay(customerId,cleanTime);
    }

    /***
     * Qbao_商户平台 商户登录
     * @param user
     * @return
     */
    @ApiOperation(value="Qbao_商户平台 商户登录", notes="用户名不存在 or 密码有误：返回message=用户名或密码输入有误 ")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/platform/loginCustomer",method = RequestMethod.POST,produces = "application/json")
    public Customer loginCustomer(@RequestBody Map user, HttpServletResponse response) {
        logger.info("login");
        Assert.notNull(user.get("customerNo"),"customerNo not null");
        Assert.notNull(user.get("password"),"password not null");
        logger.debug("login ", user.get("customerNo").toString() + "," + user.get("password").toString());
        Customer customer = customerService.loginCustomer(user.get("customerNo").toString(),user.get("password").toString());
        //result==null表示登陆用户存在且密码输入正确——login success ，返回token
        response.setHeader(tokenHeader, jwtTokenUtil.generateToken(customer.getCustomerNo(), WalletConstants.JWT_EXPIRATION));
        return customer;

    }

    /***
     * Qbao_商户平台 订单查询
     * @return
     */
    @ApiOperation(value="Qbao_商户平台 订单查询", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/platform/findOrdersByCustomer",method = RequestMethod.GET,produces = "application/json")
    public Map findOrdersByCustomer(Integer page, Integer size, String customerId, String cleanTime, String beginDate, String endDate, Integer status, String orderId) {
        logger.info("findOrdersByCustomer");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        if(StringUtils.isNotBlank(cleanTime)){
            beginDate = "";
            endDate = "";
        }
        Map map = orderService.findOrdersByCustomer(page, size, customerId, cleanTime, beginDate, endDate, status, orderId);
        return map;

    }
    /***
     * Qbao_商户平台 代币
     * @return
     */
    @ApiOperation(value="Qbao_商户平台 代币", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = AdminAccount.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/platform/findContractsAllByAdmin", method = RequestMethod.GET, produces = "application/json")
    public List<Contract> findContractsAllByAdmin() {
        logger.info("findContractsAll");
        return contractService.findContractsAll();
    }

    /***
     * Qbao 获取商户二维码Url
     * @return
     */
    @ApiOperation(value = "Qbao 获取商户二维码Url", notes = "返回Map")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Page.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getCustomerQrCodeUrl", method = RequestMethod.GET, produces = "application/json")
    public String getCustomerQrCodeUrl(String customerNo){
        logger.info("/getCustomerQrCodeUrl");

        return customerService.queryCustomerQrCode(customerNo);
    }

    /***
     * Qbao 后台 获取订单表
     * @return
     */
    @ApiOperation(value = "Qbao 后台 获取订单表", notes = "返回Page<Order>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Page.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getOrderListByPage", method = RequestMethod.GET, produces = "application/json")
    public Page<Order> getOrderListByPage(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         String startTime, String endTime, String accountNo, String customerId, String unit,
                                         String status, String orderId, String accountName, String type){
        logger.info("/admin/getOrderListByPage");

        return orderService.getOrderListByPage(page,size,startTime,endTime,accountNo,accountName,customerId,unit,status,
                orderId,type);
    }

    /***
     * Qbao 商户 强制更新密码
     * @return
     */
    @ApiOperation(value = "Qbao 商户 强制更新密码update", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/platform/updateCustomerPwd", method = RequestMethod.POST, produces = "application/json")
    public Customer updateCustomerPwd(@RequestBody Customer customer){
        logger.info("/platform/updateCustomerPwd");
        Assert.notNull(customer.getCustomerNo(),"customerNo not null");
        Assert.notNull(customer.getPassword(),"password not null");
       return customerService.updateCustomerPwd(customer);
    }

    /***
     * Qbao 商户 忘记密码
     * @return
     */
    @ApiOperation(value = "Qbao 商户 忘记密码reset", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/platform/resetCustomerPwd", method = RequestMethod.POST, produces = "application/json")
    public Customer resetCustomerPwd(@RequestBody Customer customer){
        logger.info("/platform/resetCustomerPwd");
        Assert.notNull(customer.getCustomerNo(),"customerNo not null");
        Assert.notNull(customer.getPhoneNumber(),"phoneNumber not null");
        Assert.notNull(customer.getCode(),"code not null");
        return customerService.resetCustomerPwd(customer);
    }

    /***
     * Qbao 商户login 忘记密码-获取验证码
     * @return
     */
    @ApiOperation(value = "Qbao 商户login 忘记密码-获取验证码", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/platform/getCodeSms", method = RequestMethod.POST, produces = "application/json")
    public Map getCodeSms(@RequestBody Customer customer){
        logger.info("/platform/getCodeSms");
        Assert.notNull(customer.getCustomerNo(),"customerNo not null");
        Assert.notNull(customer.getPhoneNumber(),"phoneNumber not null");
        return customerService.getCodeSms(customer);
    }

    /***
     * Qbao 商户 获取商户所在国家及区号
     * @return
     */
    @ApiOperation(value = "Qbao 商户 获取商户所在国家及区号", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = List.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/platform/getCountryInfoForCustomer", method = RequestMethod.GET, produces = "application/json")
    public List<CountryInformation> getCountryInfoForCustomer(){
        logger.info("/platform/getCountryInfoForCustomer");
        return countryInformationService.getCountryInfo();
    }
}
