package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.SystemNoticeDao;
import com.aethercoder.core.entity.social.SystemNotice;
import com.aethercoder.core.service.SystemNoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @author lilangfeng
 * @date 2018/01/03
 **/
@Service
public class SystemNoticeServiceImpl implements SystemNoticeService{
    private static Logger logger = LoggerFactory.getLogger(SystemNoticeServiceImpl.class);
    @Autowired
    private SystemNoticeDao systemNoticeDao;


    @Override
    public SystemNotice createSystemNotice(SystemNotice systemNotice) {
            if (WalletConstants.NOTICE_STATUS_ISSUE.equals(systemNotice.getStatus())) {
                systemNotice.setIssueTime(new Date());
            }
            systemNotice.setIsDelete(false);
            return systemNoticeDao.save(systemNotice);
    }
    @Override
    public SystemNotice updateSystemNotice(SystemNotice systemNotice){
        SystemNotice uSystemNotice = systemNoticeDao.findOne(systemNotice.getId());

        uSystemNotice.setTitle(systemNotice.getTitle());
        uSystemNotice.setContent(systemNotice.getContent());
        if (!WalletConstants.NOTICE_STATUS_ISSUE.equals(uSystemNotice.getStatus()) && WalletConstants.NOTICE_STATUS_ISSUE.equals(systemNotice.getStatus())) {
            uSystemNotice.setIssueTime(new Date());
        }else if(!WalletConstants.NOTICE_STATUS_ISSUE.equals(systemNotice.getStatus())){
//            uSystemNotice.setIssueTime(null);
        }
        uSystemNotice.setStatus(systemNotice.getStatus());
        return systemNoticeDao.save(uSystemNotice);
    }
    @Override
    public void deleteNotice(Long id)
    {
        SystemNotice systemNotice =systemNoticeDao.findOne(id);
        systemNotice.setIsDelete(true);
        systemNoticeDao.save(systemNotice);
    }
    @Override
    public List<SystemNotice> findSystemNoticeByLanguage(String language){

        return systemNoticeDao.findAllByLanguageTypeAndStatus(language,WalletConstants.NOTICE_STATUS_ISSUE);
    }

    @Override
    public Page<SystemNotice> findSystemNoticesByPage(Integer page, Integer size, String content,Integer status,String languageType) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "updateTime");
        Page<SystemNotice> systemNoticePage = systemNoticeDao.findAll(new Specification<SystemNotice>(){
            @Override
            public Predicate toPredicate(Root<SystemNotice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != content && !"".equals(content)) {
                    list.add(criteriaBuilder.like(root.get("content").as(String.class), "%"+content + "%"));
                }
                if(status !=null) {
                    list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), status));
                }
                list.add(criteriaBuilder.equal(root.get("isDelete").as(Boolean.class),false));
                if(null != languageType && !"".equals(languageType)) {
                    list.add(criteriaBuilder.equal(root.get("languageType").as(String.class), languageType));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return systemNoticePage;
    }

    @Override
    public Page<SystemNotice> findSystemNoticesByPage(Integer page, Integer size, String languageType) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "updateTime");
        Page<SystemNotice> systemNoticePage = systemNoticeDao.findAll(new Specification<SystemNotice>(){
            @Override
            public Predicate toPredicate(Root<SystemNotice> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(criteriaBuilder.equal(root.get("isDelete").as(Boolean.class),false));
                if(null != languageType && !"".equals(languageType)) {
                    list.add(criteriaBuilder.equal(root.get("languageType").as(String.class), languageType));
                }
                list.add(criteriaBuilder.isNotNull(root.get("issueTime").as(String.class)));

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return systemNoticePage;
    }

}
