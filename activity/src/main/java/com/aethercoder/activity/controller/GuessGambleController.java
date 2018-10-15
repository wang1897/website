package com.aethercoder.activity.controller;

import com.aethercoder.activity.entity.guessGamble.GambleRank;
import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import com.aethercoder.activity.entity.guessGamble.JoinGamble;
import com.aethercoder.activity.service.GuessGambleService;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.wallet.Account;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hepengfei on 27/02/2018.
 */
@RestController
@RequestMapping("activity/guessGamble")
@Api(tags = "guessGamble", description = "竞猜赌博接口")
public class GuessGambleController {
    private static Logger logger = LoggerFactory.getLogger(GuessGambleController.class);

    @Autowired
    private GuessGambleService guessGambleService;

    /**
     * Qbao—后台 添加投注小游戏
     *
     * @param guessGamble
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-后台 添加游戏分类", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessGamble.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/saveGuessGamble", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public GuessGamble saveGuessGamble(@RequestBody GuessGamble guessGamble) {
        logger.info("saveGuessGamble");
        Assert.notNull(guessGamble.getTitle(), "title not null");
        Assert.notNull(guessGamble.getContent(), "content not null");
        Assert.notNull(guessGamble.getAmount(), "amount not null");
        Assert.notNull(guessGamble.getGameId(), "gameId not null");
        return guessGambleService.saveGuessGamble(guessGamble);
    }

    /**
     * Qbao—后台 修改投注小游戏
     *
     * @param guessGamble
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-后台 修改投注小游戏", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessGamble.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/updateGuessGamble", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map updateGuessGamble(@RequestBody GuessGamble guessGamble) {
        logger.info("updateGuessGamble");
        Assert.notNull(guessGamble.getId(), "id not null");
        guessGambleService.updateGuessGamble(guessGamble);
        Map map = new HashMap();
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao-后台 查询所有投注小游戏
     * @return
     */
    @ApiOperation(value = "Qbao-后台 查询所有投注小游戏", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessGamble.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/findGuessGamblesByPage", method = RequestMethod.GET, produces = "application/json")
    public Page<GuessGamble> findGuessGamblesByPage(Integer page, Integer size,Long gameId) {

        logger.info("findGuessGamblesByPage");
        if (null == page) {
            page = WalletConstants.DEFAULT_PAGE;
        }
        if (null == size) {
            size = WalletConstants.DEFAULT_PAGE_SIZE;
        }
        return guessGambleService.findGuessGamblesByPage(page, size,gameId);

    }

    /***
     * Qbao-后台 投注小游戏详情
     * @return
     */
    @ApiOperation(value = "Qbao-后台 投注小游戏详情", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessGamble.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/admin/findGuessGambleInfo", method = RequestMethod.GET, produces = "application/json")
    public GuessGamble findGuessGambleInfo(Long id) {
        logger.info("findGuessGambleInfo");
        Assert.notNull(id, "id not null");
        return guessGambleService.findGuessGambleInfo(id);
    }

    /***
     * Qbao-后台 删除投注小游戏
     * @return
     */
    @ApiOperation(value = "Qbao-后台 删除投注小游戏", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/admin/deleteGuessGamble", method = RequestMethod.GET, produces = "application/json")
    public Map deleteGuessGamble(Long id) {
        logger.info("deleteGuessGamble");
        Assert.notNull(id, "id not null");
        guessGambleService.deleteGuessGamble(id);
        Map map = new HashMap();
        map.put("result", "success");
        return map;
    }

    /**
     * Qbao—后台 投注小游戏开奖
     *
     * @param guessGamble
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-后台 投注小游戏开奖", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/openAward", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map openAward(@RequestBody GuessGamble guessGamble) {
        logger.info("openAward");
        Assert.notNull(guessGamble.getId(), "id not null");
        Assert.notNull(guessGamble.getLuckOption(), "option not null");
        guessGambleService.openAward(guessGamble);
        Map map = new HashMap();
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao-h5 投注小游戏列表
     * @return
     */
    @ApiOperation(value = "Qbao-h5 投注小游戏列表", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/getGuessGambleList", method = RequestMethod.GET, produces = "application/json")
    public Map getGuessGambleList(@RequestParam Long gameId, Integer page, Integer size) {
        logger.info("getGuessGambleList");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}

        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();

        return  guessGambleService.getGuessGambleList(gameId,accountNo,page,size);
    }

    /***
     * Qbao-h5 投注小游戏详情
     * @return
     */
    @ApiOperation(value = "Qbao-h5 投注小游戏详情", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessGamble.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/getGuessGambleInfo", method = RequestMethod.GET, produces = "application/json")
    public Map getGuessGambleInfo(@RequestParam Long id) {
        logger.info("getGuessGambleInfo");
        Assert.notNull(id, "id not null");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return guessGambleService.getGuessGambleInfo(id,accountNo);
    }

    /***
     * Qbao-h5 投注小游戏统计结果详情
     * @return
     */
    @ApiOperation(value = "Qbao-h5 投注小游戏统计结果详情", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessGamble.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/getGuessResultInfo", method = RequestMethod.GET, produces = "application/json")
    public Map getGuessResultInfo(@RequestParam Long id) {
        logger.info("getGuessResultInfo");
        Assert.notNull(id, "id not null");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        return guessGambleService.getGuessResultInfo(id,accountNo);
    }

    /**
     * Qbao—h5 参与投注小游戏
     *
     * @param joinGamble
     * @throws Throwable
     */
    @ApiOperation(value = "Qbao-h5 参与投注小游戏", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessGamble.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/joinGamble", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map joinGamble(@RequestBody JoinGamble joinGamble) {
        logger.info("joinGamble");
        Assert.notNull(joinGamble.getAmount(), "amount not null");
        Assert.notNull(joinGamble.getGambleId(), "gambleId not null");
        Assert.notNull(joinGamble.getOption(), "option not null");
        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();
        joinGamble.setAccountNo(accountNo);
        guessGambleService.joinGamble(joinGamble);
        Map map = new HashMap();
        map.put("result", "success");
        return map;
    }


    /***
     * Qbao-h5 获取个人参与投注小游戏列表
     * @return
     */
    @ApiOperation(value = "Qbao-h5 获取个人参与投注小游戏列表", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GuessGamble.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/getJoinGamblePerson", method = RequestMethod.GET, produces = "application/json")
    public Map getJoinGamblePerson(@RequestParam Long gameId, Integer page, Integer size) {
        logger.info("getJoinGambleInfo");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}

        String accountNo = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("accountNo");
//        String accountNo = accountHeader.getAccountNo();

        return guessGambleService.getJoinGamblePerson(accountNo,gameId,page,size);
    }

    /***
     * Qbao-h5 投注小游戏榜单
     * @return
     */
    @ApiOperation(value = "Qbao-h5 投注小游戏榜单", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = GambleRank.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )

    @RequestMapping(value = "/getJoinGambleRank", method = RequestMethod.GET, produces = "application/json")
    public Map getJoinGambleRank(@RequestParam Long gameId) {
        logger.info("getJoinGambleRank");
        return guessGambleService.getJoinGambleRank(gameId);
    }


    /**
     * Qbao 竞猜小游戏分享
     *
     * @return
     * @throws Throwable
     */

    @ApiOperation(value = "竞猜小游戏分享Url", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getGambleGameShareUrl", method = RequestMethod.GET, produces = "application/json")
    public Map getGambleGameShareUrl(@RequestParam Long gambleId ) {
        logger.info("getGambleGameShareUrl");
        Assert.notNull(gambleId, "gambleId can not be null");
        String language = LocaleContextHolder.getLocale().getLanguage();
        String url = guessGambleService.getGambleGameShareUrl(gambleId, language);
        Map map = new HashMap<>();
        map.put("result", "success");
        map.put("url", url);
        return map;
    }

    @ApiOperation(value = "竞猜小游戏分享Params", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Map.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getGambleGameShareParams", method = RequestMethod.GET, produces = "application/json")
    public Map getGambleGameShareParams(@RequestParam Long gambleId ) {
        logger.info("getGambleGameShareParams");
        Assert.notNull(gambleId, "gambleId can not be null");
        String language = LocaleContextHolder.getLocale().getLanguage();
        return guessGambleService.getGambleGameShareParams(gambleId, language);

    }

}
