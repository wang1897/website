package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.wallet.ExchangeLogType;
import com.aethercoder.core.service.ExchangeLogTypeService;
import com.aethercoder.foundation.entity.i18n.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/9
 * @modified By:
 */
@RestController
@RequestMapping("exchangeLogType")
@Api(tags = "exchangeLogType", description = "交易记录类型接口管理")
public class ExchangeLogTypeController {

    private static Logger logger = LoggerFactory.getLogger(ExchangeLogTypeController.class);

    @Autowired
    private ExchangeLogTypeService exchangeLogTypeService;

    /***
     * Qbao 后台 获取交易记录类型
     * @return
     */
    @ApiOperation(value = "Qbao 后台 获取交易记录类型", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = ExchangeLogType.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getExchangeLogTypeByLanguage", method = RequestMethod.GET, produces = "application/json")
    public List<ExchangeLogType> getExchangeLogTypeByLanguageForAdmin() {
        logger.info("admin/getExchangeLogTypeByLanguage");
        return exchangeLogTypeService.getExchangeLogTypeByLanguageForAdmin();
    }

    /***
     * Qbao 后台 添加交易记录类型
     * @return
     */
    @ApiOperation(value = "Qbao 后台 添加交易记录类型", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/saveExchangeLogTypeForAdmin", method = RequestMethod.POST, produces = "application/json")
    public Map insertExchangeLogTypeForAdmin(@RequestBody ExchangeLogType exchangeLogType) {
        logger.info("admin/insertExchangeLogTypeForAdmin");
        Assert.notNull(exchangeLogType,"exchangeLogType cannot be null");
        Assert.notNull(exchangeLogType.getTypeName(),"exchangeLogType.getTypeName() cannot be null");
        Assert.notNull(exchangeLogType.getKoName(),"exchangeLogType.getKoName() cannot be null");
        Assert.notNull(exchangeLogType.getEnName(),"exchangeLogType.getEnName() cannot be null");
        Assert.notNull(exchangeLogType.getId(),"exchangeLogType.getId() cannot be null");

        exchangeLogTypeService.saveExchangeLogTypeForAdmin(exchangeLogType);

        Map map = new HashMap();
        map.put("result","success");
        return map;
    }

    /***
     * Qbao 后台 更新交易记录类型
     * @return
     */
    @ApiOperation(value = "Qbao 后台 更新交易记录类型", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/updateExchangeLogTypeForAdmin", method = RequestMethod.POST, produces = "application/json")
    public Map updateExchangeLogTypeForAdmin(@RequestBody ExchangeLogType exchangeLogType) {
        logger.info("admin/updateExchangeLogTypeForAdmin");
        Assert.notNull(exchangeLogType,"exchangeLogTypeList cannot be null");
        exchangeLogTypeService.updateExchangeLogTypeForAdmin(exchangeLogType);

        Map map = new HashMap();
        map.put("result","success");
        return map;
    }

    /***
     * Qbao_app 获取指定语言下交易记录类型
     * @return
     */
    @ApiOperation(value = "Qbao_app 获取指定语言下交易记录类型", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Message.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getExchangeLogTypeByLanguage", method = RequestMethod.GET, produces = "application/json")
    public List<ExchangeLogType> getExchangeLogTypeByLanguage(HttpServletRequest request) {
        logger.info("getExchangeLogTypeByLanguage");
        String language = request.getHeader(WalletConstants.REQUEST_HEADER_LANGUAGE);
        Assert.notNull(language, "language not null");
        return exchangeLogTypeService.getExchangeLogTypeByLanguage(language);
    }
}
