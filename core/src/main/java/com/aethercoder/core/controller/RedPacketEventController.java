package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.event.RedPacketEvent;
import com.aethercoder.core.service.RedPacketEventService;
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

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2018/1/4 下午3:52
 */
@RestController
@RequestMapping( "redPacketEvent" )
@Api( tags = "redPacketEvent", description = "天降红包管理" )
public class RedPacketEventController {

    private static Logger logger = LoggerFactory.getLogger(RedPacketEventController.class);

    @Autowired
    private RedPacketEventService redPacketEventService;

    /**
     * 创建红包
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/admin/createRedPacketEvent", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "创建红包", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = RedPacketEvent.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    public RedPacketEvent createRedPacketEvent(@RequestBody RedPacketEvent redPacketEvent) {
        logger.info("/admin/createRedPacketEvent");
        return redPacketEventService.saveRedPacketEvent(redPacketEvent);
    }

    /**
     * Qbao 获取天降红包详情
     *
     * @return group
     */
    @ApiOperation(value = "Qbao 获取天降红包详情", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = RedPacketEvent.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @org.springframework.transaction.annotation.Transactional
    @RequestMapping(value = "/getRedPacketEventInfo", method = RequestMethod.GET, produces = "application/json")
    public Map getRedPacketEventInfo(@RequestParam Long id) {
        logger.info("getRedPacketEventInfo");
        Map<String, Object> map = new HashMap<>();
        RedPacketEvent redPacketEvent = redPacketEventService.getRedPacketEventInfo(id);
        map.put("result",redPacketEvent);
        return map;
    }

    /***
     * Qbao_后台 终止天降红包
     * @return
     */
    @ApiOperation(value = "Qbao_后台 终止天降红包", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = RedPacketEvent.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateRedPacketEvent", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Map updateRedPacketEvent(@RequestBody RedPacketEvent redPacketEvent) {
        logger.info("/admin/updateRedPacketEvent");
        Assert.notNull(redPacketEvent,  "redPacket not null");
        Assert.notNull(redPacketEvent.getId(), "id not null");
        redPacketEventService.updateRedPacketEvent(redPacketEvent);
        Map<String, Object> map = new HashMap<>();
        map.put("result","success");
        return map;

    }


    /***
     * Qbao 查询所有天降红包
     * @return
     */
    @ApiOperation(value="Qbao 查询所有天降红包", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = RedPacketEvent.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAllDefinition",method = RequestMethod.GET,produces = "application/json")
    public Page<RedPacketEvent> findAllDefinition(Integer page, Integer size, String groupNo, Long unit, String beginDate, String endDate, Boolean isDeleted){
        logger.info("findAllDefinition");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return redPacketEventService.findAllRedPacketEvent(page,size,groupNo,unit,beginDate,endDate,isDeleted);

    }

}
