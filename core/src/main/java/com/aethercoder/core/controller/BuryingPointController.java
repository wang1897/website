package com.aethercoder.core.controller;

import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.URLDecipheringService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/4/3
 * @modified By:
 */
@RestController
@RequestMapping("buryingPoint")
@Api(tags = "buryingPoint", description = "在线客服埋点信息接口")
public class BuryingPointController {
    private static Logger logger = LoggerFactory.getLogger(AndroidController.class);

    @Autowired
    private URLDecipheringService urlDecipheringService;

    /***
     * Qbao APP 获取埋点信息
     * @return
     */
    @ApiOperation(value = "Qbao APP 获取埋点信息", notes = "Map")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getBuryingPointInfo", method = RequestMethod.GET, produces = "application/json")
    public Map getBuryingPointInfo(@RequestParam String version,@RequestParam String machineType) {
        logger.info("getBuryingPointInfo");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();

        String uri = urlDecipheringService.sealBuryingPointInfo(accountNo, version, machineType);
        Map map = new HashMap();
        map.put("uri",uri);
        return map;
    }

    /***
     * Qbao APP android 获取埋点信息
     * @return
     */
    @ApiOperation(value = "Qbao APP android 获取埋点信息", notes = "Map")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getBuryingPointInfoForAndroid", method = RequestMethod.GET, produces = "application/json")
    public Map getBuryingPointInfoForAndroid(@RequestParam String version,@RequestParam String machineType) {
        logger.info("getBuryingPointInfoForAndroid");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        String language = LocaleContextHolder.getLocale().getLanguage();

        String uri = urlDecipheringService.sealBuryingPointInfoForAndroid(accountNo, version, machineType,language);
        Map map = new HashMap();
        map.put("uri",uri);
        return map;
    }

    /***
     * Qbao 客服 解密埋点信息
     * @return
     */
    @ApiOperation(value = "Qbao 客服 解密埋点信息", notes = "Map")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getDecryptBuryingPointInfo", method = RequestMethod.GET, produces = "application/json")
    public Map getDecryptBuryingPointInfo(HttpServletRequest request) {
        logger.info("getDecryptBuryingPointInfo");

        return urlDecipheringService.getCustomerInfo(request);
    }
}
