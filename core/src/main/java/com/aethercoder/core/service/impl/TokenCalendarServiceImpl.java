package com.aethercoder.core.service.impl;


import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.TokenCalendarDao;
import com.aethercoder.core.dao.batch.PushTokenCalendarBatch;
import com.aethercoder.core.dao.batch.SendRedPacketBatch;
import com.aethercoder.core.entity.media.TokenCalendar;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.SysConfigService;
import com.aethercoder.core.service.TokenCalendarService;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.util.DateUtil;
import com.aethercoder.foundation.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author li langfeng
 * @date 2018/01/24
 */
@Service
public class TokenCalendarServiceImpl implements TokenCalendarService {
    @Autowired
    private TokenCalendarDao tokenCalendarDao;
    @Autowired
    private BatchService batchService;
    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public TokenCalendar createTokenCalendar(TokenCalendar tokenCalendar) {
        StringUtil.isIllegalDBVercharThrowEx(tokenCalendar.getTitle(), 100);
        StringUtil.isIllegalDBVercharThrowEx(tokenCalendar.getContent(), 500);
        tokenCalendar.setIsDelete(false);
        TokenCalendar tokenCalendar1 = tokenCalendarDao.save(tokenCalendar);
        // 如果需要推送 则添加一条推送的批处理
        pushTokenCalendar(tokenCalendar1);
        return tokenCalendar1;
    }

    @Override
    public TokenCalendar updateTokenCalendar(TokenCalendar tokenCalendar) {
        StringUtil.isIllegalDBVercharThrowEx(tokenCalendar.getTitle(), 100);
        StringUtil.isIllegalDBVercharThrowEx(tokenCalendar.getContent(), 500);
        tokenCalendar.setIsDelete(false);
        TokenCalendar tokenCalendar1 = tokenCalendarDao.save(tokenCalendar);
        // 如果需要推送 则添加一条推送的批处理
        pushTokenCalendar(tokenCalendar1);
        return tokenCalendar1;
    }

    private void pushTokenCalendar(TokenCalendar tokenCalendar1) {
        // 先删除所有旧的
        batchService.deleteBatchTasks(tokenCalendar1.getClass().getSimpleName(), tokenCalendar1.getId());
        if (tokenCalendar1.getWillPush()) {

            //获取开始时间与结束时间相差N天
            Integer dayNumber = DateUtil.differentDays(tokenCalendar1.getStartTime(), tokenCalendar1.getEndTime());

            Date endDate = tokenCalendar1.getStartTime();
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(Calendar.MINUTE, -5);

            for (int i = 0; i <= dayNumber; i++) {
                Calendar day = Calendar.getInstance();
                day.setTime(cal.getTime());
                day.add(Calendar.HOUR, i*24);
                batchService.createBatchTask("PushTokenCalendarBatch"+i, day.getTime(),
                        PushTokenCalendarBatch.class.getName(), tokenCalendar1.getClass().getSimpleName(), tokenCalendar1.getId());

            }
        }
    }

    @Override
    public void deleteTokenCalendar(Long id) {
        TokenCalendar tokenCalendar = tokenCalendarDao.findOne(id);
        tokenCalendar.setIsDelete(true);
        tokenCalendarDao.save(tokenCalendar);
        // 先删除所有旧的
        batchService.deleteBatchTasks(tokenCalendar.getClass().getSimpleName(), tokenCalendar.getId());
    }

    @Override
    public TokenCalendar findTokenCalendar(Long id) {
        return tokenCalendarDao.findTokenCalendarByIdAndIsDeleteIsFalse(id);

    }

    @Override
    public List<TokenCalendar> findAllTokenCalendar(String languageType) {
        return tokenCalendarDao.findTokenCalendarByLanguageTypeAndIsDeleteIsFalse(languageType);
    }

    @Override
    public List<TokenCalendar> findTokenCalendarByTime(String languageType, String tokenCalendarTime) {
        List<TokenCalendar> tokenCalendarList = tokenCalendarDao.findByLanguageTypeAndMonths(
                languageType, WalletConstants.TOKEN_CALENDAR_STATUS_APPROVED, tokenCalendarTime, tokenCalendarTime);
        return tokenCalendarList;
    }

    @Override
    public String getTokenCalendarUrl(String eventDate,String language) {
        //获取币月历事件地址
        SysConfig urlConfig = sysConfigService.findSysConfigByName(WalletConstants.TOKEN_CALENDER_EVENT_URL);
        return urlConfig.getValue()+"?shareTime="+eventDate+"&lang="+language;
    }

    @Override
    public List getTokenCalendarEventInfo(String language,String dateStr) {
        dateStr = dateStr + " 00:00:00";
        Date startTimeOfDay = DateUtil.stringToDate(dateStr);
        Date endTimeOfDay = DateUtil.getEndTimeOfDay(startTimeOfDay);

        List<TokenCalendar> tokenCalendarList = tokenCalendarDao.findByLanguageTypeAndIsDeleteIsFalseAndStartTimeIsLessThanEqualAndEndTimeIsGreaterThanEqual(language,endTimeOfDay,startTimeOfDay);
        return tokenCalendarList;
    }
}
