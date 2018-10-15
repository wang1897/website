package com.aethercoder.core.controller;

import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Questionnaire;
import com.aethercoder.core.service.QuestionnaireService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/2/28
 * @modified By:
 */
@RestController
@RequestMapping( value = "questionnaire", produces = "application/json")
@Api( tags = "questionnaire", description = "H5 问卷调查" )
public class QuestionnaireController {

    private static Logger logger = LoggerFactory.getLogger(QuestionnaireController.class);

    @Autowired
    private QuestionnaireService questionnaireService;

    @ApiOperation(value = "用户是否参加过问卷调查", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping( value = "/questionnaireInfo", method = RequestMethod.GET)
    public Map questionnaireInfo(){
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");
        logger.info("/questionnaireInfo");
        Boolean result = questionnaireService.questionnaireInfo(accountNo);
        Map map = new HashMap();
        map.put("result",result);
        return map;
    }

    @ApiOperation(value = "提交问卷调查", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping( value = "/submitQuestionnaire", method = RequestMethod.POST)
    public Map submitQuestionnaire(@RequestBody Questionnaire questionnaire){
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(accountNo, "accountNo not null");

        logger.info("/submitQuestionnaire");
        questionnaireService.submitQuestionnaire(questionnaire, accountNo);
        Map map = new HashMap();
        map.put("result","success");
        return map;
    }
}
