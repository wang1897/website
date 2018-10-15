package com.aethercoder.foundation.controller;

import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.batch.BatchDefinition;
import com.aethercoder.foundation.service.BatchService;
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

import java.util.Date;

/**
 * Created by hepengfei on 08/12/2017.
 */
@RestController
@RequestMapping(value = "batch", produces = "application/json")
@Api(tags = "batch", description = "批处理接口管理")
public class BatchController {
    private static Logger logger = LoggerFactory.getLogger(BatchController.class);

    @Autowired
    private BatchService batchService;

    /***
     * Qbao 创建批处理定义
     * @return
     */
    @ApiOperation(value = "Qbao 创建批处理定义", notes = "返回Batch")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = BatchDefinition.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/createDefinition", method = RequestMethod.POST, consumes = "application/json")
    public BatchDefinition createDefinition(@RequestBody BatchDefinition batchDefinition) {
        logger.info("createDefinition");
        Assert.notNull(batchDefinition.getFrequency() , "frequency not null");
        Assert.notNull(batchDefinition.getStartTime() , "startTime not null");
        Assert.notNull(batchDefinition.getClassName() , "className not null");
        Assert.isTrue(batchDefinition.getStartTime().before(batchDefinition.getEndTime()), "start time cannot be later than end time");
        Assert.isTrue(batchDefinition.getEndTime().after(new Date()), "end time must be later than current time");

        return batchService.saveBatchDefinition(batchDefinition);
    }


    @RequestMapping(value = "/findAllDefinition",method = RequestMethod.GET,produces = "application/json")
    public Page<BatchDefinition> findAllDefinition(Integer page, Integer size){
        logger.info("findAllDefinition");
        if(null == page){page = CommonConstants.DEFAULT_PAGE;}
        if(null == size){size = CommonConstants.DEFAULT_PAGE_SIZE;}
        return batchService.findAllDefinition(page,size);

    }


    @RequestMapping(value = "/findDefinitionByid",method = RequestMethod.GET,produces = "application/json")
    public BatchDefinition findDefinitionByid(@RequestParam long id){
        logger.info("findDefinitionByid");
        Assert.notNull(id,"id not null");
        return batchService.findDefinitionById(id);

    }
}
