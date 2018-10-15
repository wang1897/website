package com.aethercoder.core.dao.batch;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.TokenCalendarDao;
import com.aethercoder.core.entity.media.TokenCalendar;
import com.aethercoder.core.util.PushUtil;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.foundation.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther jiawei.tao
 * @date 2018/2/23 下午1:38
 */
@Service
public class PushTokenCalendarBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(PushTokenCalendarBatch.class);

    @Autowired
    private TokenCalendarDao tokenCalendarDao;

    @Autowired
    private PushUtil pushUtil;

    @Value("${jPush.languageZh}")
    private String languageZh;
    @Value("${jPush.languageEn}")
    private String languageEn;
    @Value("${jPush.languageKo}")
    private String languageKo;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("PushTokenCalendarBatch start");

        TokenCalendar tokenCalendar = tokenCalendarDao.findOne(task.getResourceId());

        //推送内容
        String message = tokenCalendar.getContent();
        //扩展字段
        String type = WalletConstants.JPUSH_TYPE_CANLADER;
        //String url = "";
        //String groupNo = "";
        //String gameId = "";
        //语言
        String languageType = tokenCalendar.getLanguageType();
        List tagList = new ArrayList();
        if (WalletConstants.LANGUAGE_TYPE_ZH.equals(languageType)){
            tagList.add(languageZh);
        }else if (WalletConstants.LANGUAGE_TYPE_KO.equals(languageType)){
            tagList.add(languageKo);
        }else {
            tagList.add(languageEn);
        }
        int i = DateUtil.differentDays(task.getExpireTime(), new Date());
        if (i >= 0 && i < 1){
            pushUtil.sendTag(message,type,null,null,null,tagList);
        }
        BatchResult successResult = task.getSuccessResult("success");
        logger.info("PushTokenCalendarBatch end");
        return successResult;
    }
}
