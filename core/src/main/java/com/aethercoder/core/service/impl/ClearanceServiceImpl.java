package com.aethercoder.core.service.impl;

import com.aethercoder.core.dao.ClearanceDao;
import com.aethercoder.core.dao.ClearanceDetailDao;
import com.aethercoder.core.entity.wallet.Clearance;
import com.aethercoder.core.entity.wallet.ClearanceDetail;
import com.aethercoder.core.service.ClearanceService;
import com.aethercoder.foundation.util.DateUtil;
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
import java.util.*;

@Service
public class ClearanceServiceImpl implements ClearanceService {
    @Autowired
    private ClearanceDao clearanceDao;
    @Autowired
    private ClearanceDetailDao clearanceDetailDao;
    @Override
    public Page<Clearance> findClearanceByPage(Integer page, Integer size, String clearanceDay, Integer type, Long unit, Integer accountStatus, Boolean isClear) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "clearanceDay");
        org.springframework.data.domain.Page<Clearance> clearances = clearanceDao.findAll(new Specification<Clearance>() {
            @Override
            public Predicate toPredicate(Root<Clearance> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(clearanceDay)) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("clearanceDay").as(Date.class), DateUtil.stringToDateFormat(clearanceDay)));

                    // 设定EndDay
                    Date endDay = DateUtil.stringToDateFormat(clearanceDay);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(endDay);
                    calendar.add(calendar.DATE, 1); //把日期往后增加一天
                    endDay = calendar.getTime();

                    list.add(criteriaBuilder.lessThan(root.get("clearanceDay").as(Date.class), endDay));
                }

                if (type != null) {
                    list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), type));
                }

                if (unit != null) {
                    list.add(criteriaBuilder.equal(root.get("unit").as(Long.class), unit));
                }

                if (accountStatus != null) {
                    list.add(criteriaBuilder.equal(root.get("accountStatus").as(Integer.class), accountStatus));
                }

                if (isClear != null) {
                    list.add(criteriaBuilder.equal(root.get("isClear").as(Boolean.class), isClear));

                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return clearances;
    }

    @Override
    public Page<ClearanceDetail> findClearanceDetailByPage(Integer page, Integer size, Long clearanceId) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        org.springframework.data.domain.Page<ClearanceDetail> clearances = clearanceDetailDao.findAll(new Specification<ClearanceDetail>() {
            @Override
            public Predicate toPredicate(Root<ClearanceDetail> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (clearanceId != null) {
                    list.add(criteriaBuilder.equal(root.get("clearanceId").as(Integer.class), clearanceId));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return clearances;
    }

    @Override
    public List<ClearanceDetail> findUnClearClearanceDetail(Long clearanceId) {
        return clearanceDetailDao.findByClearanceIdAndAndIsClearIsFalse(clearanceId);
    }

    @Override
    public Page<ClearanceDetail> findAllClearanceDetailByPage(Integer page, Integer size, String clearanceDay, Long clearanceId, Integer qbaoType, Integer chainType, Long qbaoUnit,
                                                              Long chainUnit, Boolean isClear) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "createTime");
        org.springframework.data.domain.Page<ClearanceDetail> clearanceDetails = clearanceDetailDao.findAll(new Specification<ClearanceDetail>() {
            @Override
            public Predicate toPredicate(Root<ClearanceDetail> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(clearanceDay)) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), DateUtil.stringToDateFormat(clearanceDay)));

                    // 设定EndDay
                    Date endDay = DateUtil.stringToDateFormat(clearanceDay);
                    Calendar calendar = new GregorianCalendar();
                    calendar.setTime(endDay);
                    calendar.add(calendar.DATE, 1); //把日期往后增加一天
                    endDay = calendar.getTime();

                    list.add(criteriaBuilder.lessThan(root.get("createTime").as(Date.class), endDay));
                }

                if (clearanceId != null) {
                    list.add(criteriaBuilder.equal(root.get("clearanceId").as(Integer.class), clearanceId));
                }

                if (qbaoType != null) {
                    list.add(criteriaBuilder.equal(root.get("qbaoType").as(Integer.class), qbaoType));
                }

                if (chainType != null) {
                    list.add(criteriaBuilder.equal(root.get("chainType").as(Integer.class), chainType));
                }

                if (qbaoUnit != null) {
                    list.add(criteriaBuilder.equal(root.get("qbaoUnit").as(Long.class), qbaoUnit));
                }

                if (chainUnit != null) {
                    list.add(criteriaBuilder.equal(root.get("chainUnit").as(Long.class), chainUnit));
                }

                if (isClear != null) {
                    list.add(criteriaBuilder.equal(root.get("isClear").as(Boolean.class), isClear));

                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return clearanceDetails;
    }

    @Override
    public Clearance updateClearance(Clearance clearance) {

        Clearance clearanceUpdate = clearanceDao.findOne(clearance.getId());
        // 限定更新以下3个对账字段

        if (clearance.getAccountStatus() != null) {
            clearanceUpdate.setAccountStatus(clearance.getAccountStatus());
        }
        if (clearance.getAccountRemark() != null) {
            clearanceUpdate.setAccountRemark(clearance.getAccountRemark());
        }

        clearanceUpdate.setAccountDay(new Date());

        return clearanceDao.save(clearanceUpdate);
    }

    @Override
    public List<ClearanceDetail> findClearanceDetail(Long clearanceId) {
        return clearanceDetailDao.findByClearanceIdOrderByIsClear(clearanceId);
    }
}
