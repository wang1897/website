package com.aethercoder.core.controller;

import com.aethercoder.core.entity.social.GroupTags;
import com.aethercoder.core.service.GroupOfTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2018/1/3 下午2:49
 */
@RestController
@RequestMapping("groupTags")
@Api(tags = "groupTags", description = "群标签接口管理")
public class GroupOfTagsController {
    private static Logger logger = LoggerFactory.getLogger(GroupOfTagsController.class);

    @Autowired
    private GroupOfTagsService groupOfTagsService;

    /**
     * Qbao 添加群标签
     *
     * @param groupTags
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 添加群标签", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupTags.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveGroupTag", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public List<GroupTags> saveGroupTags(@RequestBody List<GroupTags> groupTags) {
        logger.info("saveGroupTag");
        Assert.notNull(groupTags.size(),"tag not null");
        return groupOfTagsService.saveGroupTags(groupTags);
    }

    /**
     * Qbao 更新群标签顺序
     *
     * @param tagIdList
     * @return group
     */
    @ApiOperation(value = "Qbao 更新群标签顺序", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupTags.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @org.springframework.transaction.annotation.Transactional
    @RequestMapping(value = "/admin/updateGroupTagsSequence", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map updateGroupTagsSequence(@RequestBody Long[] tagIdList) {

        logger.info("updateGroupTagsSequence");
        Map<String, Object> map = new HashMap<>();
        groupOfTagsService.updateGroupSequence(tagIdList);
        map.put("result", "success");
        return map;
    }

    /**
     * Qbao 更新群标签名称
     *
     * @param groupTags
     * @return group
     */
    @ApiOperation(value = "Qbao 更新群标签名称", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupTags.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/updateGroupTagsName", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map updateGroupTagsName(@RequestBody GroupTags groupTags) {

        logger.info("updateGroupTagsName");
        Map<String, Object> map = new HashMap<>();
        groupOfTagsService.updateGroupName(groupTags);
        map.put("result", "success");
        return map;
    }


    /***
     * Qbao_后台 删除群标签
     * @return
     */
    @ApiOperation(value = "Qbao_后台 删除群标签", notes = "参数：id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupTags.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteGroupTags", method = RequestMethod.GET, produces = "application/json")
    public Map deleteGroupTags(String[] id) {
        logger.info("/admin/deleteGroupTags");
        Assert.notNull(id.length==0, "id not null");
        Map<String, Object> map = new HashMap<>();
       groupOfTagsService.deleteGroupTags(id);
        map.put("result","success");
        return map;
    }

    /***
     * Qbao 查询所有群标签关联群个数
     * @return
     */
    @ApiOperation(value="Qbao 查询所有群标签关联群个数", notes="返回List<SysConfig>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupTags.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findGroupTagsAllAndGroups",method = RequestMethod.GET,produces = "application/json")
    public List<GroupTags> findGroupTagsAllAndGroups(){
        logger.info("findGroupTagsAllAndGroups");
        return groupOfTagsService.findGroupTagsAllAndGroups();
    }

    /***
     * Qbao 查询所有群标签
     * @return
     */
    @ApiOperation(value="Qbao 查询所有群标签", notes="返回List<SysConfig>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupTags.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findGroupTagsAll",method = RequestMethod.GET,produces = "application/json")
    public List<GroupTags> findGroupTagsAll(){
        logger.info("findSysConfigAll");
        return groupOfTagsService.findAllGroupTags();
    }


}
