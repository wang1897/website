package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.social.GroupManager;
import com.aethercoder.core.service.GroupManagerService;
import com.aethercoder.foundation.service.LocaleMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2017/12/6 下午2:40
 */
@RestController
@RequestMapping("groupManager")
@Api(tags = "groupManager", description = "申请成为群主接口管理")
public class GroupManagerController {

    private static Logger logger = LoggerFactory.getLogger(GroupManagerController.class);

    @Autowired
    private GroupManagerService groupManagerService;

    public LocaleMessageService localeMessageUtil;

    /**
     * 申请成为群主
     *
     * @return
     * @throws Throwable
     */

    @RequestMapping(value = "/saveGroupManager", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ApiOperation(value = "申请成为群主", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupManager.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    public Map<String, Object> saveGroupManager(@RequestBody GroupManager groupManager) {
        logger.info("saveGroupManager");
        Map<String, Object> map = new HashMap<>();
        groupManagerService.saveGroupManager(groupManager);
        map.put("result", localeMessageUtil.getLocalMessage("GROUPMANAGER_SUCCESS", null));
        return map;
    }

    /**
     * Qbao-修改群主信息
     *
     * @return
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 修改群主信息", notes = "QBAO id  不可修改")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupManager.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateGroupManager", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map<String, Object> updateGroupManager(@RequestBody List<GroupManager> groupManager) {

        logger.info("admin/updateGroupManager");
        Map<String, Object> map = new HashMap<>();
        groupManagerService.updateGroupManager(groupManager);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao_后台 查询群主信息+分页
     * @param page
     * @param size
     * @return
     */
    @ApiOperation(value = "Qbao_后台 钱包用户查询+分页", notes = "params:page=当前页(从0开始) size=当前显示数量")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GroupManager.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findGroupManagersByPage", method = RequestMethod.GET, produces = "application/json")
    public Page<GroupManager> findGroupManagers(Integer page, Integer size, String accountNo, Integer transferStatus, Integer gearType) {
        logger.info("findGroupManagersByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return groupManagerService.findGroupManagers(page, size, accountNo, transferStatus, gearType);
    }

}
