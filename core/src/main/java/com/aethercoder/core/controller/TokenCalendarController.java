package com.aethercoder.core.controller;

import com.aethercoder.core.entity.media.TokenCalendar;
import com.aethercoder.core.service.TokenCalendarService;
import com.aethercoder.foundation.service.LocaleMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilangfeng
 * @date 2018/01/25
 */

@RestController
@RequestMapping("tokenCalendar")
@Api(tags = "tokenCalendar", description = "币月历")
public class TokenCalendarController {
    private static Logger logger = LoggerFactory.getLogger(TokenCalendar.class);
    @Autowired
    private TokenCalendarService tokenCalendarService;
    @Autowired
    public LocaleMessageService localeMessageUtil;


    /**
     * 新建事件
     * 后台管理
     */

    @ApiOperation(value = "后台管理新建事件", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = TokenCalendar.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有访问权限"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @RequestMapping(value = "/admin/createTokenCalendar", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public TokenCalendar createTokenCalendar(@RequestBody TokenCalendar tokenCalendar) {
        logger.info("createTokenCalendar");
        return tokenCalendarService.createTokenCalendar(tokenCalendar);
    }

    /**
     * 删除事件
     * 后台管理
     */
    @ApiOperation(value = "后台管理删除事件", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = TokenCalendar.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有访问权限"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @RequestMapping(value = "/admin/deleteTokenCalendar", method = RequestMethod.GET)
    public Map deleteContentAdmin(@RequestParam Long id) {
        logger.info("deleteTokenCalendar");
        Assert.notNull(id, "id not null");
        tokenCalendarService.deleteTokenCalendar(id);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;
    }

    /**
     * 查看事件
     * 后台管理
     */
    @ApiOperation(value = "查看币月历", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = TokenCalendar.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有访问权限"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @RequestMapping(value = "/admin/findTokenCalendar", method = RequestMethod.GET, produces = "application/json")
    public TokenCalendar findTokenCalendar(@RequestParam Long id) {
        logger.info("findTokenCalendar");
        Assert.notNull(id, "id not null");
        return tokenCalendarService.findTokenCalendar(id);
    }

    /**
     * 更新事件
     * 后台管理
     */
    @ApiOperation(value = "后台管理更新月币", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = TokenCalendar.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有访问权限"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @RequestMapping(value = "/admin/updateTokenCalendar", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public TokenCalendar updateTokenCalendar(@RequestBody TokenCalendar tokenCalendar) {
        return tokenCalendarService.updateTokenCalendar(tokenCalendar);
    }

    /**
     * 查找所有
     * 后台管理
     */
    @ApiOperation(value = "查找所有", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = TokenCalendar.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有访问权限"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @RequestMapping(value = "/admin/findAllTokenCalendar", method = RequestMethod.GET, produces = "application/json")
    public List<TokenCalendar> findAllTokenCalendar(@RequestParam String languageType) {
        return tokenCalendarService.findAllTokenCalendar(languageType);
    }

    /**
     * Qbao 币月历
     *
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/getTokenCalendarInfo", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "TokenCalendarInfo", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = TokenCalendar.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    public List<TokenCalendar> getTokenCalendarInfo(HttpServletRequest request, @RequestParam String tokenDateTime) {
        logger.info("getTokenCalendarInfo");
        String language = LocaleContextHolder.getLocale().getLanguage();
        return tokenCalendarService.findTokenCalendarByTime(language, tokenDateTime);
    }

    /**
     * Qbao 币月历事件分享
     *
     * @return
     * @throws Throwable
     */

    @ApiOperation(value = "币月历事件分享", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getTokenCalendarUrl", method = RequestMethod.GET, produces = "application/json")
    public Map getTokenCalendarUrl(@RequestParam String eventDate) {
        logger.info("getTokenCalendarUrl");
        Assert.notNull(eventDate,"eventDate can not be null");
        String language = LocaleContextHolder.getLocale().getLanguage();
        String tokenCalendarUrl = tokenCalendarService.getTokenCalendarUrl(eventDate, language);
        Map map = new HashMap<>();
        map.put("result", tokenCalendarUrl);
        return map;
    }

    /**
     * Qbao 获取币月历事件
     *
     * @return
     * @throws Throwable
     */

    @ApiOperation(value = "获取币月历事件", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getTokenCalendarEventInfo", method = RequestMethod.GET, produces = "application/json")
    public Map getTokenCalendarEventInfo(@RequestParam String date) {
        logger.info("getTokenCalendarEventInfo");
        Assert.notNull(date,"date can not be null");
        String language = LocaleContextHolder.getLocale().getLanguage();
        List tokenCalendarInfo = tokenCalendarService.getTokenCalendarEventInfo(language,date);
        Map map = new HashMap<>();
        map.put("result", tokenCalendarInfo);
        return map;
    }
}


