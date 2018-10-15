package com.aethercoder.core.service;

import com.aethercoder.core.entity.social.GroupNotice;
import org.springframework.data.domain.Page;

/**
 * @auther Guo Feiyan
 * @date 2017/12/18 下午6:51
 */
public interface GroupNoticeService {
    GroupNotice saveGroupNotice(GroupNotice groupNotice);

    GroupNotice findById(Long id);

    GroupNotice getLatestNoticeInfo(String groupNo);

    Page<GroupNotice> findGroupNoticesByGroupNo(Integer page, Integer size, String groupNo, String title);

    void deleteNotice(Long id, String groupNo, String createBy);
}
