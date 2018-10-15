package com.aethercoder.core.dao.batch;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.*;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * @auther jiawei.tao
 * @date 2017/12/23 下午1:38
 */
@Service
public class ClearanceBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(ClearanceBatch.class);

//    @Value( "${rongCloud.appKey}" )
//    private String appKey;
//    @Value( "${rongCloud.appSecret}" )
//    private String appSecret;


    @Autowired
    private ExchangeLogService exchangeLogService;
    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Autowired
    private BatchService batchService;
    @Autowired
    private ContractService contractService;
    @Autowired
    public LocaleMessageService localeMessageUtil;
    @Autowired
    private ClearanceDao clearanceDao;
    @Autowired
    private ClearanceDetailDao clearanceDetailDao;
    @Autowired
    private QtumService qtumService;
    @Autowired
    private SysWalletDao sysWalletDao;
    @Autowired
    private SysWalletAddressDao sysWalletAddressDao;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private URLDecipheringService urlDecipheringService;

    @Override
    @Transactional

    public BatchResult run(BatchTask task) throws Exception {
        logger.info("ClearanceBatch 3.1 2018-04-28 11:24");

        // 提币
        List<ClearanceDetail> clearanceDetailList1 = runByType(task, WalletConstants.MENTION_TYPE);

        // 充值
        List<ClearanceDetail> clearanceDetailList2 = runByType(task, WalletConstants.RECHANGER_TYPE);

        // 若有未Clear发短信给相关负责人发短信。
        int unclearNum = 0;
        //  统计交易数及交易金额
        for (ClearanceDetail clearanceDetail : clearanceDetailList1) {

            if (!clearanceDetail.getIsClear()) {
                unclearNum++;

            }

        }
        for (ClearanceDetail clearanceDetail : clearanceDetailList2) {

            if (!clearanceDetail.getIsClear()) {
                unclearNum++;

            }

        }

        if (unclearNum > 0) {

            logger.info("有未清算数据 " + unclearNum + " 条");

            // 接收消息对象由系统设定
            SysConfig sysConfig = sysConfigService.findSysConfigByName(WalletConstants.CLEARANCE_WARNING_MOBILE_LIST);

            if (sysConfig != null) {
                // 发送短信
                String mobileStr = sysConfig.getValue();
                Locale locale = Locale.SIMPLIFIED_CHINESE;

                // urlDecipheringService.sendSmsClearance(mobileStr,unclearNum+"");
                //String templateCode = WalletConstants.QBAO_PAY_STR + localeMessageUtil.getLocalMessage("QBAO_CLEARANCE_UNCLEAR_SMS", new String[]{unclearNum + ""});

                String templateCode = WalletConstants.QBAO_PAY_STR + localeMessageUtil.getLocalMessage("QBAO_CLEARANCE_UNCLEAR_SMS", locale, new String[]{unclearNum + ""});
                urlDecipheringService.sendMultiSms(mobileStr, templateCode);

            }
        }

        logger.info("结束执行ClearanceBatch");
        BatchResult batchResult = new BatchResult();
        batchResult.setTaskId(0L);
        batchResult.setStatus(CommonConstants.BATCH_RESULT_SUCCESS);
        batchResult.setResult("ClearanceBatch execute success");
        batchService.saveBatchResult(batchResult);

        return batchResult;

    }

    private List<ClearanceDetail>  runByType(BatchTask task,Integer runType) throws Exception {

        // exchangeLog.FeeUnit为空时，需要设定qtumId??
        Contract qtumContract = contractService.findContractByName(WalletConstants.QTUM_TOKEN_NAME, WalletConstants.CONTRACT_QTUM_TYPE);

        Long qtumId = qtumContract == null ? new Long(21) : qtumContract.getId();

        // 根据提币/充值取得上次执行的ID和chain块号（ExchangeLog遍历时不区分币种，因此所有的币种取得上次执行的ID和chain块号应该是设定一样的值）
        Clearance lastClearance = clearanceDao.findFirstClearanceByTypeOrderByIdDesc(runType);

        // 上次执行的Chain块号
        Long oldChainBlock = lastClearance != null ? lastClearance.getChainBlock() : new Long(0);

        // 上次执行的ID
        Long oldQbaoId = lastClearance != null ? lastClearance.getQbaoId() : new Long(0);

        // Batch执行日前一日最后一条Exchange记录
         ExchangeLog endExchangeLog = exchangeLogDao.findFirstByExchangeTimeBeforeOrderByIdDesc(DateUtil.stringToDateFormat(DateUtil.dateToStringYYYYMMDD(new Date())));

        // Qbao表未进行清算的最大Id
        Long endLog = endExchangeLog == null ? new Long(0) : endExchangeLog.getId();

        // 获取Qbao表所有未进行清算的提币/充值记录
        List<ExchangeLog> exchangeLogs = exchangeLogService.findUnclearExchangeLogs(oldQbaoId + 1, endLog, runType);

        // 获取提币/充值的地址
        List<String> addressList = new ArrayList<>();

        // sys_wallet_address 中的 记录的地址是提币的地址
        if (WalletConstants.MENTION_TYPE == runType.intValue()) {
            List<SysWalletAddress> sysWalletAddressList = sysWalletAddressDao.findAll();
            for (SysWalletAddress sysWalletAddress : sysWalletAddressList) {
                addressList.add(sysWalletAddress.getAddress());

            }

        // sys_wallet 中 wallet_address对应的是充值地址。
        } else {
            SysWallet sysWallet = sysWalletDao.findSysWalletByName("wallet_address");

            if (sysWallet != null) {
                addressList.add(sysWallet.getValue());
            }
        }

        // 获取
        Long endChainBlock = oldChainBlock;

        for (ExchangeLog exchangeLog : exchangeLogs) {

            HashMap qtumMap = qtumService.getTransaction(exchangeLog.getTransactionHash());
            if (qtumMap != null && qtumMap.containsKey("height") &&  qtumMap.containsKey("confirmations")) {

                if((Integer) qtumMap.get("confirmations") > 6) {
                    endChainBlock = ((Integer) qtumMap.get("height")).longValue();
                    break;
                }
            }

        }

        // 获取所有链上的记录
        List qtumMapList = qtumService.getTransHistoryByAddress(addressList, null, Integer.MAX_VALUE, 0, oldChainBlock + 1, endChainBlock);

        // Clearance 合计
        HashMap<Long, Clearance> contractMap = saveClearance(endChainBlock, endLog > oldQbaoId ? endLog : oldQbaoId, runType);

        List<ClearanceDetail> clearanceDetailList = new ArrayList<>();
        // Qbao表记录和链上的记录做对账
        for (ExchangeLog exchangeLog : exchangeLogs) {
            ClearanceDetail clearanceDetail = clearanceDetailDao.findByQbaoId(exchangeLog.getId());
            if (clearanceDetail != null) {
                continue;
            }


            clearanceDetail = new ClearanceDetail();

            clearanceDetail.setQbaoId(exchangeLog.getId());
            clearanceDetail.setQbaoTxid(exchangeLog.getTransactionHash());
            clearanceDetail.setQbaoAmount(exchangeLog.getAmount());
            clearanceDetail.setQbaoUnit(exchangeLog.getUnit()); // ??
            clearanceDetail.setQbaoExchangeTime(exchangeLog.getExchangeTime());
            clearanceDetail.setQbaoFromAddress(exchangeLog.getFromddress());
            clearanceDetail.setQbaoToAddress(exchangeLog.getAddress());
            clearanceDetail.setQbaoFeeAmount(exchangeLog.getFee());
            clearanceDetail.setQbaoType(exchangeLog.getType());
            clearanceDetail.setQbaoFeeUnit(exchangeLog.getFeeUnit() == null ? qtumId : exchangeLog.getFeeUnit()); //Fee Unit会空，但Unit不为空

            // FeeUnit都是qtumId
            clearanceDetail.setChainFeeUnit(qtumId);
            // 设置链上信息。
            setChainInfo(clearanceDetail, qtumMapList, null, runType, contractMap);
            clearanceDetailList.add(clearanceDetail);

        }

        // 链上未匹配到的记录生成ClearanceDetail
        for (Object qtumObj : qtumMapList) {
            HashMap qtumMap = (HashMap) qtumObj;
            if (qtumMap.get("matched") == null) {

                String chainTxid = (String) qtumMap.get("txid");

                ClearanceDetail clearanceDetail = clearanceDetailDao.findByChainTxid(chainTxid);

                if (clearanceDetail != null) {
                    continue;
                }

                clearanceDetail = new ClearanceDetail();

                // FeeUnit都是qtumId
                clearanceDetail.setChainFeeUnit(qtumId);

                // 设置链上信息。
                setChainInfo(clearanceDetail, qtumMapList, qtumMap, runType, contractMap);

                clearanceDetailList.add(clearanceDetail);

            }

        }

        // Clearance 合计
        updateClearance(qtumMapList, contractMap, clearanceDetailList, runType);

        return clearanceDetailList;


    }

    private HashMap<Long, Clearance>  saveClearance(Long endChain, Long endLog, Integer runType) {

        // 清算日
//        Date clearanceDate = DateUtil.stringToDateFormat(DateUtil.dateToStringYYYYMMDD(new Date()));


        // 对账日
        Date accountDay = new Date();

        // 清算日
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(accountDay);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);//设置为前一天
        Date clearanceDate = calendar.getTime();   //得到前一天的时间

        // 取得有交易数据的代币种类
        List<Contract> contractList = contractService.findAllAvailableContracts(WalletConstants.CONTRACT_QTUM_TYPE);
        HashMap<Long, Clearance> contractMap = new HashMap();

        for (Contract contract : contractList) {
            Clearance clearance = new Clearance();
            clearance.setChainAmount(new BigDecimal(0));
            clearance.setQbaoAmount(new BigDecimal(0));
            clearance.setClearanceDay(clearanceDate);
            clearance.setType(runType);
            clearance.setUnit(contract.getId());
            clearance.setChainNumber(0L);
            clearance.setQbaoNumber(0L);
            clearance.setIsClear(true);
            clearance.setQbaoId(endLog); //遍历表最大的QbaoId
            clearance.setChainBlock(endChain);//遍历链最大的BlockId
            clearance.setAccountDay(accountDay);
            clearance.setAccountStatus(WalletConstants.CLEARANCE_ACCOUNT_STATUS_NORMAL); // 对账状态默认，无需处理
            contractMap.put(contract.getId(), clearance);

        }

        // Clearace Save
        Iterator it = contractMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            Clearance clearance = (Clearance) me.getValue();
            Clearance clearanceSave = clearanceDao.save(clearance);
            me.setValue(clearanceSave);

        }

        return contractMap;

    }

    private void updateClearance(List qtumMapList, HashMap<Long, Clearance> contractMap, List<ClearanceDetail> clearanceDetailList, Integer runType) {

        //  统计交易数及交易金额
        for (ClearanceDetail clearanceDetail : clearanceDetailList) {

            if (contractMap.containsKey(clearanceDetail.getQbaoUnit()) || contractMap.containsKey(clearanceDetail.getChainUnit())) {
                Clearance clearance = null;
                if (contractMap.containsKey(clearanceDetail.getQbaoUnit())) {
                    clearance = contractMap.get(clearanceDetail.getQbaoUnit());
                } else {

                    clearance = contractMap.get(clearanceDetail.getChainUnit());
                }

                if (clearanceDetail.getChainAmount() != null) {
                    clearance.setChainAmount(clearance.getChainAmount().add(clearanceDetail.getChainAmount()));
                    clearance.setChainNumber(clearance.getChainNumber() + 1);
                }

                if (clearanceDetail.getQbaoAmount() != null) {
                    clearance.setQbaoAmount(clearance.getQbaoAmount().add(clearanceDetail.getQbaoAmount()));
                    clearance.setQbaoNumber(clearance.getQbaoNumber() + 1);
                }
                if (!clearanceDetail.getIsClear()) {
                    clearance.setIsClear(false);
                    clearance.setAccountStatus(WalletConstants.CLEARANCE_ACCOUNT_STATUS_CONFIRM); //对账状态，待处理

                }

            }
        }

        // Clearace Save
        Iterator it = contractMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry me = (Map.Entry) it.next();
            Clearance clearance = (Clearance) me.getValue();
            clearanceDao.save(clearance);
        }


    }

    private void setChainInfo(ClearanceDetail clearanceDetail, List qtumMapList, HashMap blockMap , Integer runType,HashMap<Long, Clearance> contractMap) {

        boolean matchResult = confirmTransactionHash(clearanceDetail, qtumMapList, blockMap, runType);

        clearanceDetail.setIsClear(matchResult);

        if (contractMap.containsKey(clearanceDetail.getQbaoUnit())) {
            Clearance clearance = contractMap.get(clearanceDetail.getQbaoUnit());

            clearanceDetail.setClearanceId(clearance.getId());

        } else if (contractMap.containsKey(clearanceDetail.getChainUnit())) {
            Clearance clearance = contractMap.get(clearanceDetail.getChainUnit());

            clearanceDetail.setClearanceId(clearance.getId());

        } else {
            clearanceDetail.setClearanceId(0l);
        }

         clearanceDetailDao.save(clearanceDetail);
    }

    private boolean confirmTransactionHash(ClearanceDetail clearanceDetail, List qtumMapList, HashMap blockMap, Integer runType) {
        // 默认设定未清算
        boolean result = false;
        HashMap map = null;

        // 传入的链数据非空的情况（只有链数据，没有Qbao表记录）
        if (blockMap != null) {
            // 使用传入的链数据
            map = blockMap;
            // 传入的链数据空的情况（有Qbao表记录）
        } else {
            // 查询匹配的链数据
            for (Object qtumObj : qtumMapList) {
                HashMap qtumMap = (HashMap) qtumObj;
                if (qtumMap.containsKey("txid") && qtumMap.get("txid").equals(clearanceDetail.getQbaoTxid())) {
                    map = qtumMap;
                    qtumMap.put("matched", true);
                    break;
                }

            }
        }

        // 链数据不存在
        if (map == null) {
            return result;

        // 链数据存在（包含两种情况：Qbao表记录有/无）
        } else {

            // 获取链上的contract
            Contract contract = getContractByChain(map);
            // 链上信息设定
            clearanceDetail.setChainBlock(map.containsKey("height") ? ((Integer) map.get("height")) : 0L);
            clearanceDetail.setChainTxid(map.containsKey("txid") ? (String) map.get("txid") : null);

            clearanceDetail.setChainUnit(contract.getId()) ;
            if(map.containsKey("time") ) {
                Timestamp ts = new Timestamp((Long) map.get("time"));
                Date exchangeTime = new Date(ts.getTime());
                clearanceDetail.setChainExchangeTime(exchangeTime);
            }
            // vin 第一个address
//            clearanceDetail.setChainFromAddress(null);

            // vout from不一样的任何一个地址
//            clearanceDetail.setChainToAddress(null);

            // vin的value总和-vout的value总和
//            clearanceDetail.setChainFeeAmount(null);

            // vout :sum(value)/vout : first token value (vout对象中排除vin一样address)
            // clearanceDetail.setChainAmount(null);
            clearanceDetail.setChainType(runType);

            // 在clearanceDetail对象生成时固定设定QumId
//            clearanceDetail.setChainFeeUnit(null);

            // 以下代码设定from/to/amount/feeAmount
            // qtum转账，金额就是所有Vout(除去和fromAddress一样)中value的总和。
            // 手续费 qtum的话就是Vin的Value总和减去Vout的Value总和。
            if (WalletConstants.QTUM_TOKEN_NAME.equals(contract.getAddress())) {
                Object vin = map.get("vin");
                ArrayList<HashMap> vinMap = (ArrayList<HashMap>) vin;
                BigDecimal vinAmount = new BigDecimal(0);

                // fromAddress原则上都是一样的）
                for (HashMap vinValue : vinMap) {
                    String address = (String) vinValue.get("address");
                    clearanceDetail.setChainFromAddress(address);
                    vinAmount = vinAmount.add(BigDecimal.valueOf((Double) vinValue.get("value")));
                }

                // toAddress 原则上有两条，一条转出，取和From不一样的
                Object vout = map.get("vout");
                ArrayList<HashMap> voutMap = (ArrayList<HashMap>) vout;
                BigDecimal voutAmount = new BigDecimal(0);
                for (HashMap voutValue : voutMap) {
                    BigDecimal vValue = BigDecimal.valueOf((Double) voutValue.get("value"));
                    vinAmount = vinAmount.subtract(vValue);
                    String address = (String) voutValue.get("address");
                    if (address == null || address.equals(clearanceDetail.getChainFromAddress())) {
                        continue;

                    }
                    clearanceDetail.setChainToAddress(address);
                    voutAmount = voutAmount.add(vValue);

                }
                // feeAmount
                clearanceDetail.setChainFeeAmount(vinAmount);
                // amount
                clearanceDetail.setChainAmount(voutAmount);


            // 其他代币的转账
            // 金额就是Vout中，Address不同于Vin的Addreess的第一个tokenValue的值去除 10的 按照转账代币对应的精度的值 次方
            // 手续费和Qtum算法一样
            } else {

                Object vin = map.get("vin");

                ArrayList<HashMap> vinMap = (ArrayList<HashMap>) vin;
                BigDecimal vinAmount = new BigDecimal(0);
                for (HashMap vinValue : vinMap) {

                    String address = (String) vinValue.get("address");
                    if (address == null) {
                        continue;
                    }
                    clearanceDetail.setChainFromAddress(address);
                    vinAmount = vinAmount.add(BigDecimal.valueOf((Double) vinValue.get("value")));
                }

                // toAddress 原则上有两条，一条转出，取和From不一样的
                Object vout = map.get("vout");
                ArrayList<HashMap> voutMap = (ArrayList<HashMap>) vout;
                BigDecimal voutAmount = new BigDecimal(0);
                boolean firstToken = true;
                for (HashMap voutValue : voutMap) {
                    BigDecimal vValue = BigDecimal.valueOf((Double) voutValue.get("value"));
                    vinAmount = vinAmount.subtract(vValue);
                    String address = (String) voutValue.get("address");
                    if (address == null || address.equals(clearanceDetail.getChainFromAddress())) {
                        continue;

                    }
                    clearanceDetail.setChainToAddress(address);
                    if(voutValue.containsKey("tokenValue") && firstToken) {

                        voutAmount = new BigDecimal((String)voutValue.get("tokenValue"));
                       firstToken = false;
                    }

                }

                Integer decimal = Integer.parseInt(contract.getContractDecimal(), 16);
                for (int i = 0; i < decimal; i++) {
                    voutAmount = voutAmount.divide(new BigDecimal(10));
                }
                // feeAmount
                clearanceDetail.setChainFeeAmount(vinAmount);
                // amount
                clearanceDetail.setChainAmount(voutAmount);


            }
        }

        //  blockMap==null,Qbao表记录存在，表数据和链数据进行比较。从XXXTxid到XXXFeeUnit。都相等result 设为true
        if (blockMap == null) {
            result = checkIfChainClear(clearanceDetail) ;
        }

        return result;
    }

    private boolean checkIfChainClear(ClearanceDetail clearanceDetail) {

        // txid比较
        if (!clearanceDetail.getQbaoTxid().equals(clearanceDetail.getChainTxid())) {

            return false;
        }

        // amount
        if (clearanceDetail.getQbaoAmount() == null || clearanceDetail.getQbaoAmount().compareTo(clearanceDetail.getChainAmount()) != 0) {
            return false;
        }

        // unit
        if (clearanceDetail.getQbaoUnit() != clearanceDetail.getChainUnit().longValue()) {
            return false;
        }

//        // time???
//        if(!clearanceDetail.getQbaoExchangeTime().equals(clearanceDetail.getChainExchangeTime())) {
//            return false;
//        }

        // fromAddress
        if (clearanceDetail.getQbaoFromAddress() == null || !clearanceDetail.getQbaoFromAddress().equals(clearanceDetail.getChainFromAddress())) {
            return false;
        }

        // toAddress
        if (clearanceDetail.getQbaoToAddress() == null || !clearanceDetail.getQbaoToAddress().equals(clearanceDetail.getChainToAddress())) {
            return false;
        }

//        // feeAmount
//        if (clearanceDetail.getQbaoFeeAmount() == null || clearanceDetail.getQbaoFeeAmount().compareTo(clearanceDetail.getChainFeeAmount()) != 0) {
//            return false;
//        }
//
//        // type
//        if (clearanceDetail.getQbaoType() != clearanceDetail.getChainType().intValue()) {
//            return false;
//        }
//
//        // feeUnit
//        if (clearanceDetail.getQbaoFeeUnit() != clearanceDetail.getChainFeeUnit().longValue()) {
//            return false;
//        }

        return true;
    }

    private Contract getContractByChain(HashMap blockMap) {

        Contract contract = null;
        // 如果Vin 有contractAddressSha160 这个就是代币的合约地址
        // 经测试，开发环境充值Vin 没有contractAddressSha160,Vout里有
        String contractAddress = null;
        Object vin = blockMap.get("vin");

        ArrayList<HashMap> vinMap = (ArrayList<HashMap>) vin;
        for (HashMap vinValue : vinMap) {

            String contractAddressSha160 = (String) vinValue.get("contractAddressSha160");
            if (contractAddressSha160 != null) {
                contractAddress = contractAddressSha160;
                continue;
            }
        }

        if(contractAddress == null) {

            Object vout = blockMap.get("vout");

            ArrayList<HashMap> voutMap = (ArrayList<HashMap>) vout;
            for (HashMap voutValue : voutMap) {

                String contractAddressSha160 = (String) voutValue.get("contractAddressSha160");
                if (contractAddressSha160 != null) {
                    contractAddress = contractAddressSha160;
                    continue;
                }
            }

        }

        // contractAddressSha160有的情况，用这个地址去取Contract
        if(contractAddress != null) {
            contract = contractService.findContractByAddress(contractAddress);
            if(contract==null) {

                // address 在smart_contract 表不存在的情况
                contract = new Contract();
                contract.setId(null);
                contract.setName(null);
                contract.setAddress(contractAddress);
                contract.setContractDecimal("1");
            }

        } else {
            // 没有则代表qtum的转账
            contract = contractService.findContractByName(WalletConstants.QTUM_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);

            // 会有空的情况
            if(contract == null) {
                contract = new Contract();
                contract.setId(21l);
                contract.setName(WalletConstants.QTUM_TOKEN_NAME);

            }

        }

        return contract;
    }

}
