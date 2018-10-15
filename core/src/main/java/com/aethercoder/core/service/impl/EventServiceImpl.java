package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.EventDao;
import com.aethercoder.core.entity.batch.RedPacketBatchDefinition;
import com.aethercoder.core.entity.event.Event;
import com.aethercoder.core.service.EventApplyService;
import com.aethercoder.core.service.EventService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.batch.BatchDefinition;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
@Service
public class EventServiceImpl implements EventService {

    private static Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    @Autowired
    private EventDao eventDao;

    @Autowired
    private BatchService batchService;

    @Autowired
    private EventApplyService eventApplyService;
    @Autowired
    private LocaleMessageService localeMessageService;

    private String eventTable = "event";
    private String bannerField = "event_banner";

    @Override
    public Event createEvent(Event event) {
        if (validationEvent(event)) {
            eventDao.save(event);
        }

        //zh
        saveEventMessageZH(event);
        //en
        saveEventMessageEN(event);
        //ko
        saveEventMessageKO(event);
        //创建批处理
        Date endDate = event.getEndDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.MINUTE, 10);

        batchService.createBatchTask("event", cal.getTime(), WalletServiceImpl.class.getName(), event.getClass().getSimpleName(), event.getId());
        return event;
    }
    private void saveEventMessageZH(Event event) {
        if (event.getEventBanner() != null) {
            localeMessageService.saveMessage(eventTable, bannerField, event.getId() + "", WalletConstants.LANGUAGE_TYPE_ZH, event.getEventBanner());
        }
    }
    private void saveEventMessageEN(Event event) {
        if (event.getEventBanner_en() != null) {
            localeMessageService.saveMessage(eventTable, bannerField, event.getId() + "", WalletConstants.LANGUAGE_TYPE_EN, event.getEventBanner_en());
        }
    }
    private void saveEventMessageKO(Event event) {
        if (event.getEventBanner_ko() != null) {
            localeMessageService.saveMessage(eventTable, bannerField, event.getId() + "", WalletConstants.LANGUAGE_TYPE_KO, event.getEventBanner_ko());
        }
    }

    private void translateEvent(Event event, String language, int type) {
        if (event.getEventBanner() != null) {
            String questionI18n = localeMessageService.getMessageByTableFieldId(eventTable, bannerField, event.getId() + "", type, language);
            event.setEventBanner(questionI18n);
        }
    }
    // 检测Event的是否合法
    private boolean validationEvent(Event event) {
        Boolean result = false;
        if (event == null) {
            return result;
        }
        // 设定金额检测
        Long maxAmount = event.getUpperLimit();
        Long minAmount = event.getLowerLimit();
        Long totalAmount = event.getEventTotalAmount();

        if (maxAmount != null && minAmount != null && maxAmount < minAmount) {
            throw new AppException(ErrorCode.EVENT_LOWERLIMIT_IS_BIGGER);
        }
//        else if (maxAmount != null && totalAmount != null && totalAmount < maxAmount) {
//            throw new AppException(ErrorCode.EVENT_UPPERLIMIT_IS_BIGGER);
//        }

        // 活动期限检测
        String originalCurrency = event.getOriginalCurrency();
        String destCurrency = event.getDestCurrency();
        Date beginDate = event.getBeginDate();
        Date endDate = event.getEndDate();
        Date startDate = event.getStartDate();

        if (beginDate.compareTo(endDate) > 0) {
            throw new AppException(ErrorCode.DATE_RANGE_ERROR);
        }
        if (startDate != null && (beginDate.compareTo(startDate) > 0 || startDate.compareTo(endDate) > 0)) {
            throw new AppException(ErrorCode.DATE_RANGE_ERROR);
        }
        if (event.getType() != null && event.getType() == 0) {
            Long dataNum = eventDao.countEventsByDate(beginDate, endDate, originalCurrency, destCurrency);
            if (dataNum != null && ((dataNum > 1 && event.getId() != null) || (dataNum > 0 && event.getId() == null))) {
                throw new AppException(ErrorCode.EVENT_OVERLAP);
            }
        }
//        else if (event.getId() != null) {
//            Event pEvent = findByEventID(event.getId());
//            if (pEvent.getBeginDate().before(new Date())) {
//                throw new AppException(ErrorCode.EVENT_NOT_UPDATE);
//            } else {
//                result = true;
//            }
//        }
        result = true;
        return result;
    }

    @Override
    public Event updateEvent(Event event) {

        Event pEvent = findByEventID(event.getId());
        if (validationEvent(event)) {
            BeanUtils.copyPropertiesWithoutNull(event, pEvent);
            eventDao.save(pEvent);

            //更新或创建批处理
            Date endDate = event.getEndDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(Calendar.MINUTE, 10);
            batchService.createBatchTask("event", cal.getTime(), "com.aethercoder.core.service.impl.WalletServiceImpl", event.getClass().getSimpleName(), event.getId());
        }
        //zh
        saveEventMessageZH(event);
        //en
        saveEventMessageEN(event);
        //ko
        saveEventMessageKO(event);
        return pEvent;
    }

    @Override
    public void deleteEvent(Long id) {
        Event event = findByEventID(id);
        Long countApply = eventApplyService.countEventAppliesByEventId(id);
        if(countApply.compareTo(new Long(0))>0){
            throw new AppException(ErrorCode.EVENT_APPLIED_CANNOT_DELETE);
        }
        // 活动如果开始不能删除
        if (event.getEventAvailable()&&event .getBeginDate().compareTo(new Date()) < 0) {
            throw new AppException(ErrorCode.EVENT_CANNOT_DELETE);
        }
        eventDao.delete(event);
        //删除i8n
        localeMessageService.deleteMessageByTableFieldId(eventTable, bannerField, id + "");
        //删除批处理
        batchService.deleteBatchTask("event", event.getClass().getSimpleName(), id);
        return;
    }

    @Override
    public synchronized BigDecimal getEventSurplus(Long id) {
        BigDecimal result = new BigDecimal(0);
        Event event = findEventByEventID(id);
        BigDecimal appliedAmount = eventDao.getApplyIncomesByEventId(id);

        BigDecimal totalAmount = new BigDecimal(event.getEventTotalAmount());
        if (appliedAmount == null) {
            result = totalAmount;
        } else {
            result = totalAmount.compareTo(appliedAmount) >= 0 ? totalAmount.subtract(appliedAmount) : new BigDecimal(0);
        }
        return result;
    }

    @Override
    public Event findEventByEventID(Long eventId) {
        Event event = eventDao.findEventByIdAndEventAvailableIsTrue(eventId);
        if (event == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }
        return event;
    }

    @Override
    public Event findByEventID(Long eventId) {
        Event event = eventDao.findOne(eventId);
        if (event == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }
        return event;
    }

    @Override
    public List<Event> findEventAll() {
        return eventDao.findAll();
    }

    @Override
    public List<Event> findEventsNow() {

        Date nowDate = new Date();
        List<Event> eventList = eventDao.findEventsNow(nowDate);
        if (eventList == null || eventList.size() == 0) {
            // 没有当前有效活动，取过去最近的一期活动
            Event lastEvent = eventDao.findFirst1ByEndDateIsBeforeAndEventAvailableIsTrueOrderByEndDateDesc(nowDate);
            eventList = new ArrayList();
            if (lastEvent != null) {
                eventList.add(lastEvent);
            }
        }
        String language = localeMessageService.getLanguage();
        eventList.forEach(event -> {
            translateEvent(event, language, CommonConstants.I18N_SHOW_DEFAULT);
        });
        return eventList;
    }

    @Override
    public Page<Event> findEvensByPage(Integer page, Integer size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "beginDate");
        return eventDao.findAll(pageable);
    }

    @Override
    public Page<Event> findAvailableEvensByPage(Integer page, Integer size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "endDate");
        Page<Event> eventPage = eventDao.findAll(new Specification<Event>() {
            @Override
            public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                list.add(criteriaBuilder.equal(root.get("eventAvailable").as(Boolean.class), true));

                //小于或等于传入时间
                list.add(criteriaBuilder.lessThanOrEqualTo(root.get("beginDate").as(String.class), DateUtil.dateToString(new Date())));

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        String language = localeMessageService.getLanguage();
        eventPage.getContent().forEach(event -> {
            translateEvent(event, language, CommonConstants.I18N_SHOW_DEFAULT);
        });
        return eventPage;
    }

    @Override
    public Page<Event> findEvensByPage(Integer page, Integer size, String eventName, String originalCurrency, String destCurrency, Boolean eventAvailable, String beginDate, String endDate) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "beginDate");
        Page<Event> eventPage = eventDao.findAll(new Specification<Event>() {
            @Override
            public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                //活动名称
                if (null != eventName && !"".equals(eventName)) {
                    list.add(criteriaBuilder.like(root.get("eventName").as(String.class), eventName + "%"));
                }
                //是否有效
                if (null != eventAvailable && !"".equals(eventAvailable)) {
                    list.add(criteriaBuilder.equal(root.get("eventAvailable").as(Boolean.class), eventAvailable));
                }
                //兑换元币种
                if (null != originalCurrency && !"".equals(originalCurrency)) {
                    list.add(criteriaBuilder.equal(root.get("originalCurrency").as(String.class), originalCurrency));
                }
                //兑换币种
                if (null != destCurrency && !"".equals(destCurrency)) {
                    list.add(criteriaBuilder.equal(root.get("destCurrency").as(String.class), destCurrency));
                }
                if (StringUtils.isNotBlank(beginDate)) {
                    //大于或等于传入时间
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("beginDate").as(String.class), beginDate));
                }
                if (StringUtils.isNotBlank(endDate)) {
                    //小于或等于传入时间
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate").as(String.class), endDate));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        eventPage.forEach(event -> {
            translateEventEN(event, CommonConstants.I18N_SHOW_EMPTY);
            translateEventKO(event, CommonConstants.I18N_SHOW_EMPTY);
        });


        return eventPage;
    }

    private void translateEventEN(Event event, int type) {
        String questionI18n = localeMessageService.getMessageByTableFieldId(eventTable, bannerField, event.getId() + "", type, WalletConstants.LANGUAGE_TYPE_EN);
        event.setEventBanner_en(questionI18n);
    }
    private void translateEventKO(Event event, int type) {
        String questionI18n = localeMessageService.getMessageByTableFieldId(eventTable, bannerField, event.getId() + "", type, WalletConstants.LANGUAGE_TYPE_KO);
        event.setEventBanner_ko(questionI18n);
    }
    @Override
    public void createRedPacketEvent(RedPacketBatchDefinition event) {
        BatchDefinition batchDefinition = new BatchDefinition();
        BeanUtils.copyPropertiesWithoutNull(event, batchDefinition);
        batchDefinition.setParameterName1(event.getAccountNo());
        batchDefinition.setParameterName2(String.join(",", event.getGroupNoList()));
        batchDefinition.setParameterName3(event.getUnit().toString());
        batchDefinition.setParameterName4(event.getAmount().toPlainString());
        batchDefinition.setParameterName5(event.getNumber().toString());
        batchDefinition.setParameterName6(event.getComment());
        batchDefinition.setFrequency(CommonConstants.BATCH_FREQUENCY_DAILY);
        batchDefinition.setName(WalletConstants.SEND_RED_PACKET_BATCH_NAME_PRIX + event.getAccountNo());
        batchDefinition.setClassName(WalletConstants.SEND_RED_PACKET_BATCH_CLASS_NAME);
        batchService.saveBatchDefinition(batchDefinition);
    }

    @Override
    public void updateRedPacketEvent(RedPacketBatchDefinition event) {
        BatchDefinition batchDefinition = batchService.findDefinitionById(event.getId());
        BeanUtils.copyPropertiesWithoutNull(event, batchDefinition);
        batchDefinition.setParameterName1(event.getAccountNo());
        batchDefinition.setParameterName2(String.join(",", event.getGroupNoList()));
        batchDefinition.setParameterName3(event.getUnit().toString());
        batchDefinition.setParameterName4(event.getAmount().toPlainString());
        batchDefinition.setParameterName5(event.getNumber().toString());
        batchDefinition.setParameterName6(event.getComment());
        batchDefinition.setFrequency(CommonConstants.BATCH_FREQUENCY_DAILY);
        batchDefinition.setName(WalletConstants.SEND_RED_PACKET_BATCH_NAME_PRIX + event.getAccountNo());
        batchDefinition.setClassName(WalletConstants.SEND_RED_PACKET_BATCH_CLASS_NAME);
        batchService.saveBatchDefinition(batchDefinition);
    }
}
