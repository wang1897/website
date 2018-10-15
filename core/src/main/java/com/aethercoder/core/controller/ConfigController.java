package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("config")
@Api(tags = "SysConfig", description = "配置接口管理（地址等）")
public class ConfigController {
    private static Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private SysConfigService sysConfigService;


    /***
     * Qbao 查询所有配置信息
     * @return
     */
    @ApiOperation(value="Qbao 查询所有配置地址", notes="返回List<SysConfig>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysConfig.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findSysConfigAll",method = RequestMethod.GET,produces = "application/json")
    public List<SysConfig> findSysConfigtAll(){
        logger.info("findSysConfigAll");
        return sysConfigService.findAllSysConfig();
    }



    /***
     * Qbao 根据name查询配置地址信息
     * @return
     */
    @ApiOperation(value="Qbao 根据name查询配置地址信息", notes="返回SysConfig")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysConfig.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findSysConfigByName",method = RequestMethod.GET,produces = "application/json")
    public SysConfig findSysConfigByName(@RequestParam("name") String name){
        logger.info("findSysConfigByName");
        Assert.notNull(name,"name not null");
        return sysConfigService.findSysConfigByName(name);
    }




    /***
     * Qbao_后台 添加地址
     * @return
     */
    @ApiOperation(value="Qbao_后台 添加地址", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysConfig.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveConfig",method = RequestMethod.POST,produces = "application/json", consumes = "application/json")
    public SysConfig saveConfig(@RequestBody SysConfig sysConfig){
        logger.info("/admin/saveConfig");
        Assert.notNull(sysConfig,"contract not null");
        return sysConfigService.saveSysConfig(sysConfig);
    }

    /***
     * Qbao_后台 修改地址
     * @return
     */
    @ApiOperation(value="Qbao_后台 修改地址", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysConfig.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateConfig",method = RequestMethod.POST,produces = "application/json", consumes = "application/json")
    public SysConfig updateConfig(@RequestBody SysConfig sysConfig){
        logger.info("/admin/updateConfig");
        Assert.notNull(sysConfig,"contract not null");
        Assert.notNull(sysConfig.getId(),"id not null");
        return sysConfigService.updateSysConfig(sysConfig);
    }

    /***
     * Qbao_后台 删除单个地址配置信息
     * @return
     */
    @ApiOperation(value="Qbao_后台 删除单个地址配置信息", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysConfig.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteConfig",method = RequestMethod.GET,produces = "application/json")
    public Map deleteConfig(@RequestParam("id") Long id){
        logger.info("/admin/deleteConfig");
        Assert.notNull(id,"id not null");
        sysConfigService.deleteSysConfig(id);
        Map<String, Object> map = new HashMap<>();
        map.put("result","success");
        return map;
    }


    /***
     * 获取所有语言客服聊天URL列表
     * @return
     */
    @ApiOperation(value="获取所有语言客服URL列表", notes="参数：1. accountNo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysConfig.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getCSWindowUrlList",method = RequestMethod.GET,produces = "application/json")
    public List<SysConfig> getCSWindowUrlList(){
        logger.info("getCSWindowUrlList");
        return sysConfigService.findSysConfigsLikeNameAhead(WalletConstants.CONFIG_CUSTOMER_SERVICE_PREFIX);
    }

}
