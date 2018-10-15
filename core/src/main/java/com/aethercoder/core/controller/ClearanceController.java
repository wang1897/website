package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.event.GetRedPacket;
import com.aethercoder.core.entity.event.SendRedPacket;
import com.aethercoder.core.entity.wallet.Clearance;
import com.aethercoder.core.entity.wallet.ClearanceDetail;
import com.aethercoder.core.service.ClearanceService;
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

import java.util.List;

/**
 * @auther Jiawei.tao
 * @date 2017/1/16 下午2:40
 */
@RestController
@RequestMapping( "clearance" )
@Api( tags = "clearance", description = "清关算" )
public class ClearanceController {

    private static Logger logger = LoggerFactory.getLogger(ClearanceController.class);

    @Autowired
    public ClearanceService clearanceService;

    /***
     * Qbao_后台 查询清关算+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台 查询清关算+分页", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SendRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findClearanceByPage",method = RequestMethod.GET,produces = "application/json")
    public Page<Clearance> findClearanceByPage(Integer page, Integer size, String clearanceDay, Integer type, Long unit, Integer accountStatus, Boolean isClear){
        logger.info("findClearanceByPage");

        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return clearanceService.findClearanceByPage(page, size, clearanceDay, type, unit, accountStatus, isClear);
    }

    /***
     * Qbao_后台 查询清关算详细+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台 查询清关算详细+分页", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GetRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findClearanceDetailByPage",method = RequestMethod.GET,produces = "application/json")
    public Page<ClearanceDetail> findClearanceDetailByPage(Integer page, Integer size, Long clearanceId){
        logger.info("findClearanceDetailByPage");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}

        return clearanceService.findClearanceDetailByPage(page,size,clearanceId);
    }
    /***
     * Qbao_后台 查询清关算错误的明细
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台  查询清关算错误的明细", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GetRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findUnClearClearanceDetail",method = RequestMethod.GET,produces = "application/json")
    public List<ClearanceDetail> findUnClearClearanceDetail(Integer page, Integer size, Long clearanceId){
        logger.info("findClearanceDetailByPage");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}

        return clearanceService.findUnClearClearanceDetail(clearanceId);
    }
    /***
     * Qbao_后台 查询所有清关算详细+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value="Qbao_后台 查询所有清关算详细+分页", notes="params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GetRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findAllClearanceDetailByPage", method = RequestMethod.GET, produces = "application/json")
    public Page<ClearanceDetail> findAllClearanceDetailByPage(Integer page, Integer size, String clearanceDay, Long clearanceId, Integer qbaoType, Integer chainType, Long qbaoUnit, Long chainUnit, Boolean isClear) {
        logger.info("findClearanceDetailByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }

        return clearanceService.findAllClearanceDetailByPage(page, size, clearanceDay, clearanceId, qbaoType, chainType, qbaoUnit, chainUnit, isClear);
    }

    /***
     * Qbao_后台 对账处理
     * @return
     */
    @ApiOperation(value = "Qbao_后台 对账处理", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Clearance.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/admin/updateClearance", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Clearance updateClearance(@RequestBody Clearance clearance) {
        logger.info("/admin/updateClearance");
        Assert.notNull(clearance, "clearance not null");
        Assert.notNull(clearance.getId(), "id not null");
        return clearanceService.updateClearance(clearance);
    }

    /***
     * Qbao_后台 查询清关算的明细
     * @return
     */
    @ApiOperation(value="Qbao_后台  查询清关算的明细", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GetRedPacket.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findClearanceDetail",method = RequestMethod.GET,produces = "application/json")
    public List<ClearanceDetail> findClearanceDetail(Long clearanceId){
        logger.info("findClearanceDetail");

        return clearanceService.findClearanceDetail(clearanceId);
    }
}
