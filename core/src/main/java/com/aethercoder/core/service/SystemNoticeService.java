package com.aethercoder.core.service;

import com.aethercoder.core.entity.social.SystemNotice;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 *@author lilangfeng
 * @date 2018/01/08
 */
public interface SystemNoticeService {
    SystemNotice createSystemNotice(SystemNotice systemNotice);
    SystemNotice updateSystemNotice(SystemNotice systemNotice);
    void deleteNotice(Long id);
    List<SystemNotice> findSystemNoticeByLanguage(String language);
    Page<SystemNotice> findSystemNoticesByPage(Integer page, Integer size, String content, Integer status,String languageType);
    Page<SystemNotice> findSystemNoticesByPage(Integer page, Integer size, String languageType);

}
