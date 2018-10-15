package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.AccountDao;
import com.aethercoder.core.dao.GroupMemberDao;
import com.aethercoder.core.dao.GroupNoticeDao;
import com.aethercoder.core.entity.social.GroupMember;
import com.aethercoder.core.entity.social.GroupNotice;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.GroupNoticeService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2017/12/18 下午6:51
 */
@Service
public class GroupNoticeServiceImpl implements GroupNoticeService {

    @Autowired
    private GroupNoticeDao groupNoticeDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private AccountDao accountDao;



    @Override
    public GroupNotice saveGroupNotice(GroupNotice groupNotice) {
        //check是否是管理员或者群主 或者1002系统管理员
        if((groupNotice.getCreateBy()!= null && !checkRole(groupNotice.getGroupNo(),groupNotice.getCreateBy())) || (groupNotice.getOperator() != null && !groupNotice.getOperator().isEmpty() && !groupNotice.getOperator().equals(WalletConstants.GROUP_SYSTEM))) {
            throw new AppException(ErrorCode.GROUP_BUILD_NO_POWER);
        }
        if (groupNotice.getOperator() != null && !groupNotice.getOperator().isEmpty() && groupNotice.getOperator().equals(WalletConstants.GROUP_SYSTEM)){
            groupNotice.setCreateBy(groupNotice.getOperator());
        }
        StringUtil.isIllegalDBVercharThrowEx(groupNotice.getTitle(),WalletConstants.GROUP_NOTICE_CONTENT_TITLE);
        StringUtil.isIllegalDBVercharThrowEx(groupNotice.getContent(),WalletConstants.GROUP_NOTICE_CONTENT_LENGTH);
        groupNotice.setIsDelete(false);
        groupNotice.setWriteTime(new Date());
        return groupNoticeDao.save(groupNotice);
    }

    @Override
    public GroupNotice findById(Long id) {
        return groupNoticeDao.findGroupNoticeByIdAndIsDeleteFalse(id);
    }

    @Override
    public GroupNotice getLatestNoticeInfo(String groupNo) {
        return groupNoticeDao.findFirstByGroupNoAndIsDeleteFalseOrderByCreateTimeDesc(groupNo);
    }

    @Override
    public Page<GroupNotice> findGroupNoticesByGroupNo(Integer page, Integer size, String groupNo, String title) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<GroupNotice> groupNotices = groupNoticeDao.findAll(new Specification<GroupNotice>() {
            @Override
            public Predicate toPredicate(Root<GroupNotice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();

                if (null != groupNo && !"".equals(groupNo)) {
                    list.add(criteriaBuilder.equal(root.get("groupNo").as(String.class),  groupNo ));
                }
                if (null != title && !"".equals(title)) {
                    list.add(criteriaBuilder.like(root.get("title").as(String.class),  "%"+title+"%"));
                }

                list.add(criteriaBuilder.equal(root.get("isDelete").as(Boolean.class), false ));

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        groupNotices.forEach(groupNotice -> {
            GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNo(groupNotice.getGroupNo(),groupNotice.getCreateBy());
            if (groupNotice.getCreateBy().equals(WalletConstants.GROUP_SYSTEM)){
                Account account = accountDao.findByAccountNo(groupNotice.getCreateBy());
                groupNotice.setAccountName(account.getAccountName());
                groupNotice.setHeader(account.getHeader());
                groupNotice.setRole(WalletConstants.GROUP_ROLE_SYSTEM);
            }
            if (null != groupMember ) {
                groupNotice.setAccountName(groupMember.getDisplayName());
                groupNotice.setHeader(groupMember.getHeaderUrl());
                groupNotice.setRole(groupMember.getRole());
            }
        });
        return groupNotices;
    }

    @Override
    public void deleteNotice(Long id, String groupNo, String createBy) {
        //check是否是管理员或者群主
        if(!checkRole(groupNo,createBy)) {
            throw new AppException(ErrorCode.GROUP_BUILD_NO_POWER);
        }
        groupNoticeDao.delete(id);
    }

    private Boolean checkRole(String groupNo, String createBy){
        //check是否是管理员或者群主
        GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNoAndIsDeletedFalse(String.valueOf(groupNo),createBy);
        if(groupMember != null && ( null == groupMember.getRole() || groupMember.getRole().equals(WalletConstants.GROUP_ROLE_MEMBER))){
            return false;
        }
        return true;
    }
}
