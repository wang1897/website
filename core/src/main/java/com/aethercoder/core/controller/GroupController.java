package com.aethercoder.core.controller;

import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.json.Data;
import com.aethercoder.core.entity.social.*;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.GroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiawei.tao on 2017/10/09.
 */
@RestController
@RequestMapping("group")
@Api(tags = "group", description = "群接口管理")
public class GroupController {

    private static Logger logger = LoggerFactory.getLogger(GroupController.class);

    @Autowired
    private GroupService groupService;

    /**
     * Qbao-创建群
     *
     * @param buildGroup
     * @return group
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 创建群", notes = "params: 1.accountNo 2. logoUrl 3.unit 4.amount  5.name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/buildGroup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Group buildGroup(@RequestBody BuildGroup buildGroup) {

        logger.info("buildGroup");
        return groupService.buildGroup(buildGroup);
    }


    /**
     * Qbao-后台创建群
     *
     * @param group
     * @return group
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 后台创建群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/createGroup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Group createGroup(@RequestBody Group group) {
        logger.info("admin/createGroup");
        return groupService.createGroup(group);
    }

    /**
     * Qbao 更新群
     *
     * @param group
     * @return group
     */
    @ApiOperation(value = "Qbao 更新群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/updateGroup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Group updateGroup(@RequestBody Group group) {
        logger.info("admin/updateGroup");
        return groupService.updateGroup(group);
    }

    /***
     * Qbao_后台 删除群
     * @param groupNo
     */
    @ApiOperation(value = "Qbao_后台 删除单个群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = String.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteGroup", method = RequestMethod.GET)
    public Map deleteGroup(@RequestParam("groupNo") String groupNo) {

        logger.info("admin/deleteGroup");
        Map<String, Object> map = new HashMap<>();
        groupService.deleteGroup(groupNo);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao_后台 解散群
     * @param groupNo
     */
    @ApiOperation(value = "Qbao_后台 解散单个群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = String.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/dismissGroup", method = RequestMethod.GET)
    public Map dismissGroup(@RequestParam("groupNo") String groupNo) {

        logger.info("admin/dismissGroup");
        Map<String, Object> map = new HashMap<>();
        groupService.deleteGroup(groupNo);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao 解散群
     * @param groupNo
     */
    @ApiOperation(value = "Qbao 解散单个群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = String.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/dismissGroup", method = RequestMethod.GET)
    public Map dismissGroupByApp(@RequestParam("groupNo") String groupNo) {

        logger.info("dismissGroupByApp");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Map<String, Object> map = new HashMap<>();
        groupService.dismissGroup(groupNo, accountNo);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao 查询取得群
     * @param groupNo
     * @return
     */
    @ApiOperation(value = "Qbao 取得群", notes = "返回Group")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findGroupById", method = RequestMethod.GET, produces = "application/json")
    public Group findGroupById(@RequestParam("groupNo") String groupNo) {
        logger.info("findGroupById");
        return groupService.findByGroupNo(groupNo);
    }

    /***
     * Qbao 查询所有群
     * @return
     */
    @ApiOperation(value = "Qbao 查询所有群", notes = "返回List<Group>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findGroupAll", method = RequestMethod.GET, produces = "application/json")
    public List<Group> findGroupAll() {
        logger.info("findGroupAll");
        return groupService.findGroupAll();
    }

    /***
     * Qbao 查询所有群+分页
     * @return
     */
    @ApiOperation(value = "Qbao 查询所有群+分页", notes = "返回List<Group>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findGroupsByPage", method = RequestMethod.GET, produces = "application/json")
    public Page<Group> findGroupsByPage(Integer page, Integer size,
                                        @RequestParam(required = false, value = "groupNo") String groupNo,
                                        @RequestParam(required = false, value = "groupName") String groupName,
                                        @RequestParam(required = false, value = "level") Integer level,
                                        @RequestParam(required = false, value = "isDeleted") Boolean isDeleted,
                                        @RequestParam(required = false, value = "limitUnit") Long limitUnit,
                                        @RequestParam(required = false, value = "tag") String tag) {
        logger.info("findGroupsByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return groupService.findGroupsByPage(page, size, groupNo, groupName, level, isDeleted, limitUnit, tag);
    }

    /***
     * Qbao 火热群
     * @return
     */
    @ApiOperation(value = "Qbao 火热群", notes = "返回List<Group>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getHotGroups", method = RequestMethod.GET, produces = "application/json")
    public List<Group> getHotGroups(Integer page, Integer size) {
        logger.info("getHotGroups");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return groupService.findGroupsByPage(page, size);
    }

    /***
     * Qbao 查询等于群ID或群名包含检索项的群+分页
     * @return
     */
    @ApiOperation(value = "Qbao 查询等于群ID或群名包含检索项的群+分页", notes = "返回List<Group>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findGroupsByPageAndName", method = RequestMethod.GET, produces = "application/json")
    public Page<Group> findGroupsByPageAndName(Integer page, Integer size, @RequestParam String groupNoOrName) {
        logger.info("findGroupsByPageAndName");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return groupService.findGroupsByPage(page, size, groupNoOrName);
    }

    /**
     * Qbao 添加群成员
     *
     * @param groupUserInfo
     * @return group
     */
    @ApiOperation(value = "Qbao 添加群成员", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/joinGroup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map joinGroup(@RequestBody Map groupUserInfo) {

        logger.info("joinGroup");
        String groupNo = groupUserInfo.get("groupNo").toString();
        Assert.notNull(groupNo,"groupNo can not be null!");
        logger.debug("joinGroup", groupNo + "," + groupUserInfo.get("accountNoList").toString());
        ArrayList<String> accountNoList = (ArrayList<String>) groupUserInfo.get("accountNoList");
        Assert.notEmpty(accountNoList,"accountNo can not be null!");
        String confirmStatus= (String) groupUserInfo.get("type");
        Integer confirmStatus1 = confirmStatus == null?null:Integer.valueOf(confirmStatus);
        String operator = (String) groupUserInfo.get("operator");
        Object confirmInfo = groupUserInfo.get("confirmInfo");
        String confirmInfoStr = confirmInfo == null?null:(String)confirmInfo;

        groupService.joinGroupWithEx(groupNo, accountNoList.toArray(new String[accountNoList.size()]), operator,confirmInfoStr,confirmStatus1,false);
        Map<String, Object> map = new HashMap<>();
        map.put("result", "success");
        return map;
    }


    /**
     * Qbao 添加群成员
     *
     * @param groupUserInfo
     * @return group
     */
    @ApiOperation(value = "Qbao 后台添加群成员", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/joinGroup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map joinGroupAdmin(@RequestBody Map groupUserInfo) {

        logger.info("joinGroup");
        String groupNo = groupUserInfo.get("groupNo").toString();
        logger.debug("joinGroup ", groupNo + "," + groupUserInfo.get("accountNoList").toString());
        ArrayList<String> accountNoList = (ArrayList<String>) groupUserInfo.get("accountNoList");
        String operator = (String) groupUserInfo.get("operator");
        String confirmInfo = (String) groupUserInfo.get("confirmInfo");
        return groupService.joinGroup(groupNo, accountNoList.toArray(new String[accountNoList.size()]), operator,confirmInfo,WalletConstants.JOIN_GROUP_STATUS_DEFAULT,true);
    }

    /**
     * Qbao 转让群
     *
     * @param groupUserInfo
     * @return group
     */
    @ApiOperation(value = "Qbao 转让群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/transferOfGroup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map transferOfGroup(@RequestBody Map groupUserInfo) {

        logger.info("joinGroup");
        Map<String, Object> map = new HashMap<>();
        String groupNo = groupUserInfo.get("groupNo").toString();
        String oldGroupLord = groupUserInfo.get("oldGroupLord").toString();
        String newGroupLord = groupUserInfo.get("newGroupLord").toString();
        Assert.notNull(groupNo, "groupNo not null");
        Assert.notNull(oldGroupLord, "oldGroupLord not null");
        Assert.notNull(newGroupLord, "newGroupLord not null");

        groupService.transferOfGroup(groupNo,oldGroupLord,newGroupLord);
        map.put("result","success");
        return map;
    }

    /**
     * Qbao 更新群顺序
     *
     * @param groupNoList
     * @return group
     */
    @ApiOperation(value = "Qbao 更新群顺序", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/updateGroupSequence", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map updateGroupSequence(@RequestBody String[] groupNoList) {

        logger.info("updateGroupSequence");
        Map<String, Object> map = new HashMap<>();
        groupService.updateGroupSequence(groupNoList);
        map.put("result", "success");
        return map;
    }

    /**
     * Qbao 踢出群成员
     *
     * @param groupUserInfo
     * @return group
     */
    @ApiOperation(value = "Qbao 踢出群成员", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/ejectGroup", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map ejectGroup(@RequestBody Map groupUserInfo) {

        logger.info("ejectGroup");
        Map<String, Object> map = new HashMap<>();
        String groupNo = groupUserInfo.get("groupNo").toString();
        ArrayList<String> accountNoList = (ArrayList<String>) groupUserInfo.get("accountNoList");
        logger.debug("ejectGroup ", groupNo + "," + groupUserInfo.get("accountNoList").toString());
        String operator = (String) groupUserInfo.get("operator");
        String isGap = (String) groupUserInfo.get("isGap");
        Boolean flag = groupUserInfo.get("sysBlack")==null?false:Boolean.valueOf(groupUserInfo.get("sysBlack").toString());
        groupService.ejectGroup(groupNo, accountNoList.toArray(new String[accountNoList.size()]), operator, isGap,flag);
         map.put("result", "success");
        return map;
    }

    /**
     * Qbao 更新群成员
     *
     * @param groupUserInfo
     * @return group
     */
    @ApiOperation(value = "Qbao 更新群成员,设置群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupMember.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/updateGroupMember", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map updateGroupMember(@RequestBody Map groupUserInfo) {
        logger.info("updateGroupMember");

        logger.info("updateGroupMember params: "+groupUserInfo.toString());
        Map<String, Object> map = new HashMap<>();
        ArrayList<GroupMember> memberList = (ArrayList<GroupMember>) groupUserInfo.get("memberList");
        String operator = (String) groupUserInfo.get("operator");
        List<GroupMember> members = BeanUtils.jsonToList(BeanUtils.objectToJson(memberList),GroupMember.class);
        groupService.updateGroupMember(members, operator);
        map.put("result", "success");
        return map;
    }

    /**
     * Qbao 移除黑名单上成员
     *
     * @param groupUserInfo
     * @return group
     */
    @ApiOperation(value = "Qbao 移除黑名单上成员", notes = "role ：成员权限\n" +
            "NULL/0-群众\n" +
            "2-群主\n" +
            "1-管理\n" +
            "9-ADMIN")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupMember.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/moveBlackListMember", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map moveBlackListMember(@RequestBody Map groupUserInfo) {

        logger.info("moveBlackListMember");
        Map<String, Object> map = new HashMap<>();
        ArrayList<GroupMember> memberList = (ArrayList<GroupMember>) groupUserInfo.get("memberList");
        String operator = (String) groupUserInfo.get("operator");
        List<GroupMember> members = BeanUtils.jsonToList(BeanUtils.objectToJson(memberList),GroupMember.class);
        groupService.moveBlackListMember(members, operator);
        map.put("result", "success");
        return map;
    }

    /**
     * Qbao 成员批量退出多个群
     *
     * @param groupUserInfo
     * @return group
     */
    @ApiOperation(value = "Qbao 成员批量退出多个群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/ejectGroupsList", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map ejectGroupsList(@RequestBody Map groupUserInfo) {

        logger.info("ejectGroupsList");
        Map<String, Object> map = new HashMap<>();
        ArrayList<String> groupNoList = (ArrayList<String>) groupUserInfo.get("groupNoList");
        ArrayList<String> accountNoList = (ArrayList<String>) groupUserInfo.get("accountNoList");
        logger.debug("ejectGroups ", groupNoList + "," + groupUserInfo.get("accountNoList").toString());
        String operator = (String) groupUserInfo.get("operator");
        groupService.ejectGroups(groupNoList.toArray(new String[groupNoList.size()]), accountNoList.toArray(new String[accountNoList.size()]), operator);
        map.put("result", "success");
        return map;
    }

    /**
     * Qbao 获取群信息包括群成员列表
     *
     * @param groupNo
     * @param groupNo
     * @return groupInfo
     */
    @ApiOperation(value = "获取群信息包括群成员列表 APP端使用", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupInfo.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/queryGroup", method = RequestMethod.GET, produces = "application/json")
    public GroupInfo queryGroup(@RequestParam("groupNo") String groupNo) {
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
        logger.info("queryGroup");
        return groupService.queryGroupByGroupNoAndAccountNo(groupNo, accountNo);
    }

    /**
     * Qbao 获取群所有成员
     *
     * @param groupNo
     * @return group
     */
    @ApiOperation(value = "Qbao 获取群所有成员", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/queryGroupMember", method = RequestMethod.GET, produces = "application/json")
    public List<GroupMember> queryGroupMember(@RequestParam("groupNo") String groupNo) {
        logger.info("queryGroupMember");
        return groupService.queryGroupMember(groupNo);
    }

    /**
     * Qbao 获取群管理员个数
     *
     * @param groupNo
     * @return number
     */
    @ApiOperation(value = "Qbao 获取群管理员个数", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/queryGroupAdminNumber", method = RequestMethod.GET, produces = "application/json")
    public Integer queryGroupAdminNumber(@RequestParam("groupNo") String groupNo) {
        logger.info("queryGroupAdminNumber");
        return groupService.queryGroupAdminNumber(groupNo);
    }

    /**
     * Qbao 获取群成员增量信息
     *
     * @param groupNo
     * @return group
     */
    @ApiOperation(value = "Qbao 获取群成员增量信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/syncGroupMember", method = RequestMethod.GET, produces = "application/json")
    public UserInfo syncGroupMember(@RequestParam("groupNo") String groupNo, @RequestParam(required = false) String timestamp) {
        logger.info("syncGroupMember");
        return groupService.syncGroupMember(groupNo, timestamp);
    }

    /**
     * Qbao 获取单个群成员信息
     *
     * @param groupNo
     * @return group
     */
    @ApiOperation(value = "Qbao 获取单个群成员信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getGroupMemberInfo", method = RequestMethod.GET, produces = "application/json")
    public GroupMember getGroupMemberInfo(@RequestParam("groupNo") String groupNo, @RequestParam("memberNo") String memberNo) {
        logger.info("getGroupMemberInfo");
        return groupService.getGroupMemberInfo(groupNo, memberNo);
    }

    /**
     * Qbao 模糊查询分页获取群成员
     *
     * @param groupNo
     * @return group
     */
    @ApiOperation(value = "Qbao 模糊查询分页获取群成员", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupMember.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/queryGroupByPage", method = RequestMethod.GET, produces = "application/json")
    public Page<GroupMember> queryGroupByPage(@RequestParam Integer page, @RequestParam Integer size, @RequestParam("groupNo") String groupNo, @RequestParam(required = false) String memberNoOrName, @RequestParam(required = false) Integer role, @RequestParam(required = false) Boolean isGap) {
        logger.info("queryGroupByPage");
        return groupService.queryGroupByPage(page, size, groupNo, memberNoOrName, role, isGap);
    }


    /***
     * Qbao 获取该当用户的群信息 增量
     * @param timestamp
     * @return
     */
    @ApiOperation(value = "Qbao 获取该当用户的群信息 增量", notes = "返回GroupInfo")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupInfo.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/syncUserGroupInfo", method = RequestMethod.GET, produces = "application/json")
    public UserInfo syncUserGroupInfo( @RequestParam(required = false) String timestamp) {
        logger.info("syncUserGroupInfo");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return groupService.syncUserGroupInfo(accountNo, timestamp);
    }

    /***
     * Qbao 获取当前群的未添加用户列表+分页
     * @param groupNo
     * @return
     */
    @ApiOperation(value = "获取当前群的未添加用户列表+分页", notes = "返回AccountList")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getAddGroupUsers", method = RequestMethod.GET, produces = "application/json")
    public Page<Account> queryAddGroupUsersByPage(@RequestParam Integer page, @RequestParam Integer size, @RequestParam String groupNo, String memberNoOrName) {

        logger.info("getAddGroupUsers");
        return groupService.queryAddGroupUsersByPage(page, size, groupNo, memberNoOrName);
    }

    /***
     * Qbao 获取推荐群
     * @return
     */
    @ApiOperation(value = "获取推荐群", notes = "返回GroupList")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getCommendGroups", method = RequestMethod.GET, produces = "application/json")
    public List<Group> getCommendGroups() {

        logger.info("getCommendGroups");
        return groupService.getGroupsByLevel(WalletConstants.GROUP_LEVEL_COMMEND);
    }

    /**
     * Qbao 获取所有群主
     *
     * @return group
     */
    @ApiOperation(value = "Qbao 获取所有群主", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupMember.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/queryGroupRole", method = RequestMethod.GET, produces = "application/json")
    public List<GroupMember> queryGroupRole() {
        logger.info("queryGroupRole");
        return groupService.findGroupMembersByRoleAndIsDeletedFalse(WalletConstants.GROUP_ROLE_HOSTER);
    }

    /**
     * Qbao 获取该群主的所有群
     *
     * @return group
     */
    @ApiOperation(value = "Qbao 获取该群主的所有群", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupMember.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/queryGroupsByAccountNo", method = RequestMethod.GET, produces = "application/json")
    public List<Group> queryGroupsByAccountNo() {
        logger.info("queryGroupsByAccountNo");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return groupService.findGroupsByHoster(accountNo);
    }

    /**
     * Qbao 获取该群主的所有群个数
     *
     * @return group
     */
    @ApiOperation(value = "Qbao 获取该群主的所有群个数", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupMember.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/queryGroupByAccountNo", method = RequestMethod.GET, produces = "application/json")
    public Map queryGroupByAccountNo() {
        logger.info("queryGroupByAccountNo");
        Map<String, Object> map = new HashMap<>();
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        List<Group> groupList = groupService.findGroupsByHoster(accountNo);
        map.put("result",groupList.size());
        return map;
    }


    /**
     * Qbao 获取该群群主
     *
     * @return group
     */
    @ApiOperation(value = "Qbao 获取该群群主", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupMember.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/queryGroupHostByGroupNo", method = RequestMethod.GET, produces = "application/json")
    public Map queryGroupHostByGroupNo(@RequestParam String groupNo) {
        logger.info("queryGroupHostByGroupNo");
        Map<String, Object> map = new HashMap<>();
        GroupMember groupMember = groupService.findByGroupNoAndRole(groupNo);
         map.put("result",groupMember);
        return map;
    }

    /**
     * Qbao 获取建群费用
     *
     * @return group
     */
    @ApiOperation(value = "Qbao 获取建群费用", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupMember.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/queryGroupBuildFee", method = RequestMethod.GET, produces = "application/json")
    public List<Data> queryGroupBuildFee() {
        logger.info("queryGroupBuildFee");
        return groupService.queryGroupBuildFee();
    }

    /**
     * Qbao 更新群信息 群昵称，群置顶，免打扰
     * @param groupInfo
     * @return
     */
    @ApiOperation(value = "Qbao 更新群信息 群昵称，群置顶，免打扰", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Group.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/updateGroupInfo", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GroupInfo updateGroupInfo(@RequestBody GroupInfo groupInfo) {
        logger.info("updateGroupInfo");
        return groupService.updateGroupInfo(groupInfo);
    }

    /**
     * Qbao 设置 进群验证
     * @param
     * @return
     */
    @ApiOperation(value = "Qbao 设置 进群验证", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/setGroupConfirmStatus", method = RequestMethod.POST,consumes = "application/json", produces = "application/json")
    public Map setGroupConfirmStatus(@RequestBody Map groupUserInfo) {
        logger.info("setGroupConfirmStatus");

        String groupNo = groupUserInfo.get("groupNo").toString();
        Assert.notNull(groupNo,"groupNo can not be null");
        Object typeOb = groupUserInfo.get("type");
        Integer type = Integer.valueOf(typeOb.toString());
        Assert.notNull(type,"type can not be null");
        Object commandInfo = groupUserInfo.get("commandInfo");
        String commandInfoStr = commandInfo == null?null:(String)commandInfo;

        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();

        groupService.setGroupConfirmStatus(groupNo,accountNo,type,commandInfoStr);
        Map map = new HashMap();
        map.put("result","success");
        return map;
    }

    /**
     * Qbao 处理验证信息
     * @param
     * @return
     */
    @ApiOperation(value = "Qbao 处理验证信息", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/dealWithIdentityInfo", method = RequestMethod.POST, produces = "application/json")
    public Map dealWithIdentityInfo(@RequestBody Map groupUserInfo) {
        logger.info("dealWithIdentityInfo");
        String groupNo = groupUserInfo.get("groupNo").toString();
        Assert.notNull(groupNo,"groupNo can not be null");
        Object typeObj = groupUserInfo.get("type");
        Assert.notNull(typeObj,"type can not be null");
        Integer type = Integer.valueOf(typeObj.toString());
        String accountNo = groupUserInfo.get("accountNo").toString();
        Assert.notNull(accountNo,"accountNo can not be null");

        String operator = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String operator = accountHeader.getAccountNo();
        groupService.dealWithIdentityInfo(groupNo,accountNo,type,operator);

        Map map = new HashMap();
        map.put("result","success");
        return map;
    }

    /**
     * Qbao 获取群分享URL
     * @param
     * @return
     */
    @ApiOperation(value = "Qbao 获取群分享URL", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getGroupShareUrl", method = RequestMethod.GET, produces = "application/json")
    public Map getGroupShareUrl(@RequestParam String groupNo) {
        logger.info("getGroupShareUrl");
        Assert.notNull(groupNo,"groupNo can not be null");
        String language = LocaleContextHolder.getLocale().getLanguage();
        String groupShareUrl = groupService.getGroupShareUrl(groupNo,language);
        Map map = new HashMap();
        map.put("result",groupShareUrl);
        return map;
    }

    /**
     * Qbao 获取群分享内容
     * @param
     * @return
     */
    @ApiOperation(value = "Qbao 获取群分享内容", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getGroupShareUrlInfo", method = RequestMethod.GET, produces = "application/json")
    public Map getGroupShareUrlInfo(@RequestParam String groupNo) {
        logger.info("getGroupShareUrlInfo");
        Assert.notNull(groupNo,"groupNo can not be null");

        Map groupShareUrlInfo = groupService.getGroupShareUrlInfo(groupNo);
        Map map = new HashMap();
        map.put("result","success");
        map.put("result",groupShareUrlInfo);
        return map;
    }
}
