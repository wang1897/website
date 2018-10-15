package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.GroupManagerDao;
import com.aethercoder.core.entity.social.GroupManager;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.GroupManagerService;
import com.aethercoder.core.service.SysConfigService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.DateUtil;
import com.aethercoder.foundation.util.StringUtil;
import org.springframework.beans.BeanUtils;
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
 * @date 2017/12/6 上午11:30
 */
@Service
public class GroupManagerServiceImpl implements GroupManagerService {

    @Autowired
    private GroupManagerDao groupManagerDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SysConfigService sysConfigService;


    @Override
    public GroupManager saveGroupManager(GroupManager groupManager) {
        //招募活动是否过期
        Date endTime = DateUtil.stringToDate(sysConfigService.findSysConfigByName(WalletConstants.GROUP_MANAGER_END_TIME).getValue());
        if(!DateUtil.dateCompare(new Date(),endTime)){
            throw new AppException(ErrorCode.EVENT_IS_OVER);
        }
        ///校验fundUserName是否包含表情
        if (null != groupManager.getGroupName() && StringUtil.containsEmoji(groupManager.getGroupName())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }
        GroupManager groupManager1 = findByAccountNo(groupManager.getAccountNo());
        if (groupManager1 != null) {
            throw new AppException(ErrorCode.APPLY_FOR);
        }
        groupManager.setGearType(WalletConstants.NOT_GEAR);//无奖励
        groupManager.setTransferStatus(WalletConstants.NOT_TRANSFER);//未转账
        //校验地址
        if (null == groupManager.getAddress() || groupManager.getAddress().isEmpty()) {
            groupManager.setAddress(accountService.getMainAddress(groupManager.getAccountNo()));
        } else {
            Boolean flag = accountService.checkAddress(groupManager.getAccountNo(), groupManager.getAddress());
            //该用户没有该地址
            if (!flag) {
                throw new AppException(ErrorCode.NO_ADDRESS);
            }
        }

        return groupManagerDao.save(groupManager);
    }

    @Override
    public void updateGroupManager(List<GroupManager> groupManagerList) {

        for (GroupManager groupManager : groupManagerList) {
            GroupManager groupManager1 = groupManagerDao.findOne(groupManager.getId());
            Boolean flag = accountService.checkAddress(groupManager.getAccountNo(), groupManager.getAddress());
            //该用户没有该地址
            if (!flag) {
                throw new AppException(ErrorCode.NO_ADDRESS);
            }
            BeanUtils.copyProperties(groupManager, groupManager1, "accountNo");
            groupManagerDao.save(groupManager1);
        }

    }

    @Override
    public Page<GroupManager> findGroupManagers(Integer page, Integer size, String accountNo, Integer transferStatus, Integer gearType) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<GroupManager> groupManagers = groupManagerDao.findAll(new Specification<GroupManager>() {
            @Override
            public Predicate toPredicate(Root<GroupManager> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != accountNo && !"".equals(accountNo)) {
                    list.add(criteriaBuilder.equal(root.get("accountNo").as(String.class), accountNo));
                }
                if (null != transferStatus && !"".equals(transferStatus)) {
                    list.add(criteriaBuilder.equal(root.get("transferStatus").as(Integer.class), transferStatus));
                }
                if (null != gearType && !"".equals(gearType)) {
                    list.add(criteriaBuilder.equal(root.get("gearType").as(Integer.class), gearType));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return groupManagers;
    }

    @Override
    public GroupManager findByAccountNo(String accountNo) {
        GroupManager groupManager1 = groupManagerDao.findByAccountNo(accountNo);
        return groupManager1;
    }
}
