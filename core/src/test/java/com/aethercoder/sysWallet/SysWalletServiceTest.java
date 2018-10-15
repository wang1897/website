package com.aethercoder.sysWallet;

import com.aethercoder.TestApplication;
import com.aethercoder.core.dao.ExchangeLogDao;
import com.aethercoder.core.dao.SysWalletAddressDao;
import com.aethercoder.core.entity.media.Media;
import com.aethercoder.core.entity.wallet.SysWalletAddress;
import com.aethercoder.core.service.MediaService;
import com.aethercoder.core.service.QtumService;
import com.aethercoder.core.service.WalletService;
import com.aethercoder.foundation.service.BatchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created by jiaweiTao on 12/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class SysWalletServiceTest {
    @Autowired
    private BatchService batchService;

    @Autowired
    private ExchangeLogDao exchangeLogDao;
    @Autowired
    private SysWalletAddressDao sysWalletAddressDao;
    @Autowired
    private WalletService walletService;
    @Autowired
    private MediaService mediaService;
    @Autowired
    private QtumService qtumService;

    @Test
    public void testMediaQuery() {
        System.out.println("testMediaQuery");

        Page<Media> mediaList = mediaService.findMediasByPage(0,20,null,null,null);

        System.out.println("endMediaQuery");

    }
    @Test
    public void testStartClearBatch() {
        System.out.println("ClearanceBatch");

//        BatchDefinition batchDefinition = new BatchDefinition();
//
//        batchDefinition.setName("ClearanceBatch");
//        batchDefinition.setFrequency(CommonConstants.BATCH_FREQUENCY_MINUTELY);
//        batchDefinition.setStartTime(new Date());
//        batchDefinition.setIsActive(true);
//        batchDefinition.setClassName(ClearanceBatch.class.getName());
//        batchDefinition.setTimeSlot(WalletConstants.BATCH_TIMESLOT);
//        batchService.saveBatchDefinition(batchDefinition);

    }
    @Test
    public void testStartWithdrawBatch() {
//        System.out.println("testStartWithdrawBatch");
//
//        BatchDefinition batchDefinition = new BatchDefinition();
//
//        batchDefinition.setName("WithdrawBatch");
//        batchDefinition.setFrequency(WalletConstants.BATCH_FREQUENCY_MINUTELY);
//        batchDefinition.setStartTime(new Date());
//        batchDefinition.setIsActive(true);
//        batchDefinition.setClassName(WithdrawBatch.class.getName());
//        batchDefinition.setTimeSlot(WalletConstants.BATCH_TIMESLOT);
//        batchService.saveBatchDefinition(batchDefinition);
//        ExchangeLog exchangeLog = exchangeLogDao.findOne(new Long(3580));

        List<SysWalletAddress> addressList=sysWalletAddressDao.findByKeepServiceIsTrue();
//        walletService.startServiceByAddress(addressList);
//        List<String> addressList = new ArrayList<>();
//        addressList.add("09800417b097c61b9fd26b3ddde4238304a110d5");
//        String txId = "9e5f26fe3959105c8ee541d4cd82af22c0c901c8f4f62b81069a52184d9ba043";
//        HashMap transaction = qtumService.getTransaction(txId);
//        if (transaction != null) {
//            Integer confirmations = (Integer)transaction.get("confirmations");
//            Integer height = (Integer)transaction.get("height");
//            List<LinkedHashMap>  logs =  (List<LinkedHashMap>) qtumService.getEventLog(new Long(height.toString()),new Long(height.toString()),addressList);
//            for(LinkedHashMap log:logs){
//                String transactionHash = (String)log.get("transactionHash");
//                if(txId.equals(transactionHash)){
//                    System.out.println("true");
//                }
//            }
//            Map vout = (Map)voutList.get(index);
//            Map scriptPubKey = (Map)vout.get("scriptPubKey");
//            map.put("script", scriptPubKey.get("hex"));
//            map.put("outputIndex", index);
//            list.add(map);
//        }
   }
    @Test
    public void testGetTxId() {
//        System.out.println("testStartWithdrawBatch");
//
//        BatchDefinition batchDefinition = new BatchDefinition();
//
//        batchDefinition.setName("WithdrawBatch");
//        batchDefinition.setFrequency(WalletConstants.BATCH_FREQUENCY_MINUTELY);
//        batchDefinition.setStartTime(new Date());
//        batchDefinition.setIsActive(true);
//        batchDefinition.setClassName(WithdrawBatch.class.getName());
//        batchDefinition.setTimeSlot(WalletConstants.BATCH_TIMESLOT);
//        batchService.saveBatchDefinition(batchDefinition);
//        ExchangeLog exchangeLog = exchangeLogDao.findOne(new Long(3580));

        List<SysWalletAddress> addressList=sysWalletAddressDao.findByKeepServiceIsTrue();
//        walletService.startServiceByAddress(addressList);
//        List<String> addressList = new ArrayList<>();
//        addressList.add("09800417b097c61b9fd26b3ddde4238304a110d5");
//        String txId = "9e5f26fe3959105c8ee541d4cd82af22c0c901c8f4f62b81069a52184d9ba043";
//        HashMap transaction = qtumService.getTransaction(txId);
//        if (transaction != null) {
//            Integer confirmations = (Integer)transaction.get("confirmations");
//            Integer height = (Integer)transaction.get("height");
//            List<LinkedHashMap>  logs =  (List<LinkedHashMap>) qtumService.getEventLog(new Long(height.toString()),new Long(height.toString()),addressList);
//            for(LinkedHashMap log:logs){
//                String transactionHash = (String)log.get("transactionHash");
//                if(txId.equals(transactionHash)){
//                    System.out.println("true");
//                }
//            }
//            Map vout = (Map)voutList.get(index);
//            Map scriptPubKey = (Map)vout.get("scriptPubKey");
//            map.put("script", scriptPubKey.get("hex"));
//            map.put("outputIndex", index);
//            list.add(map);
//        }
    }
}
