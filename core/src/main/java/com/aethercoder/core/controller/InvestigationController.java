package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.event.FundUser;
import com.aethercoder.core.entity.event.Investigation;
import com.aethercoder.core.service.FundUserService;
import com.aethercoder.core.service.InvestigationService;
import com.aethercoder.core.service.SendMailService;
import io.rong.models.SMSSendCodeResult;
import io.rong.models.SMSVerifyCodeResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiawei.tao on 2017/10/24.
 */
@RestController
@RequestMapping( "investigation" )
@Api( tags = "investigation", description = "问卷调查接口管理" )
public class InvestigationController {

    private static Logger logger = LoggerFactory.getLogger(InvestigationController.class);

    @Value( "${rongCloud.appKey}" )
    private String appKey;

    @Value( "${rongCloud.appSecret}" )
    private String appSecret;

    @Autowired
    private InvestigationService investigationService;
    @Autowired
    private FundUserService fundUserService;

    @Autowired
    private SendMailService sendMailService;
    /**
     * Qbao-保存ICO问卷调查结果
     *
     * @param investigation
     * @return investigation
     * @throws Throwable
     */
    @ApiOperation( value = "Qbao 保存ICO问卷调查结果", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/save", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
    @Transactional
    public Investigation saveInvestigation(@RequestBody Investigation investigation) {
        logger.info("save");
        Assert.notNull(investigation.getAccountNo(),"accountNo not null");
        investigation = investigationService.save(investigation);

        FundUser fundUser = fundUserService.findFundUserById(Long.parseLong(investigation.getAccountNo()));
        fundUser.setIcoQualification(WalletConstants.ICO_QUALIFICATION_CHECKING);
        fundUserService.updateFundUser(fundUser);
        return investigation;
    }

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @return
     * @author GFY
     */
    @ApiOperation( value = "发送短信验证码", notes = "参数:1.mobile-接收短信验证码的目标手机号（必传） " )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/sendCode", method = RequestMethod.GET, produces = "application/json" )
    @Transactional
    public SMSSendCodeResult sendCode(String mobile) {

        logger.info("sendCode");
        return investigationService.sendCode(mobile, "1");
    }

    /**
     * 验证码验证
     *
     * @param sessionId
     * @param code
     * @return
     * @author GFY
     */
    @ApiOperation( value = "验证码验证", notes = "参数:1.sessionId-短信验证码唯一标识，在发送短信验证码方法，返回值中获取。（必传） 2.code-短信验证码内容。（必传）" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/verifyCode", method = RequestMethod.GET, produces = "application/json" )
    @Transactional
    public SMSVerifyCodeResult verifyCode(String sessionId, String code) {

        logger.info("verifyCode");
        return investigationService.verifyCode(sessionId, code);
    }

    /***
     * Qbao_后台 查询问卷调查分页+模糊查询
     * @param page
     * @param size
     * @return
     */
    @ApiOperation( value = "Qbao_后台 查询问卷调查分页+模糊查询", notes = "params:page=当前页(从0开始) size=当前显示数量 模糊查询(userName-用户名 phone-手机号 email-邮箱 isEmailed-是否发送(0: no\n" +
            "1: did))" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/findInvestigationByPage", method = RequestMethod.GET, produces = "application/json" )
    public Page<Investigation> findInvestigationByPage(Integer page, Integer size, String userName, String phone, String email, Boolean isEmailed, String investigationNo) {
        logger.info("findInvestigationByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return investigationService.findInvestigationByPage(page, size, userName, phone, email, isEmailed, investigationNo);
    }


    /***
     * Qbao_后台 查询ICO问卷调查分页+模糊查询
     * @param page
     * @param size
     * @return
     */
    @ApiOperation( value = "Qbao_后台 查询ICO问卷调查分页+模糊查询", notes = "params:page=当前页(从0开始) size=当前显示数量 模糊查询(userName-用户名 phone-手机号 email-邮箱 isEmailed-是否发送(0: no\n" +
            "status))" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/findInvestigationFotICOByPage", method = RequestMethod.GET, produces = "application/json" )
    public Page<Investigation> findInvestigationFotICOByPage(Integer page, Integer size, String userName, String phone, String email, String status) {
        logger.info("findInvestigationFotICOByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        /*if (null == status) {
            status = WalletConstants.STATUS_NOT_CHECK;
        }*/
        return investigationService.findInvestigationForICOByPage(page, size, userName, phone, email, status);
    }

    /***
     * Qbao_后台 修改是否已发送邮件
     * @return
     */
    @ApiOperation( value = "Qbao_后台 修改是否已发送邮件", notes = "参数 :Investigation对象 " )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "/admin/updateIsEmailed", method = RequestMethod.POST, produces = "application/json", consumes = "application/json" )
    public Investigation updateIsEmailed(@RequestBody Investigation investigation) {
        logger.info("admin/updateIsEmailed");
        Assert.notNull(investigation.getId(), "id not null");
        return investigationService.update(investigation);
    }

    /***
     * Qbao 根据id查询ICO问卷调查
     * @param id
     * @return
     */
    @ApiOperation( value = "Qbao 根据id查询ICO问卷调查", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )

    @RequestMapping( value = "/findInvestigationById", method = RequestMethod.GET, produces = "application/json" )
    public Investigation findInvestigationById(long id) {
        logger.info("findInvestigationById");
        Assert.notNull(id, "id not null");
        return investigationService.findById(id);

    }

    /***
     * Qbao 根据登录的UserId查询ICO问卷调查
     * @param userId
     * @return
     */
    @ApiOperation( value = "Qbao 根据登录的UserId查询ICO问卷调查", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )

    @RequestMapping(value = "/findInvestigationByUserId", method = RequestMethod.GET, produces = "application/json" )
    public Investigation findInvestigationByUserId(String userId) {
        logger.info("findInvestigationByUserId");
        Assert.notNull(userId, "id not null");
        return investigationService.findByUserId(userId);

    }

    /***
     * Qbao_后台 修改问卷的审查结果
     * @return
     */
    @ApiOperation( value = "Qbao_后台 修改问卷的审查结果", notes = "参数 :Investigation对象 " )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "/admin/updateStatus", method = RequestMethod.POST, produces = "application/json", consumes = "application/json" )
    public Investigation updateStatus(@RequestBody Investigation investigation) {
        logger.info("admin/updateStatus");
        Assert.notNull(investigation.getAccountNo(), "User id not null");
        FundUser fundUser = fundUserService.findFundUserById(Long.parseLong(investigation.getAccountNo()));
        if (WalletConstants.STATUS_APPROVED.equals(investigation.getStatus())) {
            fundUser.setIcoQualification(WalletConstants.ICO_QUALIFICATION_APPROVED);
            sendMailService.sendFundUserICOSuccess(fundUser,null);
        } else if (WalletConstants.STATUS_DECLINED.equals(investigation.getStatus())) {
            fundUser.setIcoQualification(WalletConstants.ICO_QUALIFICATION_FAILED);
            sendMailService.sendFundUserICOFail(fundUser,null);
        }
        fundUserService.updateFundUser(fundUser);
        return investigationService.update(investigation);
    }


    /***
     * Qbao_后台 通过问卷的ID列表批量审批通过KYC
     * @param investigationIDList KYC的ID列表
     * @return
     */
    @ApiOperation( value = "Qbao_后台 通过问卷的ID列表批量审批通过KYC", notes = "参数 :Investigation对象 " )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Investigation.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @Transactional
    @RequestMapping( value = "/admin/updateSuccessByList", method = RequestMethod.POST, produces = "application/json", consumes = "application/json" )
    public Map<String, String> updateSuccessByList(@RequestBody List<Long> investigationIDList) {
        logger.info("admin/updateSuccessByList");
        investigationService.updateSuccessByList(investigationIDList);
        Map<String, String> result = new HashMap<>();
        result.put("result", String.valueOf(investigationIDList.size()));
        return result;
    }
}
