package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.MediaDao;
import com.aethercoder.core.entity.media.Media;
import com.aethercoder.core.service.MediaService;
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
import java.util.List;

@Service
public class MediaServiceImpl implements MediaService {
    @Autowired
    MediaDao mediaDao;

    @Override
    public Media createMedia(Media media) {
        StringUtil.isIllegalDBVercharThrowEx(media.getName(), 50);
        StringUtil.isIllegalDBVercharThrowEx(media.getUrl(), 255);
        StringUtil.isIllegalDBVercharThrowEx(media.getIcon(), 255);
        StringUtil.isIllegalDBVercharThrowEx(media.getNotes(), 255);
        media.setStatus(WalletConstants.MEDIA_STATUS_ISSUE);
        List<Media> mediaList = mediaDao.findAll();
        for (int i = mediaList.size(); 0 <= i; i--) {
            if (mediaList.size() == 0) {
                media.setSequence(mediaList.size());
            } else {
                media.setSequence(mediaList.size());
                break;
            }
        }
        return mediaDao.save(media);
    }

    @Override
    public Media updateMedia(Media media) {
        Media mediaTemmp = mediaDao.findMediaById(media.getId());
        StringUtil.isIllegalDBVercharThrowEx(media.getName(), 50);
        StringUtil.isIllegalDBVercharThrowEx(media.getUrl(), 255);
        StringUtil.isIllegalDBVercharThrowEx(media.getIcon(), 255);
        StringUtil.isIllegalDBVercharThrowEx(media.getNotes(), 255);
        mediaTemmp.setName(media.getName());
        mediaTemmp.setNotes(media.getNotes());
        mediaTemmp.setIcon(media.getIcon());
        mediaTemmp.setUrl(media.getUrl());
        return mediaDao.save(mediaTemmp);
    }

    @Override
    public Page<Media> findMediasByPage(Integer page, Integer size, String name, Integer status, String languageType) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "status"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "sequence"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "updateTime"));
        Pageable pageable = new PageRequest(page, size, new Sort(orders));
        Page<Media> media = mediaDao.findAll(new Specification<Media>() {
            @Override
            public Predicate toPredicate(Root<Media> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != name && !"".equals(name)) {
                    list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + name + "%"));
                }
                if (status != null) {
                    list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), status));
                }
                if (null != languageType && !"".equals(languageType)) {
                    list.add(criteriaBuilder.like(root.get("languageType").as(String.class), languageType));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return media;
    }

    @Override
    public List<Media> findMediaByLanguageAndStatusIsDefaultUsing(String languageType) {
        return mediaDao.findMediaByLanguageTypeAndStatus(languageType, WalletConstants.MEDIA_STATUS_ISSUE);
    }


    @Override
    public Media modifyStatus(Media media) {
        Media media1 = mediaDao.findOne(media.getId());
        media1.setStatus(media.getStatus());
        return mediaDao.save(media1);
    }

    @Override
    public void sortMediaBySequence(Long[] tagsIdList) {
        if (tagsIdList == null || tagsIdList.length == 0) {
            return;
        }
        List<Media> MediaList = new ArrayList<>();
        for (int i = 0; i < tagsIdList.length; i++) {
            Media Media = mediaDao.findMediaById(tagsIdList[i]);
            Media.setSequence(i);
            MediaList.add(Media);
        }
        mediaDao.save(MediaList);
    }
}

