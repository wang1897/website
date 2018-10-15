package com.aethercoder.core.controller;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.GuessNumberGameDao;
import com.aethercoder.core.entity.guess.GuessNumberGame;
import com.aethercoder.core.entity.guess.GuessRecord;
import com.aethercoder.core.entity.json.BaseWinner;
import com.aethercoder.core.entity.json.GameJson;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.GuessNumberGameService;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guofeiyan on 2018/01/29.
 */

@RestController
@RequestMapping("guessNumber")
@Api(tags = "guessNumber", description = "竞猜游戏接口管理")
public class GuessNumberGameController {

    private static Logger logger = LoggerFactory.getLogger(GuessNumberGameController.class);

    @Autowired
    private GuessNumberGameService guessNumberGameService;

    @Autowired
    private GuessNumberGameDao guessNumberGameDao;

    /**
     * Qbao-后台 创建竞猜游戏
     *
     * @param guessNumberGame
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-后台 创建竞猜游戏", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveGuessNumberGame", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public GuessNumberGame saveGuessNumberGame(@RequestBody GuessNumberGame guessNumberGame) {
        logger.info("saveGuessNumberGame");
        Assert.notNull(guessNumberGame.getBeginBlock(),"beginBlock not  null");
        Assert.notNull(guessNumberGame.getEndBlock(),"endBlock not  null");
        Assert.notNull(guessNumberGame.getLuckBlock(),"luckBlock not  null");
        Assert.notNull(guessNumberGame.getZhName(),"name not  null");
        return guessNumberGameService.saveGuessNumberGame(guessNumberGame);
    }

    /**
     * Qbao-后台 修改竞猜游戏
     *
     * @param guessNumberGame
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-后台 修改竞猜游戏", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateGuessNumberGame", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map updateGuessNumberGame(@RequestBody GuessNumberGame guessNumberGame) {
        logger.info("updateGuessNumberGame");
        Map map = new HashMap();
        Assert.notNull(guessNumberGame.getId(),"id not  null");
        guessNumberGameService.updateGuessNumberGame(guessNumberGame);
        map.put("result", "success");
        return map;
    }

    /**
     * Qbao-后台 设置开奖数字和时间
     *
     * @param guessNumberGame
     * @return event
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-后台 开奖", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateAward", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public Map updateAward(@RequestBody GuessNumberGame guessNumberGame) {
        logger.info("updateAward");
        Map map = new HashMap();
        guessNumberGameService.updateAward(guessNumberGame);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao-后台 查询所有游戏分类
     * @return
     */
    @ApiOperation(value = "Qbao-后台 查询所有游戏分类", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/findAllGuessNumberGame", method = RequestMethod.GET, produces = "application/json")
    public Page<GuessNumberGame> findAllGuessNumberGame(Integer page, Integer size, Boolean isDelete, Boolean isShow,Long gameId) {

        logger.info("findAllGame");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return guessNumberGameService.findGuessNumberGames(page, size, isShow,gameId);

    }

    /***
     * Qbao 获取有效竞猜游戏
     * @return
     */
    @ApiOperation(value = "Qbao 获取有效竞猜游戏", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GameJson.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/getAvailGameInfo", method = RequestMethod.GET, produces = "application/json")
    public GameJson getAvailGameInfo(String accountNo,Long id) {
        logger.info("getAvailGameInfo");
        return guessNumberGameService.getAvailGameInfo(accountNo,id);
    }


    /**
     * Qbao 立即参与
     *
     * @param guessRecord
     * @return GuessRecord
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao 用户竞猜游戏", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessRecord.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/guessNumberByAccount", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @Transactional
    public GuessRecord guessNumberByAccount(@RequestBody GuessRecord guessRecord) {
        logger.info("guessNumberByAccount");

        return guessNumberGameService.guessNumberByAccount(guessRecord);

    }

    /***
     * Qbao 获取竞猜活动列表
     * @return
     */
    @ApiOperation(value = "Qbao 获取竞猜活动列表", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/getGuessNumberGamePerson", method = RequestMethod.GET, produces = "application/json")
    public List<GuessNumberGame> getGuessNumberGamePerson(Long gameId) {
        logger.info("getGuessNumberGamePerson");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return guessNumberGameService.getGuessNumberGamePerson(accountNo,gameId);

    }

    /***
     * Qbao 后台 获取中奖名单
     * @return
     */
    @ApiOperation(value = "Qbao 后台 获取中奖名单", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/getWinnerList", method = RequestMethod.GET, produces = "application/json")
    public Map getWinnerListByAdmin(Integer page,Integer size, Long guessNumberId, Long unit) {
        logger.info("/admin/getWinnerList");
        Assert.notNull(guessNumberId,"guessNumberId not  null");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return guessNumberGameService.getWinnerListByAdmin(page,size,guessNumberId,unit);
    }

    /***
     * Qbao H5 获取中奖名单
     * @return
     */
    @ApiOperation(value = "Qbao H5 获取中奖名单", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getWinnerList", method = RequestMethod.GET, produces = "application/json")
    public BaseWinner getWinnerList(@RequestParam Long guessNumberId) {
        logger.info("getWinnerList");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(guessNumberId,"guessNumberId not  null");
        Assert.notNull(accountNo,"accountNo not  null");
        return guessNumberGameService.getWinnerList(guessNumberId,accountNo);
    }

    /***
     * H5 获取最近一期获奖名单
     * @return
     */
    @ApiOperation(value = "H5 获取最近一期获奖名单", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getRecentlyWinnerList", method = RequestMethod.GET, produces = "application/json")
    public BaseWinner getRecentlyWinnerList(@RequestParam Long gameId) {
        logger.info("getRecentlyWinnerList");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        Assert.notNull(gameId,"gameId not  null");
        Assert.notNull(accountNo,"accountNo not  null");
        return guessNumberGameService.getRecentlyWinnerList(gameId,accountNo);
    }


    /***
     * Qbao-后台 删除竞猜
     * @return
     */
    @ApiOperation(value = "Qbao-后台 删除竞猜", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/admin/deleteGuessNumberGameById", method = RequestMethod.GET, produces = "application/json")
    public Map deleteGameById(@RequestParam Long id) {
        logger.info("deleteGuessNumberGameById");
        Map map = new HashMap();
        guessNumberGameService.deleteGuessNumberGame(id);
        map.put("result", "success");
        return map;

    }

    /***
     * Qbao 测试
     * @return
     */
    @ApiOperation(value = "Qbao 测试", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessNumberGame.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/testDummyLottery", method = RequestMethod.GET, produces = "application/json")
    public Map testDummyLottery() {
        logger.info("testDummyLottery");

        Map map = new HashMap();
        guessNumberGameService.testDummyLottery();
        map.put("result", "success");
        return map;

    }


}
