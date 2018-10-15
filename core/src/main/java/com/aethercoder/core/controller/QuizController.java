package com.aethercoder.core.controller;


import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.quiz.Quiz;
import com.aethercoder.core.entity.quiz.QuizAnswer;
import com.aethercoder.core.entity.quiz.QuizType;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.QuizService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by guofeiyan on 2018/02/07.
 */
@RestController
@RequestMapping("quiz")
@Api(tags = "quiz", description = "每日答题接口管理")
public class QuizController {

    private static Logger logger = LoggerFactory.getLogger(QuizController.class);

    @Autowired
    private QuizService quizService;

    /***
     * Qbao 后台 题库类型添加
     * @return
     */
    @ApiOperation(value="Qbao 后台 题库类型添加", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = QuizType.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveQuizType",method = RequestMethod.POST,produces = "application/json")
    public List<QuizType> saveQuizType(@RequestBody List<QuizType> quizTypes){
        logger.info("/admin/saveQuizType");
        Assert.notNull(quizTypes.size()==0,"name not null");
        return quizService.saveQuizType(quizTypes);
    }

    /***
     * Qbao 后台 题库类型修改
     * @return
     */
    @ApiOperation(value="Qbao 后台 题库类型修改", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = QuizType.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateQuizType",method = RequestMethod.POST,produces = "application/json")
    public void updateQuizType(@RequestBody QuizType quizType){
        logger.info("/admin/updateQuizType");
        Assert.notNull(quizType.getId(),"id not null");
        quizService.updateQuizType(quizType);

    }

    /***
     * Qbao 后台 删除题类型
     * @return
     */
    @ApiOperation(value="Qbao 后台 删除题类型", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = QuizType.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteQuizType",method = RequestMethod.GET,produces = "application/json")
    public Map deleteQuizType(Long[] ids){
        logger.info("/admin/deleteQuizType");
        Assert.notNull(ids.length==0,"id not null");
        quizService.deleteQuizType(ids);
        Map map = new HashMap();
        map.put("result","success");
        return map;
    }


    /***
     * Qbao_后台 查询所有问题类型
     * @return
     */
    @ApiOperation(value="Qbao_后台 查询所有问题类型", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = QuizType.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAllQuizTypes",method = RequestMethod.GET,produces = "application/json")
    public List<QuizType> findAllQuizTypes(){
        logger.info("findAllQuizTypes");
        return quizService.findAllQuizTypes();
    }

    /***
     * Qbao 后台 题库添加
     * @return
     */
    @ApiOperation(value="Qbao 后台 题库添加", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Quiz.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveQuiz",method = RequestMethod.POST,produces = "application/json")
    public Quiz saveQuiz(@RequestBody Quiz quiz){
        logger.info("/admin/saveQuiz");
        Assert.notNull(quiz.getQuestion(),"question not null");
        Assert.notNull(quiz.getAnswer(),"answer not null");
        Assert.notNull(quiz.getOption1(),"option1 not null");
        Assert.notNull(quiz.getOption2(),"option2 not null");
        return quizService.saveQuiz(quiz);
    }

    /***
     * Qbao 后台 题库修改
     * @return
     */
    @ApiOperation(value="Qbao 后台 题库修改", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Quiz.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateQuiz",method = RequestMethod.POST,produces = "application/json")
    public Quiz  updateQuiz(@RequestBody Quiz quiz){
        logger.info("/admin/updateQuiz");
        Assert.notNull(quiz.getId(),"id not null");
        return quizService.saveQuiz(quiz);

    }

    /***
     * Qbao 后台 删除题
     * @return
     */
    @ApiOperation(value="Qbao 后台 删除题", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Quiz.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteQuiz",method = RequestMethod.GET,produces = "application/json")
    public Map deleteQuiz(Long id){
        logger.info("/admin/deleteQuiz");
        Assert.notNull(id,"id not null");
         quizService.deleteQuiz(id);
         Map map = new HashMap();
         map.put("result","success");
         return map;
    }

    /***
     * Qbao_后台 查询题库
     * @return
     */
    @ApiOperation(value="Qbao_后台 查询题库", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Quiz.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findQuizzes",method = RequestMethod.GET,produces = "application/json")
    public Page<Quiz> findQuizzes(Integer page , Integer size , Long type, String language){
        logger.info("findQuizzes");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return quizService.findQuizzes(page, size, type, language);
    }


    /***
     * Qbao 开始答题
     * @return
     */
    @ApiOperation(value="Qbao_后台 开始答题", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Quiz.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getDailyAnswerQuiz",method = RequestMethod.GET,produces = "application/json")
    public Map getDailyAnswerQuiz(){
        logger.info("getDailyAnswerQuiz");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return quizService.getAccountQuiz(accountNo);
    }

    /***
     * Qbao 提交答题
     * @return
     */
    @ApiOperation(value="Qbao 提交答题", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = QuizAnswer.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/DailyAnswer",method = RequestMethod.POST,produces = "application/json", consumes = "application/json")
    public Map DailyAnswer(@RequestBody List<QuizAnswer> quizAnswers){
        logger.info("DailyAnswer");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return quizService.DailyAnswer(quizAnswers,accountNo);
    }



    /***
     * Qbao 后台 获取题库配置
     * @return
     */
    @ApiOperation(value="Qbao 后台 题库修改", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Quiz.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getQuizInfo",method = RequestMethod.GET,produces = "application/json")
    public List<SysConfig>  getQuizInfo(){
        logger.info("/admin/getQuizInfo");
        return quizService.getQuizInfo();
    }

    /***
     * Qbao 后台 题库配置更新
     * @return
     */
    @ApiOperation(value="Qbao 后台 题库配置更新", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Quiz.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateQuizInfo",method = RequestMethod.POST,produces = "application/json")
    public List<SysConfig>  updateQuizInfo(@RequestBody List<SysConfig> sysConfigs){
        logger.info("/admin/updateQuizInfo");
        return quizService.updateQuizInfo(sysConfigs);
    }

    /**
     * Qbao 每日答题分享
     *
     * @return
     * @throws Throwable
     */

    @ApiOperation(value = "每日答题分享", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getQuizShareUrl", method = RequestMethod.GET, produces = "application/json")
    public Map getQuizShareUrl() {
        logger.info("getQuizShareUrl");
        String language = LocaleContextHolder.getLocale().getLanguage();
        String url = quizService.getQuizShareUrl(language);
        Map map = new HashMap<>();
        map.put("result", "success");
        map.put("url", url);
        return map;
    }

}
