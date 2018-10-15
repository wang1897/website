package com.aethercoder.core.service;

import com.aethercoder.core.entity.social.GroupTags;

import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2018/1/3 下午2:48
 */
public interface GroupOfTagsService {

    List<GroupTags> saveGroupTags(List<GroupTags> groupTags);

    void updateGroupSequence(Long[] tagsIdList);

    void updateGroupName(GroupTags groupTags);

    void deleteGroupTags(String[] id);

    List<GroupTags> findGroupTagsAllAndGroups();

    List<GroupTags> findAllGroupTags();
}
