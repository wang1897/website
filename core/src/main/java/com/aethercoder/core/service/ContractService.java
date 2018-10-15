package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.Contract;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by GuoFeiYan on 2017/8/30.
 */
public interface ContractService {

    List<Contract> findContractAllForIOS(Integer type);

    List<Contract> findContractsAll();

    Page<Contract> findContractsAll(Integer page, Integer size, String name, Boolean isShow, Boolean isDelete,Integer type);

    List<Contract> findAllAvailableContracts(Integer type);

    Contract findContractByName(String name,Integer type);

    Contract findContractAndIsDeleteIsFalseByName(String name);

    Contract findContractById(Long id);

    Contract saveContract(Contract contract);

    Contract updateContract(Contract contract);

    void deleteContract(Long contractId, boolean isDelete);

    String getByContractAddress(String contractName);

    Contract findContractByAddress(String contractAddress);

    void updateContractByApiAddress();

    void updateContractSequence(String[] contractIdList);

    List<Contract> getTokenAmountList();

    void setDefaultWithdraw();

    List<Contract> getContractsByType(Integer type);
}

