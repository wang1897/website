package com.aethercoder.core.controller;

import com.aethercoder.core.entity.android.Android;
import com.aethercoder.core.service.AndroidService;
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

/**
 * @auther Guo Feiyan
 * @date 2017/9/26 上午11:23
 */

@RestController
@RequestMapping("android")
@Api(tags = "android", description = "安卓版本接口管理")
public class AndroidController {

    private static Logger logger = LoggerFactory.getLogger(AndroidController.class);

    @Autowired
    private AndroidService androidService;

    /***
     * Qbao 查询所有手机版本
     * @return
     */
    @ApiOperation(value = "Qbao 查询所有手机版本", notes = "返回List<Android>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Android.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAndroidAll", method = RequestMethod.GET, produces = "application/json")
    public List<Android> findAndroidAll() {
        logger.info("findAndroidAll");
        return androidService.findAndroidAll();
    }
    /***
     * Qbao 根据id查询手机版本
     * @return
     */
    @ApiOperation(value = "Qbao 根据id查询手机版本", notes = "返回该合约对象Android")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Android.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findAndroidById", method = RequestMethod.GET, produces = "application/json")
    public Android findAndroidById(@RequestParam("id") Long id) {
        logger.info("/admin/findAndroidById");
        Assert.notNull(id," id not null ");
        return androidService.findAndroidById(id);
    }
    /***
     * Qbao_后台 获得最新安卓版本
     * @return
     */
    @ApiOperation(value = "Qbao_后台 获得最新安卓版本", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Android.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAndroidLatest", method = RequestMethod.GET, produces = "application/json")
    public Android findAndroidLatest() {
        logger.info("findAndroidLatest");
        return androidService.findAndroidLatest();
    }
    /***
     * Qbao_后台 获得最新IOS版本
     * @return
     */
    @ApiOperation(value = "Qbao_后台 获得最新IOS版本", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Android.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findIOSLatest", method = RequestMethod.GET, produces = "application/json")
    public Android findIOSLatest() {
        logger.info("findIOSLatest");
        return androidService.findIOSLatest();
    }

    /***
     * Qbao_后台 添加手机版本
     * @return
     */
    @ApiOperation(value = "Qbao_后台 添加手机版本", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Android.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveAndroid", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Android saveAndroid(@RequestBody Android android) {
        logger.info("/admin/saveAndroid");
        Assert.notNull(android, "android not null");
        return androidService.saveAndroid(android);
    }

    /***
     * Qbao_后台 修改手机版本
     * @return
     */
    @ApiOperation(value = "Qbao_后台 修改手机版本", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Android.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateAndroid", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Android updateContract(@RequestBody Android android) {
        logger.info("/admin/updateAndroid");
        Assert.notNull(android.getId(), "id not null");
        return androidService.updateAndroid(android);
    }


    /***
     * Qbao_后台 删除手机版本
     * @return
     */
    @ApiOperation(value = "Qbao_后台 删除手机版本", notes = "参数：id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Android.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteAndroid", method = RequestMethod.GET, produces = "application/json")
    public Map deleteAndroid(@RequestParam Long id) {
        logger.info("/admin/deleteAndroid");
        Assert.notNull(id, "id not null");
        Map<String, Object> map = new HashMap<>();
        androidService.deleteAndroid(id);
        map.put("result","success");
        return map;
    }

}
