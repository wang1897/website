package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.GamesDao;
import com.aethercoder.core.dao.GuessNumberGameDao;
import com.aethercoder.core.dao.SysConfigDao;
import com.aethercoder.core.entity.guess.Games;
import com.aethercoder.core.entity.guess.GuessNumberGame;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.GamesService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Service
public class GamesServiceImpl implements GamesService {

    @Autowired
    private GamesDao gamesDao;

    @Autowired
    private GuessNumberGameDao guessNumberGameDao;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private LocaleMessageService localeMessageService;

    private  String gametable = "games";
    private String bannerUrlField = "banner";

    @Override
    public Games saveGame(Games games) {
        checkNameAndUnit(games);
        Games games1 = gamesDao.save(games);
        saveBanner(games1);
        return games1;
    }

    private void saveBanner(Games games) {
        if (games.getBanner()!=null){
            localeMessageService.saveMessage(gametable, bannerUrlField, games.getId() + "", WalletConstants.LANGUAGE_TYPE_ZH, games.getBanner());
        }
        if (games.getBannerUrlEn()!=null){
            localeMessageService.saveMessage(gametable, bannerUrlField, games.getId() + "", WalletConstants.LANGUAGE_TYPE_EN, games.getBannerUrlEn());
        }
        if (games.getBannerUrlKo()!=null){
            localeMessageService.saveMessage(gametable, bannerUrlField, games.getId() + "", WalletConstants.LANGUAGE_TYPE_KO, games.getBannerUrlKo());
        }
    }

    @Override
    public void updateGame(Games games) {
        Games games1 = gamesDao.findOne(games.getId());
        if (!games1.getZhName().equals(games.getZhName())) {
            checkNameAndUnit(games);
        }
        gamesDao.save(games);
        saveBanner(games);
    }

    @Override
    public void deleteGame(Long id, String method) {
        if (method != null && WalletConstants.GUESS_NUMBER_METHOD.equals(method)) {
            //删除竞猜
            List<GuessNumberGame> guessNumberGame = guessNumberGameDao.findGuessNumberGamesByGameIdAndIsDeleteFalse(id);
            guessNumberGame.forEach(guessNumberGame1 -> {
                //check 该游戏是已结束 或者未开始
                if (guessNumberGame1 != null && guessNumberGame1.getGameStartTime()!=null && (guessNumberGame1.getLuckNumber()==null
                        || guessNumberGame1.getLuckTime()==null )) {
                    throw new AppException(ErrorCode.EVENT_NOT_UPDATE);
                }
            });
        }
        localeMessageService.deleteMessageByTableFieldId(gametable,bannerUrlField,id+"");
        gamesDao.delete(id);
    }

    @Override
    public Page<Games> findGamesAll(Integer page, Integer size, Boolean isShow, String createTime) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<Games> games = gamesDao.findAll(new Specification<Games>() {
            @Override
            public Predicate toPredicate(Root<Games> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (isShow != null) {
                    list.add(criteriaBuilder.equal(root.get("isShow").as(Boolean.class), isShow));
                }
                if (StringUtils.isNotBlank(createTime)) {
                    //小于或等于传入时间
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime").as(String.class), createTime));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        games.forEach(games1 -> {
            String url_en = localeMessageService.getMessageByTableFieldId(gametable, bannerUrlField, games1.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, WalletConstants.LANGUAGE_TYPE_EN);
            games1.setBannerUrlEn(url_en);
            String url_ko = localeMessageService.getMessageByTableFieldId(gametable, bannerUrlField, games1.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, WalletConstants.LANGUAGE_TYPE_KO);
            games1.setBannerUrlKo(url_ko);
        });
        return games;
    }

    @Override
    public Page<Games> findActivatedGame(Integer page, Integer size) {
        SysConfig sysConfig = sysConfigDao.findSysConfigByName(WalletConstants.SYS_GAME_FLAG);
        String time = null;
        if (!Boolean.valueOf(sysConfig.getValue())) {
            time = WalletConstants.TIMESTAMP_DEFAULT;
        }
        Page<Games> games = findGamesAll(page, size, true, time);
        List<Games> games1 = games.getContent();
        for (int i = 0; i < games1.size(); i++) {
            Games games2 = games1.get(i);
            if (!games2.getIsShow()){
                continue;
            }
            Locale locale = LocaleContextHolder.getLocale();
            String url = localeMessageService.getMessageByTableFieldId(gametable, bannerUrlField, games2.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, locale.getLanguage());
            games2.setBanner(url);
            games2.setUrl(games2.getZhUrl());
        }
        return games;
    }

    private void checkNameAndUnit(Games games) {
        Games games1 = gamesDao.queryGamesByZhName(games.getZhName());
        if (games1 != null && !games1.getId().equals(games.getId())) {
            throw new AppException(ErrorCode.CHECK_GAME_EXIST);
        }
    }

}
