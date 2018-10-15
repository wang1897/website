package com.aethercoder.core.service;

import com.aethercoder.core.entity.social.GroupManager;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2017/12/6 上午11:30
 */
public interface GroupManagerService {

    GroupManager saveGroupManager(GroupManager groupManager);

    void updateGroupManager(List<GroupManager> groupManager);

    Page<GroupManager> findGroupManagers(Integer page, Integer size, String accountNo, Integer transferStatus, Integer gearType);

    GroupManager findByAccountNo(String accountNo);


}
