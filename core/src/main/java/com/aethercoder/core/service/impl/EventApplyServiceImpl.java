package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.AESUtil;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.AccountDao;
import com.aethercoder.core.dao.EventApplyDao;
import com.aethercoder.core.entity.event.Event;
import com.aethercoder.core.entity.event.EventApply;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.DateUtil;
import org.apache.commons.jexl3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiawei.tao on 2017/9/14.
 */
@Service
public class EventApplyServiceImpl implements EventApplyService {
    private static Logger logger = LoggerFactory.getLogger(AdminAccountServiceImpl.class);
    @Autowired
    private EventApplyDao eventApplyDao;

    @Autowired
    private EventService eventService;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Autowired
    private ContractService contractService;

    private BigDecimal surplus;
    @Override
    public synchronized EventApply createEventApply(EventApply eventApply) {
        logger.info("createEventApply start:" + this.toString() + System.currentTimeMillis());
        surplus= new BigDecimal(0);
        if (validationApplyEvent(eventApply)) {
            // 获取锁定金额
            // 计算预期收益
            EventApply pEventApply = getExpectedApplyInfo(eventApply.getAccountNo(), eventApply.getEventId(),surplus);
            // 如果没有余额，无法申请活动
            logger.info("pEventApply:"+pEventApply.getExpectedIncome());
            if (pEventApply.getExpectedIncome() == null || pEventApply.getExpectedIncome().compareTo(new BigDecimal(0)) <= 0) {
                if (surplus.compareTo(new BigDecimal(0)) == 0) {
                    throw new AppException(ErrorCode.EVENT_AMOUNT_SELL_OUT);
                } else {
                    throw new AppException(ErrorCode.EVENTAPPLY_NO_MONEY);
                }
            }
            if (WalletConstants.APPLY_STATUS_NOT_APPLY.equals(pEventApply.getApplyStatus()) ||
                    WalletConstants.APPLY_STATUS_CANCELED.equals(pEventApply.getApplyStatus())) {
                if (WalletConstants.APPLY_STATUS_CANCELED.equals(pEventApply.getApplyStatus())) {
                    eventApply.setId(pEventApply.getId());
                    eventApply.setCancelTime(null);
                }
                eventApply.setApplyStatus(WalletConstants.APPLY_STATUS_APPLIED);
                eventApply.setExpectedIncome(pEventApply.getExpectedIncome());
                eventApply.setApplyAmount(pEventApply.getApplyAmount());
                eventApply.setApplyTime(new Date());

                logger.info("saveAndFlush:"+pEventApply.getExpectedIncome());
                eventApplyDao.saveAndFlush(eventApply);
            } else {
                // 活动已经申请了
                throw new AppException(ErrorCode.EVENT_HAD_APPLIED);
            }
        }
        logger.info("createEventApply end:" + this.toString() + System.currentTimeMillis());
        return eventApply;
    }

    // 申请活动的合法性校验
    private synchronized boolean validationApplyEvent(EventApply eventApply) {
        boolean result = false;
        if (eventApply == null) {
            return result;
        }
        Event event = eventService.findEventByEventID(eventApply.getEventId());

        // 判断活动是否存在
        if (event == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        try {
            // 判断活动是否开始
            if(event.getStartDate()!=null && (DateUtil.dateCompare(df.parse(df.format(new Date())), event.getStartDate()))){
                throw new AppException(ErrorCode.EVENT_HAS_NOT_START);
            }else if (event.getStartDate() ==null && (DateUtil.dateCompare(df.parse(df.format(new Date())), event.getBeginDate()))){
                throw new AppException(ErrorCode.EVENT_HAS_NOT_START);
            }
            // 判断活动是否过期
            if ((DateUtil.dateCompare(df.parse(df.format(new Date())), event.getEndDate()))) {
                surplus = eventService.getEventSurplus(eventApply.getEventId());
                // 获取活动剩余量
                if (new BigDecimal(0).compareTo(surplus) < 0) {
                    result = true;
                } else {
                    // 判断活动剩余量<=0
                    throw new AppException(ErrorCode.EVENT_AMOUNT_SELL_OUT);
                }

            } else {
                throw new AppException(ErrorCode.EVENT_IS_OVER);
            }
        } catch (ParseException e) {
            throw new RuntimeException("Can't transfer the date. Please contact with the Administrator.");
        }

        return result;
    }

    @Override
    public EventApply cancelEventApply(Long id) {
        EventApply pEventApply = eventApplyDao.findOne(id);
        pEventApply.setApplyStatus(WalletConstants.APPLY_STATUS_CANCELED);
        pEventApply.setCancelTime(new Date());
        eventApplyDao.save(pEventApply);
        return pEventApply;
    }

    @Override
    public EventApply getExpectedApplyInfo(String accountNo, Long eventId, BigDecimal eventSurplus) {
        EventApply eventApply = eventApplyDao.getApplyByAccountNoAndEventId(accountNo, eventId);
        Event event = eventService.findEventByEventID(eventId);
        //获取合约id
        if (WalletConstants.QBT_TOKEN_NAME.equals(event.getOriginalCurrency().toUpperCase())){
            Contract contract = contractService.findContractByName(WalletConstants.QBT_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
            event.setOriginalCurrency(String.valueOf(contract.getId()));
        }else if(WalletConstants.QTUM_TOKEN_NAME.equals(event.getOriginalCurrency().toUpperCase())){
            Contract contract = contractService.findContractByName(WalletConstants.QTUM_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
            event.setOriginalCurrency(String.valueOf(contract.getId()));
        }

        // 没有申请记录说明
        if (eventApply == null) {
            eventApply = new EventApply();
            // 申请状态 未报名
            eventApply.setApplyStatus(WalletConstants.APPLY_STATUS_NOT_APPLY);
        } else if (WalletConstants.APPLY_STATUS_CANCELED.equals(eventApply.getApplyStatus())) {

        }
        if(!WalletConstants.APPLY_STATUS_APPLIED.equals(eventApply.getApplyStatus())) {
            BigDecimal applyAmount = walletService.getUnspentAmountByAccountNo(accountNo, Long.valueOf(event.getOriginalCurrency()));
            BigDecimal expectedIncome = new BigDecimal(0);
            // 根据活动设定上下限金额
            Long upperLimit = event.getUpperLimit();
            Long lowerLimit = event.getLowerLimit();
            if (lowerLimit != null && applyAmount.compareTo(new BigDecimal(lowerLimit)) < 0) {
                // 用户余额小于最小申请额度，返回0
                eventApply.setExpectedIncome(expectedIncome);
            } else if (upperLimit != null && applyAmount.compareTo(new BigDecimal(upperLimit)) > 0) {
                // 用户账户余额超过最大申请额度
                applyAmount = new BigDecimal(upperLimit);
                eventApply.setExpectedIncome(getExpectedIncome(accountNo, eventId, applyAmount, eventSurplus));
            } else {
                eventApply.setExpectedIncome(getExpectedIncome(accountNo, eventId, applyAmount, eventSurplus));
            }
            eventApply.setApplyAmount(applyAmount);
            eventApply.setEvent(event);
        }
        return eventApply;
    }

    @Override
    public BigDecimal getAllActualAmount(String accountNo) {
        if (accountNo == null) {
            return new BigDecimal(0);
        }

        BigDecimal actualAmount = eventApplyDao.sumActualIncomeByAccountNo(accountNo, WalletConstants.APPLY_STATUS_DONE);
        return actualAmount == null ? new BigDecimal(0) : actualAmount;
    }

    // 计算预期收益
    private synchronized BigDecimal getExpectedIncome(String accountNo, Long eventId, BigDecimal amount, BigDecimal eventSurplus) {
        Event event = eventService.findEventByEventID(eventId);
        BigDecimal expectedIncome = new BigDecimal(0);
        expectedIncome = parseEventExpression(amount.doubleValue(), event.getExpression());
        expectedIncome = expectedIncome == null ? new BigDecimal(0) : expectedIncome;
        // 根据活动剩余金额，如果预期金额大于剩余金额，返回剩余金额。
        if(eventSurplus==null){
            eventSurplus = eventService.getEventSurplus(eventId);
        }
        BigDecimal eventUnpaidAmount = eventSurplus;
        logger.info("eventUnpaidAmount :" + eventUnpaidAmount);
        expectedIncome = expectedIncome.compareTo(eventUnpaidAmount) > 0 ? eventUnpaidAmount : expectedIncome;
        return expectedIncome;
    }

    @Override
    public BigDecimal parseEventExpression(Double qtumAmount, String expression) {
        Map<String, Object> namespaces = new HashMap<>();
        namespaces.put("math", Math.class);

        JexlEngine jexl = new JexlBuilder()
                .namespaces(namespaces)
                .create();

        // Create an expression
        //(source * 0.1 - (source * 0.1) % 50) / 50 * 10 + source * 0.1    每100个qtum送10个qbt,每50个qbt再送10个qbt
        JexlExpression e = jexl.createExpression(expression);

        // Create a context and add data
        JexlContext jc = new MapContext();
        jc.set("source", qtumAmount);
        jc.set("sourceInt", qtumAmount.intValue());
        // Now evaluate the expression, getting the result
        Object o = e.evaluate(jc);
        return new BigDecimal(o.toString());
    }

    @Override
    public List<EventApply> findUnpaidEventAppliesUntilNow() {
        Date now = new Date();
        return eventApplyDao.getUnpaidEventApplies(now);
    }

    @Override
    public List<EventApply> setEventAppliesPaid(List<EventApply> eventApplyList) {
        eventApplyList.forEach(eventApply -> {
                    eventApply.setApplyStatus(WalletConstants.APPLY_STATUS_EXCHAGED);
                }
        );
        eventApplyDao.save(eventApplyList);
        return eventApplyList;
    }

    @Override
    public EventApply updateEventApply(EventApply eventApply) {
        EventApply uEventApply = eventApplyDao.findOne(eventApply.getId());
        BeanUtils.copyPropertiesWithoutNull(eventApply, uEventApply);
        return eventApplyDao.save(uEventApply);
    }


    @Override
    public Long countEventAppliesByEventId(Long id) {
        return eventApplyDao.countEventAppliesByEventId(id);
    }

    @Override
    public BigDecimal sumByExpectedIncome(Long id) {
        return eventApplyDao.sumByExpectedIncome(id);
    }

    @Override
    public BigDecimal sumByActualIncome(Long id) {
        return eventApplyDao.sumByActualIncome(id);
    }

    @Override
    public List<EventApply> findEventAppliesByEventId(Long id) {
        List<EventApply> eventApplyList = eventApplyDao.findEventAppliesByEventId(id);
        eventApplyList.forEach(eventApply -> {
                    eventApply.setAccount(accountDao.findByAccountNo(eventApply.getAccountNo()));
                }
        );

        return eventApplyList;
    }

    @Override
    public Page<EventApply> findEventAppliesByEventIdByPage(Integer page, Integer size, Long id, String accountNo) {
        if (null == accountNo) {
            accountNo = "";
        }
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<EventApply> eventApplyList = eventApplyDao.findByAccount_AccountNameLikeOrAccountNoLikeAndEventId(id, accountNo + "%", pageable);
        eventApplyList.forEach(eventApply -> {
                    eventApply.setAccount(accountDao.findByAccountNo(eventApply.getAccountNo()));
                }
        );
        return eventApplyList;
    }

    @Override
    public List<EventApply> getEventAppliesByAccount(String accountNo) {
        return eventApplyDao.getEventAppliesByAccountNo(accountNo);
    }

    @Override
    public List<EventApply> findByAccountNoAndStatus(String accountNo, Integer status) {
        return eventApplyDao.findEventApplyByAccountNoAndApplyStatus(accountNo,status);
    }

    @Override
    public String getShareText(String accountNo) {
        String shareCode = accountService.getShareCode(accountNo);
        SysConfig config = sysConfigService.findSysConfigByName(WalletConstants.INVITE_HTML_URL);
        StringBuilder builder = new StringBuilder();
        builder.append(config.getValue().trim()).append("?shareCode=").append(shareCode);
        String text = localeMessageUtil.getLocalMessage(WalletConstants.INVITE_FRIEND_TEXT,builder.toString());
        return text;
    }

    @Override
    public String getShareCodeUrl(String accountNo) {
        SysConfig config = sysConfigService.findSysConfigByName(WalletConstants.INVITE_FRIEND_URL);
        accountNo = AESUtil.encrypt(accountNo, CommonConstants.AES_KEY);
        StringBuilder builder = new StringBuilder();
        builder.append(config.getValue().trim()).append("?accountNo=").append(accountNo);
        return builder.toString();
    }
}
