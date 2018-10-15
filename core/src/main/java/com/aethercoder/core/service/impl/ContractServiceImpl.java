package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ContractDao;
import com.aethercoder.core.dao.SysWalletAddressDao;
import com.aethercoder.core.dao.batch.GetContractPriceBatch;
import com.aethercoder.core.dao.batch.GetCurrencyRateBatch;
import com.aethercoder.core.dao.batch.WithdrawBatch;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.SysWalletAddress;
import com.aethercoder.core.service.ContractService;
import com.aethercoder.core.service.WalletService;
import com.aethercoder.foundation.entity.batch.BatchDefinition;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.BatchService;
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
import java.util.List;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Service
public class ContractServiceImpl implements ContractService {
    private static Logger logger = LoggerFactory.getLogger(ContractServiceImpl.class);
    @Autowired
    private SysWalletAddressDao sysWalletAddressDao;
    @Autowired
    private ContractDao contractDao;

    @Autowired
    private WalletService walletService;
    @Autowired
    private BatchService batchService;

    @Override
    public List<Contract> findContractAllForIOS(Integer type) {
        List<Contract> contractList = contractDao.findContractsByTypeAndIsDeleteIsFalseOrderBySequenceAsc(type);
        List<Contract> result = new ArrayList<>();
        for(Contract contract :contractList){
            if(!WalletConstants.QTUM_TOKEN_NAME.equalsIgnoreCase(contract.getAddress())&&
                    !WalletConstants.QBAO_ENERGY_ADRESS.equalsIgnoreCase(contract.getAddress())){
                result.add(contract);
            }
        }
        return result;
    }

    @Override
    public List<Contract> findContractsAll() {
        Pageable pageable = new PageRequest(0, 100, Sort.Direction.ASC, "sequence");
        Page<Contract> contracts = contractDao.findAll(pageable);
        List<Contract> contractList = contracts.getContent();
        return contractList;
    }

    @Override
    public org.springframework.data.domain.Page<Contract> findContractsAll(Integer page, Integer size, String name, Boolean isShow, Boolean isDelete,Integer type) {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "sequence"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        Pageable pageable = new PageRequest(page, size, new Sort(orders));
        org.springframework.data.domain.Page<Contract> contracts = contractDao.findAll(new Specification<Contract>() {
            @Override
            public Predicate toPredicate(Root<Contract> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != name && !"".equals(name)) {
                    list.add(criteriaBuilder.like(root.get("name").as(String.class), "%" + name + "%"));
                }
                if (null != isShow && !"".equals(isShow)) {
                    list.add(criteriaBuilder.equal(root.get("ishow").as(Boolean.class), isShow));
                }
                if (null != isDelete && !"".equals(isDelete)) {
                    list.add(criteriaBuilder.equal(root.get("isDelete").as(Boolean.class), isDelete));
                }
                if (null != type) {
                    list.add(criteriaBuilder.equal(root.get("type").as(Boolean.class), type));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return contracts;
    }

    @Override
    public List<Contract> findAllAvailableContracts(Integer type) {
        return contractDao.findContractsByTypeAndIsDeleteIsFalseOrderBySequenceAsc(type);
    }

    @Override
    public Contract findContractByName(String name,Integer type) {
        Contract contract = contractDao.findContractByNameAndType(name,type);
        if (contract == null) {
            throw new AppException(ErrorCode.CONTRACT_NAME_NOT_EXIST);
        }
        return contract;
    }

    @Override
    public Contract findContractAndIsDeleteIsFalseByName(String name) {
        return contractDao.findContractByNameAndIsDeleteIsFalse(name);
    }

    @Override
    public Contract findContractById(Long id) {
        return contractDao.findOne(id);
    }

    @Override
    public Contract saveContract(Contract contract) {
        contract.setIsDelete(WalletConstants.COMMON_NOT_DELETE);
        return contractDao.save(contract);
    }

    @Override
    public Contract updateContract(Contract contract) {
        Contract contract1 = contractDao.findOne(contract.getId());
        Boolean inService = contract.getInService();
        Boolean inService1 = contract1.getInService();
        if (inService != null && inService1 != null && inService == false && inService1 == true) {
            //将链上余额保存到sysAddressWallet上
            // 初始化提币地址值
            List<SysWalletAddress> addressList = sysWalletAddressDao.findByContractId(contract.getId());
            walletService.startServiceByAddress(addressList);
        }
        BeanUtils.copyPropertiesWithoutNull(contract, contract1);
        return contractDao.save(contract1);
    }

    @Override
    public void deleteContract(Long contractId, boolean isDelete) {
        Contract contract = contractDao.findOne(contractId);
        contract.setIsDelete(isDelete);
        contractDao.save(contract);
    }

    @Override
    public String getByContractAddress(String contractName) {
        Contract contract = contractDao.findContractByNameAndIsDeleteIsFalse(contractName);
        if (contract == null) {
            throw new AppException(ErrorCode.CONTRACT_NAME_NOT_EXIST);
        }
        return contract.getAddress();
    }

    @Override
    public Contract findContractByAddress(String contractAddress) {
        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        for (Contract contract : contracts) {
            if (contract.getAddress().equals(contractAddress)) {
                return contract;
            }
        }

        return null;
    }

    @Override
    public void updateContractByApiAddress() {

//        BatchDefinition availableDefinition = batchService.findDefinitionByName("GetContractPriceBatch");
//        if (availableDefinition == null) {
//
//            List<Contract> contracts = findAllAvailableContracts();
//            Map<String, String> names = new HashMap<String, String>();
//            names.put(WalletConstants.BATCH_NAME_INK, "http://api.zb.com/data/v1/ticker?market=ink_usdt");
//            names.put(WalletConstants.BATCH_NAME_QBT, "https://api.exx.com/data/v1/ticker?currency=qbt_usdt");
//            names.put(WalletConstants.BATCH_NAME_TSL, "http://data.gate.io/api2/1/ticker/tsl_usdt");
//            names.put(WalletConstants.BATCH_NAME_QTUM, "https://api.exx.com/data/v1/ticker?currency=qtum_usdt");
//            names.put(WalletConstants.BATCH_NAME_ENT, "http://api.zb.com/data/v1/ticker?market=ent_usdt");
//
//            for (Contract contract : contracts) {
//                if (names.containsKey(contract.getName())) {
//                    contract.setApiAddress(names.get(contract.getName()));
//                }
//            }
//            contractDao.save(contracts);
//
//            BatchDefinition batchDefinition = new BatchDefinition();
//
//            batchDefinition.setName("GetContractPriceBatch");
//            batchDefinition.setFrequency(WalletConstants.BATCH_FREQUENCY_MINUTELY);
//            batchDefinition.setStartTime(new Date());
//            batchDefinition.setIsActive(true);
//            batchDefinition.setClassName(GetContractPriceBatch.class.getName());
//            batchDefinition.setTimeSlot(WalletConstants.BATCH_TIMESLOT);
//            batchService.saveBatchDefinition(batchDefinition);
//        }
//        BatchDefinition availableDefinition = batchService.findDefinitionByName("GetBlockTimeBatch");
//        if (availableDefinition == null) {
//            BatchDefinition batchDefinition = new BatchDefinition();
//
//            batchDefinition.setName("GetBlockTimeBatch");
//            batchDefinition.setFrequency(CommonConstants.BATCH_FREQUENCY_MINUTELY);
//            batchDefinition.setStartTime(new Date());
//            batchDefinition.setIsActive(true);
//            batchDefinition.setClassName(GetBlockTimeBatch.class.getName());
//            batchDefinition.setTimeSlot(WalletConstants.BATCH_TIMESLOT_ONE);
//            batchService.saveBatchDefinition(batchDefinition);
//        }
        BatchDefinition batchDefinition = batchService.findDefinitionByName("getRatesBatch");
        if(batchDefinition!=null){
            batchDefinition.setClassName(GetCurrencyRateBatch.class.getName());
            batchService.saveBatchDefinition(batchDefinition);
        }
        BatchDefinition getContractPriceBatch = batchService.findDefinitionByName("GetContractPriceBatch");
        if(getContractPriceBatch!=null){
            getContractPriceBatch.setClassName(GetContractPriceBatch.class.getName());
            batchService.saveBatchDefinition(getContractPriceBatch);
        }
        BatchDefinition withdrawBatch = batchService.findDefinitionByName("WithdrawBatch");
        if(withdrawBatch!=null){
            withdrawBatch.setClassName(WithdrawBatch.class.getName());
            batchService.saveBatchDefinition(withdrawBatch);
        }

        walletService.startWithdrawBatch();
    }



    @Override
    public void updateContractSequence(String[] contractIdList) {
        if (contractIdList == null || contractIdList.length == 0) {
            return;
        }
        List<Contract> contracts = new ArrayList<>();
        for (int i = 0; i < contractIdList.length; i++) {
            Contract contract = contractDao.findOne(Long.parseLong(contractIdList[i]));
            contract.setSequence(i);
            contracts.add(contract);
        }
        contractDao.save(contracts);
    }

    @Override
    public List<Contract> getTokenAmountList() {
        List<Contract> contractList = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        List<Contract> resultList = new ArrayList<>();
        for (Contract contract : contractList) {
            if (contract.getWithdrawLimit() != null && contract.getWithdrawLimit().compareTo(new BigDecimal(0)) > 0) {
                // 获取余额
                BigDecimal leftAmount = new BigDecimal(0);
                List<SysWalletAddress> sysWalletAddressList;
                if (!WalletConstants.QBT_TOKEN_NAME.equals(contract.getName())) {
                    sysWalletAddressList = sysWalletAddressDao.findByContractId(contract.getId());
                } else {
                    sysWalletAddressList = sysWalletAddressDao.findAll();
                }

                for (SysWalletAddress sysWalletAddress : sysWalletAddressList) {
                    if (!WalletConstants.QBT_TOKEN_NAME.equals(contract.getName())) {
                        leftAmount = leftAmount.add(sysWalletAddress.getLastLeftAmount());
                    } else {
                        leftAmount = leftAmount.add(sysWalletAddress.getQbtLeftAmount());
                    }

                }
                contract.setLeftAmount(leftAmount);
                resultList.add(contract);
            }
        }
        return resultList;
    }

    @Override
    public void setDefaultWithdraw() {

        List<Contract> contractList = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        for (Contract contract : contractList) {
            switch (contract.getName()) {
                case "QTUM":
                    contract.setWithdrawLimit(new BigDecimal(2000));
                    contract.setWithdrawDayLimit(new BigDecimal(200));
                    contract.setWithdrawOneLimit(new BigDecimal(200));
                    contract.setWithdrawFee(new BigDecimal(0.04));
                    contractDao.save(contract);
                    break;
                case "QBT":
                    contract.setWithdrawLimit(new BigDecimal(67000));
                    contract.setWithdrawDayLimit(new BigDecimal(1500));
                    contract.setWithdrawOneLimit(new BigDecimal(1500));
                    contract.setWithdrawFee(new BigDecimal(2));
                    contractDao.save(contract);
                    break;
                case "TSL":
                    contract.setWithdrawLimit(new BigDecimal(36000));
                    contract.setWithdrawDayLimit(new BigDecimal(18000));
                    contract.setWithdrawOneLimit(new BigDecimal(18000));
                    contract.setWithdrawFee(new BigDecimal(8));
                    contractDao.save(contract);
                    break;
                case "MED":
                    contract.setWithdrawLimit(new BigDecimal(40000));
                    contract.setWithdrawDayLimit(new BigDecimal(20000));
                    contract.setWithdrawOneLimit(new BigDecimal(20000));
                    contract.setWithdrawFee(new BigDecimal(60));
                    contractDao.save(contract);
                case "INK":
                    contract.setWithdrawLimit(new BigDecimal(12000));
                    contract.setWithdrawDayLimit(new BigDecimal(6000));
                    contract.setWithdrawOneLimit(new BigDecimal(6000));
                    contract.setWithdrawFee(new BigDecimal(6));
                    contractDao.save(contract);
                    break;
                case "BOT":
                    contract.setWithdrawLimit(new BigDecimal(4000));
                    contract.setWithdrawDayLimit(new BigDecimal(2000));
                    contract.setWithdrawOneLimit(new BigDecimal(2000));
                    contract.setWithdrawFee(new BigDecimal(3));
                    contractDao.save(contract);
                    break;
                case "HLC":
                    contract.setWithdrawLimit(new BigDecimal(10000));
                    contract.setWithdrawDayLimit(new BigDecimal(5000));
                    contract.setWithdrawOneLimit(new BigDecimal(5000));
                    contract.setWithdrawFee(new BigDecimal(12));
                    contractDao.save(contract);
                    break;
                case "ENT":
                    contract.setWithdrawLimit(new BigDecimal(36000));
                    contract.setWithdrawDayLimit(new BigDecimal(18000));
                    contract.setWithdrawOneLimit(new BigDecimal(18000));
                    contract.setWithdrawFee(new BigDecimal(40));
                    contractDao.save(contract);
                    break;
                case "CFUN":
                    contract.setWithdrawLimit(new BigDecimal(100000));
                    contract.setWithdrawDayLimit(new BigDecimal(50000));
                    contract.setWithdrawOneLimit(new BigDecimal(50000));
                    contract.setWithdrawFee(new BigDecimal(200));
                    contractDao.save(contract);
                    break;
                case "PUT":
                    contract.setWithdrawLimit(new BigDecimal(3000));
                    contract.setWithdrawDayLimit(new BigDecimal(1500));
                    contract.setWithdrawOneLimit(new BigDecimal(1500));
                    contract.setWithdrawFee(new BigDecimal(6));
                    contractDao.save(contract);
                    break;
                case "AWR":
                    contract.setWithdrawLimit(new BigDecimal(100000));
                    contract.setWithdrawDayLimit(new BigDecimal(50000));
                    contract.setWithdrawOneLimit(new BigDecimal(50000));
                    contract.setWithdrawFee(new BigDecimal(80));
                    contractDao.save(contract);
                    break;
                case "SPC":
                    contract.setWithdrawLimit(new BigDecimal(12000));
                    contract.setWithdrawDayLimit(new BigDecimal(6000));
                    contract.setWithdrawOneLimit(new BigDecimal(6000));
                    contract.setWithdrawFee(new BigDecimal(15));
                    contractDao.save(contract);
                    break;
                case "ZAT":
                    contract.setWithdrawLimit(new BigDecimal(2000));
                    contract.setWithdrawDayLimit(new BigDecimal(1000));
                    contract.setWithdrawOneLimit(new BigDecimal(1000));
                    contract.setWithdrawFee(new BigDecimal(2));
                    contractDao.save(contract);
                    break;
                case "VUE":
                    contract.setWithdrawLimit(new BigDecimal(2000));
                    contract.setWithdrawDayLimit(new BigDecimal(1000));
                    contract.setWithdrawOneLimit(new BigDecimal(1000));
                    contract.setWithdrawFee(new BigDecimal(2));
                    contractDao.save(contract);
                    break;
                case "OC":
                    contract.setWithdrawLimit(new BigDecimal(2000));
                    contract.setWithdrawDayLimit(new BigDecimal(1000));
                    contract.setWithdrawOneLimit(new BigDecimal(1000));
                    contract.setWithdrawFee(new BigDecimal(2));
                    contractDao.save(contract);
                    break;
                case "LRQ":
                    contract.setWithdrawLimit(new BigDecimal(2000));
                    contract.setWithdrawDayLimit(new BigDecimal(1000));
                    contract.setWithdrawOneLimit(new BigDecimal(1000));
                    contract.setWithdrawFee(new BigDecimal(2));
                    contractDao.save(contract);
                    break;
                case "WID":
                    contract.setWithdrawLimit(new BigDecimal(2000));
                    contract.setWithdrawDayLimit(new BigDecimal(1000));
                    contract.setWithdrawOneLimit(new BigDecimal(1000));
                    contract.setWithdrawFee(new BigDecimal(2));
                    contractDao.save(contract);
                    break;
                case "EPC":
                    contract.setWithdrawLimit(new BigDecimal(36000));
                    contract.setWithdrawDayLimit(new BigDecimal(18000));
                    contract.setWithdrawOneLimit(new BigDecimal(18000));
                    contract.setWithdrawFee(new BigDecimal(2));
                    contractDao.save(contract);
                    break;
            }
        }

    }

    @Override
    public List<Contract> getContractsByType(Integer type) {
        List<Contract> contracts = new ArrayList<>();
        List<Contract> contractList = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        contractList.forEach(contract -> {
            if (type.equals(contract.getType())){
                contracts.add(contract);
            }
        });
        return contracts;
    }
}
