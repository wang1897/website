package com.aethercoder.core.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hepengfei on 22/11/2017.
 */
public interface QtumService {
    List getUnspentByAddresses(String addressesJson);

    List getUnspentByAddresses(List<String> addressList);

    List getTransHistoryByAddress(String addressesJson, String contractAddress, Integer limit, Integer offset, Long startBlock, Long endBlock);
    List getTransHistoryByAddress(List<String> addresses, String contractAddress, Integer limit, Integer offset, Long startBlock, Long endBlock);

    HashMap getUtxo(String utxoId, Integer outputIndex);

    List getConfirmUnspentByAddresses(List<String> addresses);

    HashMap getInfo();

    List<HashMap> callContract(String contract, List<String> param);

    Double estimateFee(Integer nBlocks);

    String sendRawTransaction(String rawTransaction);

    HashMap getDGPInfo();

    HashMap getTransaction(String txhash);

    BigDecimal convertQtumAmount(BigDecimal satoshis);

    BigDecimal covertTokenAmount(BigDecimal amount, Integer decimal);

    List getEventLog(Long blockStart, Long blockEnd, List<String> contractAddrList);
}
