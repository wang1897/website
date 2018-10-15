package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.social.GroupNotice;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.GroupNoticeService;
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
 * @date 2017/12/18 下午7:09
 */
@RestController
@RequestMapping("notice")
@Api(tags = "notice", description = "群公告接口管理")
public class GroupNoticeController {

    private static Logger logger = LoggerFactory.getLogger(GroupNoticeController.class);

    @Autowired
    private GroupNoticeService groupNoticeService;


    /**
     * Qbao 创建群公告
     *
     * @param groupNotice
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 创建群公告", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupNotice.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/saveGroupNotice", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public GroupNotice saveGroupNotice(@RequestBody GroupNotice groupNotice) {
        logger.info("saveGroupNotice");
        groupNotice.setId(null);
        Assert.notNull(groupNotice.getCreateBy(),"createBy not null");
        Assert.notNull(groupNotice.getGroupNo(),"groupNo not null");
        Assert.notNull(groupNotice.getContent(),"content not null");
        Assert.notNull(groupNotice.getTitle(),"title not null");
        return groupNoticeService.saveGroupNotice(groupNotice);
    }
    /**
     * Qbao_admin 创建群公告
     *
     * @param groupNotice
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao_admin 创建群公告", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupNotice.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveGroupNotice", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public GroupNotice saveGroupNoticeByAdmin(@RequestBody GroupNotice groupNotice) {
        logger.info("admin/saveGroupNotice");
        groupNotice.setId(null);
        Assert.notNull(groupNotice.getGroupNo(),"groupNo not null");
        Assert.notNull(groupNotice.getContent(),"content not null");
        Assert.notNull(groupNotice.getTitle(),"title not null");
        return groupNoticeService.saveGroupNotice(groupNotice);
    }

    /***
     * Qbao 删除单个公告
     * @return
     */
    @ApiOperation(value = "Qbao 删除单个公告", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysConfig.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteNotice", method = RequestMethod.GET, produces = "application/json")
    public Map deleteContractOneByAdmin(@RequestParam("id") Long id, @RequestParam("groupNo") String groupNo, @RequestParam("createBy") String createBy) {
        logger.info("/admin/deleteNotice");
        Assert.notNull(id, "id not null");
        Assert.notNull(groupNo, "groupNo not null");
        Assert.notNull(createBy, "createBy not null");
        groupNoticeService.deleteNotice(id, groupNo, createBy);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao 查询所有公告信息
     * @return
     */
    @ApiOperation(value = "Qbao 查询所有公告信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupNotice.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/queryNotices", method = RequestMethod.GET, produces = "application/json")
    public Page<GroupNotice> queryNotices(Integer page, Integer size,String groupNo, String title) {
        logger.info("queryNotices");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return groupNoticeService.findGroupNoticesByGroupNo(page, size,groupNo,title);
    }

    /***
     * Qbao 获取该公告信息
     * @return
     */
    @ApiOperation(value = "Qbao 获取该公告信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupNotice.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getNoticeInfo", method = RequestMethod.GET, produces = "application/json")
    public GroupNotice getNoticeInfo(@RequestParam long id) {
        logger.info("getNoticeInfo");
        Assert.notNull(id, "id not  null");
        return groupNoticeService.findById(id);
    }

    /***
     * Qbao 获取该群的最新公告信息
     * @return
     */
    @ApiOperation(value = "Qbao 获取该群的最新公告信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupNotice.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getLatestNoticeInfo", method = RequestMethod.GET, produces = "application/json")
    public Map getNoticeInfo(@RequestParam String groupNo ) {
        logger.info("getLatestNoticeInfo");
        Assert.notNull(groupNo, "groupNo not  null");
        GroupNotice groupNotice =  groupNoticeService.getLatestNoticeInfo(groupNo);
        Map<String, Object> map = new HashMap<>();
        map.put("result", groupNotice);
        return map;
    }

    /***
     * Qbao 获取该群所有公告信息
     * @return
     */
    @ApiOperation(value = "Qbao 获取该群所有公告信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupNotice.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getNoticesInfoByGroupNo", method = RequestMethod.GET, produces = "application/json")
    public Page<GroupNotice> getNoticesInfoByGroupNo(Integer page, Integer size, @RequestParam String groupNo) {
        logger.info("getNoticesInfoByGroupNo");
        Assert.notNull(groupNo, "groupNo not null");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return groupNoticeService.findGroupNoticesByGroupNo(page, size, groupNo,null);
    }


    /***
     * Qbao 删除单个公告
     * @return
     */
    @ApiOperation(value = "Qbao 删除单个公告", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysConfig.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/deleteNotice", method = RequestMethod.GET, produces = "application/json")
    public Map deleteNotice(@RequestParam("id") Long id, @RequestParam("groupNo") String groupNo, @RequestParam("createBy") String createBy) {
        logger.info("/deleteNotice");
        Assert.notNull(id, "id not null");
        Assert.notNull(groupNo, "groupNo not null");
        Assert.notNull(createBy, "createBy not null");
        groupNoticeService.deleteNotice(id, groupNo, createBy);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;
    }


}
