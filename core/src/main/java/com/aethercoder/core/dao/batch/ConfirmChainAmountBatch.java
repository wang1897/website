package com.aethercoder.core.dao.batch;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.SysWalletAddressDao;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.SysWalletAddress;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.service.LocaleMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * @auther jiawei.tao
 * @date 2017/12/23 下午1:38
 */
@Service
public class ConfirmChainAmountBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(ConfirmChainAmountBatch.class);

    @Value( "${rongCloud.appKey}" )
    private String appKey;
    @Value( "${rongCloud.appSecret}" )
    private String appSecret;

    @Autowired
    private SysWalletAddressDao sysWalletAddressDao;
    @Autowired
    private BatchService batchService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private GroupService groupService;
    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("ConfirmExchangeBatch");
        BatchResult result = null;
        Contract qbtContract = contractService.findContractByName(WalletConstants.QBT_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
        // 比较链上余额和sysAddress上的余额是否一致，不一致则发信息。报错。
        List<SysWalletAddress> sysWalletAddressList = sysWalletAddressDao.findByKeepServiceIsTrue();
        for (SysWalletAddress sysWalletAddress : sysWalletAddressList) {

            Long contractId = sysWalletAddress.getContractId();
            Contract contract = contractService.findContractById(contractId);
            // 判断keepService状态，更新资金池剩余
            BigDecimal leftAmount = sysWalletAddress.getLastLeftAmount();
            BigDecimal qbtLeftAmount = sysWalletAddress.getQbtLeftAmount();
            BigDecimal qtumLeftAmount = sysWalletAddress.getQtumLeftAmount();

            BigDecimal leftAmountChain = new BigDecimal(0);
            // 取链上的数据
            String address = sysWalletAddress.getAddress();
            BigDecimal qtumLeftAmountChain = walletService.getChainQtumAmount(address);
            if (!WalletConstants.QTUM_TOKEN_NAME.equalsIgnoreCase(contract.getName())) {
                leftAmountChain = walletService.getChainTokenAmount(address, sysWalletAddress.getContractId());
            } else {
                leftAmountChain = qtumLeftAmountChain;
            }
            BigDecimal qbtLeftAmountChain = walletService.getChainTokenAmount(address, qbtContract.getId());


            // 建议账上资金是否是一致，符合提币的
            if (leftAmount.compareTo(leftAmountChain) > 0
                    || qbtLeftAmount.compareTo(qbtLeftAmountChain) > 0
                    || qtumLeftAmount.compareTo(qtumLeftAmountChain) > 0) {

                result = task.getFailedResult("Withdraw address "+sysWalletAddress.getId().toString()+"Chain amount is less than Qbao info");

                String message = "提币地址" + sysWalletAddress.getId().toString()+"对账金额大于链上金额";
                String pushContent = "提币地址监控报警";
                groupService.sendInVite(WalletConstants.CUSTOMER_ONE, new String[]{"176692"}, message, pushContent, null);

            }
        }
        if(result==null){
            result = task.getSuccessResult("success");
        }
        return result;
    }

}
