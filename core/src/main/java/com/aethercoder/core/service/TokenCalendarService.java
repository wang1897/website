package com.aethercoder.core.service;

import com.aethercoder.core.entity.media.TokenCalendar;
import java.util.Date;
import java.util.List;

/**
 * @author lilangfeng
 * @date 2018/01/18
 */
public interface TokenCalendarService {
    TokenCalendar createTokenCalendar(TokenCalendar tokenCalendar);

    TokenCalendar updateTokenCalendar(TokenCalendar tokenCalendar);

    void deleteTokenCalendar(Long id);

    TokenCalendar findTokenCalendar(Long id);

    List<TokenCalendar> findAllTokenCalendar(String languageType);

    List<TokenCalendar> findTokenCalendarByTime(String languageType, String tokenCalendarTime);

    String getTokenCalendarUrl(String eventDate,String language);

    List getTokenCalendarEventInfo(String language,String date);
}
