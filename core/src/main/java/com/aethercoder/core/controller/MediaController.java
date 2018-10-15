package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.media.Media;
import com.aethercoder.core.service.MediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilangfeng
 * @date 2018/01/22
 */
@RestController
@RequestMapping("media")
@Api(tags = "media", description = "媒体列表")
public class MediaController {

    private static Logger logger = LoggerFactory.getLogger(MediaController.class);
    @Autowired
    private MediaService mediaService;

    /**
     * 创建媒体列表
     */
    @ApiOperation(value = "媒体列表", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Media.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/createMedia", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Media createMedia(@RequestBody Media media) {
        logger.info("createMedia");
        Assert.notNull(media,"media is not null");
        return mediaService.createMedia(media);
    }


    /**
     * 查看事件
     * 媒体列表
     */
    @ApiOperation(value = "查看事件媒体列表", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Media.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有访问权限"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @RequestMapping(value = "/admin/findMedia", method = RequestMethod.GET, produces = "application/json")
    public Page<Media> findAllSystemNoticesByPage(Integer page, Integer size, String name, Integer status, String languageType) {
        logger.info("findAllMediaByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return mediaService.findMediasByPage(page,size,name,status,languageType);
    }
    /**
     * 更新媒体列表
     */
    @ApiOperation(value = "更新", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Media.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有访问权限"),
            @ApiResponse(code = 500, message = "系统错误")
    })
    @Transactional
    @RequestMapping(value = "/admin/updateMedia", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Media updateMedia(@RequestBody Media media) {
        Assert.notNull(media,"media is not null");
        return mediaService.updateMedia(media);
    }


    /**
     * Qbao 更改媒列表排列顺序
     *
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/getMediaInfo", method = RequestMethod.GET, produces = "application/json")
    @ApiOperation(value = "getMediaInfo", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Media.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    public List<Media> getMadiaInfo(HttpServletRequest request) {
        logger.info("getMediaInfo");
        String language = LocaleContextHolder.getLocale().getLanguage();
        return mediaService.findMediaByLanguageAndStatusIsDefaultUsing(language);
    }
    /**
     * Qbao 修改状态
     *
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/admin/modifyStatus", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "modifyStatus", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Media.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    public Media modifyMediaStatus(@RequestBody Media media) {
        logger.info("modifyMediaStatus");
        return mediaService.modifyStatus(media);
    }

    /**
     * Qbao 修改排序
     *
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/admin/sortMediaBySequence", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "sortMediaBySequence", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Media.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    public Map sortMediaBySequence(@RequestBody Long[]  tagIdList) {
            logger.info("sortMediaSequence");
            Map<String, Object> map = new HashMap<>();
            mediaService.sortMediaBySequence(tagIdList);
            map.put("result", "success");
            return map;
    }
}














