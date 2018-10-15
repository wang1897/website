package com.aethercoder.core.dao.batch;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.OrderDao;
import com.aethercoder.core.entity.pay.Order;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.core.service.ExchangeLogService;
import com.aethercoder.core.service.OrderService;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.schedule.BatchInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @auther jiawei.tao
 * @date 2018/03/30 下午1:38
 */
@Service
public class OrderCancelBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(OrderCancelBatch.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;
    @Autowired
    private ExchangeLogService exchangeLogService;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("OrderCancelBatch");
        // 获取时间超过2分钟以上还没有关闭的数据
        Date nowDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(nowDate);
        cal.add(Calendar.MINUTE, -2);
        List<Order> orderList = orderDao.findByStatusAndOrderTimeBefore(WalletConstants.UNCONFIRMED,cal.getTime());

        BatchResult successResult  = task.getSuccessResult("no unclosed order");

        for(Order order:orderList){
            orderService.cancelOrder(order.getOrderId(),null);
        }
        if(!orderList.isEmpty()){
            successResult  = task.getSuccessResult("success");
        }
        logger.info("OrderCancelBatch end");

        return successResult;
    }


}
