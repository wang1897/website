package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.dao.GroupDao;
import com.aethercoder.core.dao.GroupTagsDao;
import com.aethercoder.core.entity.social.Group;
import com.aethercoder.core.entity.social.GroupTags;
import com.aethercoder.core.service.GroupOfTagsService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2018/1/3 下午2:49
 */
@Service
public class GroupOfTagsServiceImpl implements GroupOfTagsService {

    @Autowired
    private GroupTagsDao groupTagsDao;
    @Autowired
    private GroupDao groupDao;

    @Override
    public List<GroupTags> saveGroupTags(List<GroupTags> groupTags) {

        GroupTags groupTags1 = groupTagsDao.findFirstByIsDeleteFalseOrderBySequenceDesc();
        Integer max = groupTags1 == null ? 0 : groupTags1.getSequence() + 1;
        for (int i = 0; i < groupTags.size(); i++) {
            if (StringUtils.isBlank(groupTags.get(i).getName())) {
                throw new AppException(ErrorCode.GROUP_TAGS_NAME_CHECK);
            }
            GroupTags gas = groupTagsDao.findGroupTagsByNameAndAndIsDeleteFalse(groupTags.get(i).getName());
            if (gas != null) {
                String[] strs = new String[]{groupTags.get(i).getName()};
                throw new AppException(ErrorCode.GROUP_TAGS_IS_EXIST, strs);
            }
            groupTags.get(i).setSequence(max + i);

        }
        return groupTagsDao.save(groupTags);
    }


    @Override
    public void updateGroupSequence(Long[] tagsIdList) {
        if (tagsIdList == null || tagsIdList.length == 0) {
            return;
        }
        List<GroupTags> groupTagsList = new ArrayList<>();
        for (int i = 0; i < tagsIdList.length; i++) {
            GroupTags groupTags = groupTagsDao.findGroupTagsByIdAndIsDeleteFalse(tagsIdList[i]);
            groupTags.setSequence(i);
            groupTagsList.add(groupTags);
        }
        groupTagsDao.save(groupTagsList);
    }

    @Override
    @Transactional
    public void updateGroupName(GroupTags groupTags) {

        GroupTags groupTagsUpd = groupTagsDao.findOne(groupTags.getId());
        if (groupTagsUpd != null && groupTags.getIsDelete().equals(false)) {
            GroupTags gas = groupTagsDao.findGroupTagsByNameAndAndIsDeleteFalse(groupTags.getName());
            if (gas != null) {
                String[] strs = new String[]{groupTags.getName()};
                throw new AppException(ErrorCode.GROUP_TAGS_IS_EXIST, strs);
            }
            groupTagsUpd.setName(groupTags.getName());
            groupTagsDao.save(groupTagsUpd);
            List<Group> groupList = groupDao.findGroupsByTagAndIsDeletedFalse(groupTagsUpd.getName());
            groupList.forEach(group1 -> {
                group1.setTag(groupTagsUpd.getName());
                groupDao.save(group1);
            });
        } else {
            throw new AppException(ErrorCode.GROUP_TAGS_NOT_EXIST);
        }

    }

    @Override
    @Transactional
    public void deleteGroupTags(String[] ids) {
        if (ids.length > 0) {
            for (int i = 0; i < ids.length; i++) {
                GroupTags groupTags = groupTagsDao.findOne(Long.parseLong(ids[i].toString()));
                if (groupTags != null && groupTags.getIsDelete().equals(false)) {
                    groupTags.setIsDelete(true);
                    groupTagsDao.save(groupTags);
                        List<Group> groupList = groupDao.findGroupsByTagAndIsDeletedFalse(groupTags.getName());
                        groupList.forEach(group1 -> {
                            group1.setTag("其他");
                            groupDao.save(group1);
                        });
                } else {
                    throw new AppException(ErrorCode.GROUP_TAGS_NOT_EXIST);
                }
            }

        }
    }

    @Override
    public List<GroupTags> findGroupTagsAllAndGroups() {
        List<GroupTags> groupTagsList = new ArrayList<GroupTags>();
        List list = groupTagsDao.findGroupTags();
        for (int i = 0; i < list.size(); i++) {
            Object[] objs = (Object[]) list.get(i);
            GroupTags groupTags = new GroupTags();
            groupTags.setId(Long.parseLong(objs[0].toString()));
            groupTags.setName(objs[1].toString());
            groupTags.setGroupCounts(Integer.parseInt(objs[2].toString()));
            groupTagsList.add(groupTags);
        }
        return groupTagsList;
    }

    @Override
    public List<GroupTags> findAllGroupTags() {
        return groupTagsDao.findAllByIsDeleteFalse();
    }
}
