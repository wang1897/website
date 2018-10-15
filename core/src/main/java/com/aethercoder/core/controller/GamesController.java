package com.aethercoder.core.controller;


import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.guess.Games;
import com.aethercoder.core.service.GamesService;
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
 * Created by guofeiyan on 2018/01/29.
 */
@RestController
@RequestMapping("games")
@Api(tags = "games", description = "所有游戏接口管理")
public class GamesController {

    private static Logger logger = LoggerFactory.getLogger(GamesController.class);

    @Autowired
    private GamesService gamesService;

    /**
     * Qbao—后台 添加游戏分类
     *
     * @param games
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-后台 添加游戏分类", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Games.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveGame", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Games saveGame(@RequestBody Games games) {
        logger.info("saveGame");
        Assert.notNull(games.getZhName(), "zhName not null");
        Assert.notNull(games.getZhUrl(), "zhUrl not null");
        Assert.notNull(games.getBanner(), "banner not null");
        return gamesService.saveGame(games);
    }

    /**
     * Qbao-后台 修改游戏分类
     *
     * @param games
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-后台 添加游戏分类", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Games.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateGame", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map updateGame(@RequestBody Games games) {

        logger.info("/admin／updateGame");
        Map map = new HashMap();
        gamesService.updateGame(games);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao-后台 查询所有游戏分类
     * @return
     */
    @ApiOperation(value = "Qbao-后台 查询所有游戏分类", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Games.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/findAllGame", method = RequestMethod.GET, produces = "application/json")
    public Page<Games> findAllGame(Integer page, Integer size, Boolean isShow) {

        logger.info("findAllGame");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return gamesService.findGamesAll(page, size, isShow,null);

    }

    /***
     * Qbao 获取有效游戏列表
     * @return
     */
    @ApiOperation(value = "Qbao 获取有效游戏列表", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Games.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/findActivatedGame", method = RequestMethod.GET, produces = "application/json")
    public Page<Games> findActivatedGame(Integer page, Integer size) {

        logger.info("findActivatedGame");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return gamesService.findActivatedGame(page, size);

    }

    /***
     * Qbao-后台 删除游戏分类
     * @return
     */
    @ApiOperation(value = "Qbao-后台 删除游戏分类", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Games.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/admin/deleteGameById", method = RequestMethod.GET, produces = "application/json")
    public Map deleteGameById(@RequestParam Long id,String method) {
        logger.info("deleteGameById");
        Map map = new HashMap();
        gamesService.deleteGame(id,method);
        map.put("result", "success");
        return map;

    }

}
