package com.aethercoder.core.dao.batch;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.GroupDao;
import com.aethercoder.core.dao.GroupMemberDao;
import com.aethercoder.core.dao.RedPacketEventDao;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.RedPacketEvent;
import com.aethercoder.core.entity.event.SendRedPacket;
import com.aethercoder.core.entity.social.Group;
import com.aethercoder.core.entity.social.GroupMember;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.core.service.SendRedPacketService;
import com.aethercoder.foundation.service.BatchService;
import io.rong.RongCloud;
import io.rong.messages.RedPacketMessage;
import io.rong.models.CodeSuccessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

/**
 * @auther jiawei.tao
 * @date 2017/12/13 下午1:38
 */
@Service
public class AutoCreateRedPacketBatch implements  BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(AutoCreateRedPacketBatch.class);

    @Value( "${rongCloud.appKey}" )
    private String appKey;
    @Value( "${rongCloud.appSecret}" )
    private String appSecret;

    @Autowired
    private SendRedPacketService sendRedPacketService;
    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private RedPacketEventDao redPacketEventDao;

    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private GroupDao groupDao;

    @Autowired
    private BatchService batchService;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("SendRedPacketBatch");

        RedPacketEvent redPacketEvent = redPacketEventDao.findOne(task.getResourceId());

        GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndRoleAndIsDeletedFalse(redPacketEvent.getGroupNo(),WalletConstants.GROUP_ROLE_HOSTER);
        //check该群是否解散
        Group group = groupDao.findGroupByGroupNoAndIsDeletedIsFalse(groupMember.getGroupNo());
        if (group==null){
            // 由于该群解散不能继续发红包
            BatchResult failResult = task.getFailedResult("Because the group is disbanded, it cannot continue to give red packets.");
           //删除剩余未执行的批处理
            batchService.deleteBatchTasks(redPacketEvent.getClass().getSimpleName(),redPacketEvent.getId());
            return failResult;
        }
        /*String[] groupNoList = StringUtils.split(task.getParameter2(),",");
        Integer number = groupNoList.length;
        String account = task.getParameter1();

        BigDecimal amount =task.getParameter4() ==null?new BigDecimal(0): new BigDecimal(task.getParameter4());
        Long unit = task.getParameter3() ==null?new Long(0): new Long(task.getParameter3());

        amount = amount.multiply(new BigDecimal(number));*/

        // 获取用户余额
        AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(groupMember.getMemberNo(), redPacketEvent.getUnit());
        if (accountBalance == null || accountBalance.getAmount().compareTo(redPacketEvent.getAmount()) < 0) {
            // 由于您的余额不足导致不能设置的群定时红包不能正常发送。请去充值。
            BatchResult failResult = task.getFailedResult("the group owner has not enough money.");
            return failResult;
        }
        // 根据群个数循环
//        for(int i=0;i<groupNoList.length;i++) {

            SendRedPacket sendRedPacket = new SendRedPacket();
            sendRedPacket.setAccountNo(groupMember.getMemberNo());
            sendRedPacket.setUnit(redPacketEvent.getUnit() ==null?new Long(0): redPacketEvent.getUnit());
            sendRedPacket.setAmount(redPacketEvent.getAmount()==null?new BigDecimal(0): redPacketEvent.getAmount());
            sendRedPacket.setNumber(redPacketEvent.getRedNumber() ==null?new Integer(0): redPacketEvent.getRedNumber());
            sendRedPacket.setComment(redPacketEvent.getRedComment());
            sendRedPacket.setType(redPacketEvent.getRedType());
            sendRedPacket.setIsAvailable(true);
            sendRedPacketService.createRedPacket(sendRedPacket);
            // 发红包

            String[] messagePublishGroupToGroupId = {redPacketEvent.getGroupNo()};
            RedPacketMessage messagePublishGroupTxtMessage = new RedPacketMessage(sendRedPacket);

            RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
            CodeSuccessResult messagePublishGroupResult = rongCloud.message.publishGroup(sendRedPacket.getAccountNo(), messagePublishGroupToGroupId, messagePublishGroupTxtMessage, "Here is a red packet", "{\"pushData\":\"Here is a red packet\"}", 1, 1, 0);
            System.out.println("publishGroup:  " + messagePublishGroupResult.toString());
//        }
        BatchResult successResult = task.getSuccessResult("success");

        return successResult;
    }

}
