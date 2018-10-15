package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.EventApply;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jiawei.tao on 2017/9/14.
 */
public interface EventApplyService {
    EventApply createEventApply(EventApply eventApply);

    EventApply cancelEventApply(Long id);
    /*
    获取用户的该活动的锁定金额
    */
    EventApply getExpectedApplyInfo(String accountNo, Long eventId, BigDecimal surplus);

    /*
    获取用户可提取的金额
    */
    BigDecimal getAllActualAmount(String accountNo);

    /*
    获取用户的账户余额
     */
    BigDecimal parseEventExpression(Double qtumAmount, String expression);

    /*
    获取已结束的有效活动的所有未支付申请
     */
    List<EventApply> findUnpaidEventAppliesUntilNow();

    /*
    更新申请为已经支付状态
     */
    List<EventApply> setEventAppliesPaid(List<EventApply> eventApplyList);

    /*
    更新实际划账兑换元代币金额和实际划账代币金额
     */
    EventApply updateEventApply(EventApply eventApply);

    /*
    获得该活动的所有报名人数
     */
    Long countEventAppliesByEventId(Long id);

    /*
    求和本次活动的所有预定金额
     */
    BigDecimal sumByExpectedIncome(Long id);

    /*
    求和本次活动所有实际转账金额
     */
    BigDecimal sumByActualIncome(Long id);

    /*
    该活动所有报名用户信息
     */
    List<EventApply> findEventAppliesByEventId(Long id);

    /*
    该活动所有报名用户信息分页+模糊查询
    */
    Page<EventApply> findEventAppliesByEventIdByPage(Integer page, Integer size,Long id, String accountNo);

    /*
    获取该用户参加的活动信息
    */
    List<EventApply> getEventAppliesByAccount(String accountNo);
    /*
    获取该用户参加的活动信息
    */
    List<EventApply> findByAccountNoAndStatus(String accountNo,Integer status);

    String getShareText(String accountNo);

    String getShareCodeUrl(String accountNo);
}
