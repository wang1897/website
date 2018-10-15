package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.social.SystemNotice;
import com.aethercoder.core.service.SystemNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilangfeng
 * @date 2018/01/05
 **/

@RestController
@RequestMapping( "systemNotice" )
@Api( tags = "systemNotice", description = "系统公告接口" )

public class SystemNoticeController {

    private static Logger logger = LoggerFactory.getLogger(SystemNoticeController.class);
    @Autowired
    private SystemNoticeService systemNoticeService;

    /**
     * *后台管理 新建系统公告
     **/
    @ApiOperation( value = "后台管理 新建公告", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = SystemNotice.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/admin/createSystemNotice", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
    public SystemNotice createSystemNotice(@RequestBody SystemNotice systemNotice) {
        logger.info("createSystemNotice");
        return systemNoticeService.createSystemNotice(systemNotice);
    }

    /**
     * *后台管理 更新公告
     **/
    @ApiOperation( value = "后台管理 更新公告", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = SystemNotice.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/admin/updateSystemNotice", method = RequestMethod.POST, consumes = "application/json", produces = "application/json" )
    public SystemNotice updateSystemNotice(@RequestBody SystemNotice systemNotice) {
        logger.info("updateSystemNotice");
        Assert.notNull(systemNotice.getLanguageType(), "语言类型不能为空");
        Assert.notNull(systemNotice.getStatus(), "状态不能为空");
        return systemNoticeService.updateSystemNotice(systemNotice);
    }

    /**
     * 后台管理 删除公告
     *
     * @param
     * @return
     */
    @ApiOperation( value = "后台管理 删除公告", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = SystemNotice.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/admin/deleteSystemNotice", method = RequestMethod.GET )
    public Map deleteSystemNotice(@RequestParam Long id) {
        logger.info("deleteSystemNotice");
        systemNoticeService.deleteNotice(id);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;

    }

    /**
     * 后台管理 公告内容检索+分页
     *
     * @param
     * @return
     */
    @ApiOperation( value = "后台管理 公告内容检索+分页", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = SystemNotice.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/admin/findSystemNoticeByContentLike", method = RequestMethod.GET, produces = "application/json" )
    public Page<SystemNotice> findAllSystemNoticesByPage(Integer page, Integer size, String content, Integer status,String languageType) {
        logger.info("findAllSystemNoticesByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return systemNoticeService.findSystemNoticesByPage(page, size, content, status,languageType);
    }

    /**
     * APP 按语言检索现有公告内容
     *
     * @param
     * @return
     */
    @ApiOperation( value = "APP 按语言检索现有公告内容", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = SystemNotice.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/findSystemNoticeByLanguage", method = RequestMethod.GET, produces = "application/json" )
    public List<SystemNotice> findSystemNoticeByLanguage(HttpServletRequest request) {
        logger.info("findSystemNoticeByLanguage");
        String language = request.getHeader(WalletConstants.REQUEST_HEADER_LANGUAGE);
        return systemNoticeService.findSystemNoticeByLanguage(language);
    }

    /**
     * APP 历史公告内容检索+分页
     *
     * @param
     * @return
     */
    @ApiOperation( value = "APP 历史公告内容检索+分页", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = SystemNotice.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/findSystemNoticeHistoryList", method = RequestMethod.GET, produces = "application/json" )
    public Page<SystemNotice> findSystemNoticeHistoryList(Integer page, Integer size) {
        logger.info("findSystemNoticeHistoryList");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        String languageType =  LocaleContextHolder.getLocale().getLanguage();
        return systemNoticeService.findSystemNoticesByPage(page, size, languageType);
    }
}



 
