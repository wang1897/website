package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.batch.RedPacketBatchDefinition;
import com.aethercoder.core.entity.event.Event;
import com.aethercoder.core.entity.event.EventApply;
import com.aethercoder.core.service.EventApplyService;
import com.aethercoder.core.service.EventService;
import com.aethercoder.core.service.VerifyIOSService;
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
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
@RestController
@RequestMapping("event")
@Api(tags = "event", description = "活动接口管理")
public class EventController {

    private static Logger logger = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    @Autowired
    private EventApplyService eventApplyService;

    @Autowired
    private VerifyIOSService verifyIOSService;

    /**
     * Qbao-创建活动
     * @param event
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 创建活动", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/createEvent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Event createEvent(@RequestBody Event event) {

        logger.info("/admin/createEvent");
        return eventService.createEvent(event);
    }

    /**
     * Qbao 更新活动
     *
     * @param event
     * @return event
     */
    @ApiOperation(value = "Qbao 更新活动", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/updateEvent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Event updateEvent(@RequestBody Event event) {
        logger.info("/admin/updateEvent");
        return eventService.updateEvent(event);
    }

    /***
     * Qbao_后台 删除活动
     * @param id
     */
    @ApiOperation(value = "Qbao_后台 删除单个活动", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = String.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteEvent", method = RequestMethod.GET)
    public Map deleteEvent(@RequestParam("id") Long id) {
        logger.info("/admin/deleteEvent");
        Map<String, Object> map = new HashMap<>();
        eventService.deleteEvent(id);
        map.put("result","success");
        return map;
    }

    /***
     * Qbao_后台 获取活动总余额
     * @param id
     */
    @ApiOperation(value = "Qbao 获取活动总余额", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = String.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getSurplus", method = RequestMethod.GET)
    public Map getEventSurplus(@RequestParam("id") Long id) {
        logger.info("getSurplus");
        Map<String, Object> map = new HashMap<>();
        map.put("result", eventService.getEventSurplus(id));
        return map;
    }

    /***
     * Qbao 查询取得活动
     * @return
     */
    @ApiOperation(value="Qbao 取得活动", notes="返回Event")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findEventById",method = RequestMethod.GET,produces = "application/json")
    public Event findEventById(Long id){
        logger.info("findEventById");
        return eventService.findByEventID(id);
    }
    /***
     * Qbao 查询所有活动
     * @return
     */
    @ApiOperation(value="Qbao 查询所有活动", notes="返回List<Event>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findEventAll",method = RequestMethod.GET,produces = "application/json")
    public List<Event> findEventAll(){
        logger.info("findEventAll");
        return eventService.findEventAll();
    }

    /***
     * APP 查询所有至现在有效活动+分页
     * @return
     */
    @ApiOperation(value="APP 查询所有至现在有效活动+分页", notes="返回List<Event>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAvailableEvensByPage",method = RequestMethod.GET,produces = "application/json")
    public Page<Event> findAvailableEvensByPage(@RequestParam Integer page, @RequestParam Integer size){
        logger.info("findAvailableEvensByPage");
        return eventService.findAvailableEvensByPage(page, size);
    }

    /***
     * Qbao 查询所有活动+分页
     * @return
     */
    @ApiOperation(value="Qbao 查询所有活动+分页", notes="返回List<Event>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findEventsByPage",method = RequestMethod.GET,produces = "application/json")
    public Page<Event> findEvensByPage(@RequestParam Integer page, @RequestParam Integer size){
        logger.info("findEventsByPage");
        return eventService.findEvensByPage(page, size);
    }
    /***
     * Qbao 查询所有活动+分页+模糊查询
     * @return
     */
    @ApiOperation(value="Qbao 查询所有活动+分页+模糊查询", notes="参数: 1.page-当前页  2.size-页大小  3.eventName-活动名称   " +
            " 4.originalCurrency-兑换元币种   5.destCurrency=兑换币种   6.eventAvailable-活动是否生效  7.beginDate-活动开始时间  8.endDate-活动结束时间")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findEventsByPageAndCheck",method = RequestMethod.GET,produces = "application/json")
    public Page<Event> findEventsByPageAndCheck( Integer page, Integer size , String eventName, String originalCurrency, String destCurrency, Boolean eventAvailable, String beginDate, String endDate){
        logger.info("findEventsByPageAndCheck");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return eventService.findEvensByPage(page, size, eventName, originalCurrency, destCurrency, eventAvailable, beginDate, endDate);
    }

    /***
     * Qbao 查询当前有效活动详情
     * @return
     */
    @ApiOperation(value="Qbao 查询当前有效活动详情", notes="返回结果集： 1。event 活动详情  2。count-报名总人数  3。expectedIncome-报名时锁定总额  4。actualIncome-实际转账总额")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findEventDetails",method = RequestMethod.GET,produces = "application/json")
    public Map findEventDetails(@RequestParam Long id){
        logger.info("findEventDetails");
        Assert.notNull(id , "id not null");
        Map<String,Object> map = new HashMap<String,Object>();
        //获得该活动详情
        Event event = eventService.findEventByEventID(id);
        map.put("event",event);
        //获得该活动报名人数
        Long count = eventApplyService.countEventAppliesByEventId(id);
        map.put("count",count);
        //获得该活动所有报名用户锁定总额
        BigDecimal expectedIncome = eventApplyService.sumByExpectedIncome(id);
        map.put("expectedIncome",expectedIncome);
        //获得该活动所有报名用户实际转账代币总额
        BigDecimal actualIncome = eventApplyService.sumByActualIncome(id);
        map.put("actualIncome",actualIncome);
        //获得该活动所有报名用户信息
        List<EventApply> eventApplyList = eventApplyService.findEventAppliesByEventId(id);
        map.put("eventApplyList",eventApplyList);
        return map;
    }


    /***
     * Qbao 查询当前有效活动
     * @return
     */
    @ApiOperation(value="Qbao 查询当前有效活动", notes="返回List<Event>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findEventsNow",method = RequestMethod.GET,produces = "application/json")
    public List<Event> findEventsNow(HttpServletRequest request){

        logger.info("findEventsNow");
        if(verifyIOSService.isVerifyForIOS(request)){
            return new ArrayList<Event>();
        }
        return eventService.findEventsNow();
    }

    /**
     * 查询当前有效活动详情分页+模糊查询
     * @param page
     * @param size
     * @param id
     * @param accountNo
     * @return
     */
    @ApiOperation(value="Qbao 查询当前有效活动详情分页+模糊查询", notes="参数: 1.page-当前页  2.size-页大小  3。id-活动ID 4。模糊查询accountNo - (accountNo或者accountName)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findEventAppliesByEventIdByPage",method = RequestMethod.GET,produces = "application/json")
    public Page<EventApply> findEventAppliesByEventIdByPage( Integer page, Integer size , Long id, String accountNo){
        logger.info("findEventAppliesByEventIdByPage");
        Assert.notNull(id,"eventApplyId not null");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return eventApplyService.findEventAppliesByEventIdByPage(page, size, id,accountNo);
    }




    /***
     * Qbao 测试规则表达式
     * @return
     */
    @ApiOperation(value="Qbao 测试规则表达式", notes="返回表达式执行结果")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/parseExpression", method = RequestMethod.GET, produces = "application/json")
    public Map<String, BigDecimal> parseExpression(@RequestParam("expression") String expression, @RequestParam("sourceAmount") BigDecimal sourceAmount) {
        logger.info("parseExpression");
        BigDecimal amount = eventApplyService.parseEventExpression(sourceAmount.doubleValue(), expression);
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("result", amount);
        return result;
    }


    /**
     * Qbao-创建天降红包活动
     * @param event
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 创建天降红包活动", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/createRedPacketEvent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map<String, String> createRedPacketEvent(@RequestBody RedPacketBatchDefinition event) {

        logger.info("/admin/createRedPacketEvent");
        Map<String, String> result = new HashMap<>();
        eventService.createRedPacketEvent(event);
        result.put("result", "success");
        return result;
    }


    /**
     * Qbao-更新天降红包活动
     * @param event
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 更新天降红包活动", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Event.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateRedPacketEvent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map<String, String> updateRedPacketEvent(@RequestBody RedPacketBatchDefinition event) {

        logger.info("/admin/updateRedPacketEvent");
        Map<String, String> result = new HashMap<>();
        eventService.updateRedPacketEvent(event);
        result.put("result", "success");
        return result;
    }

    /**
     * 获取服务器时间
     * @return
     */
    @ApiOperation(value="获取服务器时间", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = String.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getSystemDate",method = RequestMethod.GET,produces = "application/json")
    public Map<String, String> getSystemDate(){

        logger.info("getSystemDate");
        Map<String, String> result = new HashMap<>();
        TimeZone destTimeZone = TimeZone.getTimeZone("GMT+8");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(destTimeZone);
        result.put("result", sdf.format(new Date()));
        return result;
    }
}
