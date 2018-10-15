package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.GroupDao;
import com.aethercoder.core.dao.GroupMemberDao;
import com.aethercoder.core.dao.RedPacketEventDao;
import com.aethercoder.core.dao.batch.AutoCreateRedPacketBatch;
import com.aethercoder.core.entity.event.RedPacketEvent;
import com.aethercoder.core.entity.social.Group;
import com.aethercoder.core.entity.social.GroupMember;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.core.service.RedPacketEventService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.util.DateUtil;
import com.aethercoder.foundation.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2018/1/4 下午3:29
 */
@Service
public class RedPacketEventServiceImpl implements RedPacketEventService {

    @Autowired
    private RedPacketEventDao redPacketEventDao;

    @Autowired
    private BatchService batchService;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private GroupDao groupDao;


    @Override
    public RedPacketEvent saveRedPacketEvent(RedPacketEvent redPacketEvent) {
        //check 红包标语不包含表情
        if (null != redPacketEvent.getRedComment() && StringUtil.containsEmoji(redPacketEvent.getRedComment())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }
        GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndRoleAndIsDeletedFalse(redPacketEvent.getGroupNo(), WalletConstants.GROUP_ROLE_HOSTER);
        if (groupMember == null) {
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        }
        validation(redPacketEvent);
        RedPacketEvent redPacketEvent1 = redPacketEventDao.save(redPacketEvent);
        //获取开始时间与结束时间相差N天
        Integer dayNumber = DateUtil.differentDays(redPacketEvent1.getStartTime(), redPacketEvent1.getEndTime());
        //日循环
        Date startTime = redPacketEvent1.getExpireTime();
        for (int i = 0; i <= dayNumber; i += redPacketEvent.getDaily()) {
            for (int j = 0; j < redPacketEvent1.getNumber(); j++) {
                batchService.createBatchTask("saveRedPacketEvent" + (Integer.sum(i * redPacketEvent1.getNumber(), j)), startTime, AutoCreateRedPacketBatch.class.getName(), redPacketEvent.getClass().getSimpleName(), redPacketEvent.getId());
                //开始时间+=每分钟
                startTime = DateUtil.addDateMinut(startTime, redPacketEvent1.getMinutes());
            }
            startTime = DateUtil.addDateMinut(startTime, redPacketEvent.getDaily() * 24 * 60);
        }
        return redPacketEvent1;
    }

    @Override
    public RedPacketEvent getRedPacketEventInfo(Long id) {
        return redPacketEventDao.findOne(id);
    }

    @Override
    public void updateRedPacketEvent(RedPacketEvent redPacketEvent) {
        RedPacketEvent redPacketEvent1 = redPacketEventDao.findOne(redPacketEvent.getId());
        if (redPacketEvent1.getIsDeleted()) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }
        redPacketEvent1.setIsDeleted(true);
        redPacketEventDao.save(redPacketEvent1);
        batchService.deleteBatchTasks(redPacketEvent.getClass().getSimpleName(), redPacketEvent1.getId());

    }

    @Override
    public Page<RedPacketEvent> findAllRedPacketEvent(Integer page, Integer size, String groupNo, Long unit, String beginDate, String endDate, Boolean isDeleted) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<RedPacketEvent> accounts = redPacketEventDao.findAll(new Specification<RedPacketEvent>() {
            @Override
            public Predicate toPredicate(Root<RedPacketEvent> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();

                //群No
                if (!StringUtils.isEmpty(groupNo)) {
                    list.add(criteriaBuilder.like(root.get("groupNo").as(String.class), groupNo + "%"));
                }
                if (null != unit && !"".equals(unit)) {
                    list.add(criteriaBuilder.equal(root.get("unit").as(Long.class), unit));
                }
                if (StringUtils.isNotBlank(beginDate)) {
                    //大于或等于传入时间
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startTime").as(Date.class), DateUtil.stringToDate(beginDate)));
                }
                if (StringUtils.isNotBlank(endDate)) {
                    //小于或等于传入时间
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("endTime").as(Date.class), DateUtil.stringToDate(endDate)));
                }
                if (null != isDeleted && !"".equals(isDeleted)) {
                    list.add(criteriaBuilder.equal(root.get("isDeleted").as(Boolean.class), isDeleted));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        accounts.forEach(redPacketEvent -> {
            Group group = groupDao.findGroupByGroupNo(redPacketEvent.getGroupNo());
            if (group != null) {
                redPacketEvent.setName(group.getName());
            }
        });

        return accounts;
    }

    private static void validation(RedPacketEvent redPacketEvent) {
        //check 日期间隔 0-30
        //check 红包间隔每分钟 0-60
        //check 日期间隔每分钟N次 0<N
        //check 红包数量
        if (redPacketEvent.getDaily() < 1 || redPacketEvent.getDaily() > 30) {
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        } else if (redPacketEvent.getMinutes() < 1 || redPacketEvent.getMinutes() > 60) {
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        } else if (redPacketEvent.getNumber() < 0) {
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        } else if (redPacketEvent.getRedNumber() < 0 || redPacketEvent.getRedNumber() > 1000) {
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        } else if (redPacketEvent.getAmount().compareTo(new BigDecimal(0)) <= 0) {
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        }


    }

}
