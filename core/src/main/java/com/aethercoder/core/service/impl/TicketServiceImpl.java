package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.TicketDao;
import com.aethercoder.core.entity.ticket.Ticket;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.GroupService;
import com.aethercoder.core.service.TicketService;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.StringUtil;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author lilangfeng
 * @date 2018/01/03
 **/
@Service
public class TicketServiceImpl implements TicketService {
    private static Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);
    @Autowired
    private TicketDao ticketDao;
    @Autowired
    private GroupService groupService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private LocaleMessageService localeMessageUtil;

    @Override
    public Ticket createTicket(Ticket ticket) {
        ticket.setQuestionTime(new Date());
        ticket.setStatus(3);
        StringUtil.isIllegalDBVercharWithoutEmoji(ticket.getQuestion(), WalletConstants.GROUP_NOTICE_CONTENT_LENGTH);
        return ticketDao.save(ticket);
    }

    @Override
    public Ticket updateTicket(Ticket ticket) {
        Ticket uTicket = ticketDao.findOne(ticket.getId());
        uTicket.setAnswerTime(new Date());
        uTicket.setAnswerBy(WalletConstants.QBAO_ADMIN);
        StringUtil.isIllegalDBVercharWithoutEmoji(ticket.getAnswer(), WalletConstants.GROUP_NOTICE_CONTENT_LENGTH);
        uTicket.setAnswer(ticket.getAnswer());
        uTicket.setStatus(ticket.getStatus());
        Ticket pTicket = ticketDao.save(uTicket);
        String[] toUserNo = {pTicket.getAccountNo()};

        String[] messageParam = {pTicket.getQuestion() ,pTicket.getAnswer()};
        logger.info("message" + messageParam[0] + " " + messageParam[1]);
        Locale locale = accountService.getLocaleByAccount(pTicket.getAccountNo());

        String message = localeMessageUtil.getLocalMessage("TICK_NOTE",locale, messageParam);
        groupService.sendInVite(WalletConstants.CUSTOMER_ONE, toUserNo, message, null, null);
        return uTicket;
    }

    @Override
    public Page<Ticket> findEvensByPage(Integer page, Integer size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "beginDate");
        return ticketDao.findAll(pageable);
    }

    @Override
    public Page<Ticket> findTicketsByPage(Integer page, Integer size, String question, Long type, Integer status, String startTime, String endTime) {

        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "updateTime");
        Page<Ticket> ticketPage = ticketDao.findAll(new Specification<Ticket>() {
            @Override
            public Predicate toPredicate(Root<Ticket> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != question && !"".equals(question)) {
                    list.add(criteriaBuilder.like(root.get("question").as(String.class), "%" + question + "%"));
                }
                if (type != null) {
                    list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), type));
                }
                if (status != null) {
                    list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), status));
                }
                if (StringUtils.isNotBlank(startTime)) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("questionTime").as(String.class), startTime));
                }
                if (StringUtils.isNotBlank(endTime)) {
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("questionTime").as(String.class), endTime));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return ticketPage;
    }

    @Override
    public List<Ticket> countByType() {
        List list = ticketDao.countByType();
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

            Object[] objs = (Object[]) list.get(i);
            Ticket ticket = new Ticket();
            ticket.setName(objs[0].toString());
            ticket.setCount(Integer.parseInt(objs[1].toString()));
            tickets.add(ticket);
        }
        return tickets;
    }

}
