package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.service.QtumService;
import com.aethercoder.foundation.exception.BlockChainException;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by hepengfei on 22/11/2017.
 */
@Service
public class QtumServiceImpl implements QtumService {

    @Value("${qtum.url}")
    private String url;

    @Value("${qtum.httpUrl}")
    private String httpUrl;

    @Value("${qtum.username}")
    private String username;

    @Value("${qtum.password}")
    private String password;

    @Override
    public List getUnspentByAddresses(List<String> addresses) {
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("addresses", addresses);

        List<Map<String, Object>> paramList = new ArrayList<>();
        paramList.add(addressMap);

        List list = (List)callQtumService("getaddressutxos", paramList);

        List mempool = getMempool(addresses);
        List<String> prevtxidList = new ArrayList<>();
        if (mempool != null && !mempool.isEmpty()) {
            for(Object obj: mempool) {
                HashMap map = (HashMap)obj;
                if (map.get("prevtxid") == null) {
                    Integer index = (Integer)map.get("index");
                    HashMap transaction = this.getTransaction((String)map.get("txid"));
                    if (transaction != null) {
                        List voutList = (List)transaction.get("vout");
                        Map vout = (Map)voutList.get(index);
                        Map scriptPubKey = (Map)vout.get("scriptPubKey");
                        map.put("script", scriptPubKey.get("hex"));
                        map.put("outputIndex", index);
                        list.add(map);
                    }
                } else {
                    prevtxidList.add((String)map.get("prevtxid"));
                }
            }
        }

        HashMap info = (HashMap)callQtumService("getinfo", null);
        Integer blocks = (Integer)info.get("blocks");

        for(Iterator it = list.iterator(); it.hasNext();) {
            HashMap map = (HashMap)it.next();
            String txid = (String)map.get("txid");
            if (prevtxidList.contains(txid)) {
                it.remove();
                continue;
            }

            if (map.get("height") != null && (Integer) map.get("height") >= 0) {
                map.put("confirmations", blocks - (Integer) map.get("height") + 1);
            } else {
                map.put("confirmations", 0);
                map.put("height", -1);
            }
        }

        return list;
    }

    @Override
    public List getConfirmUnspentByAddresses(List<String> addresses) {
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("addresses", addresses);

        List<Map<String, Object>> paramList = new ArrayList<>();
        paramList.add(addressMap);

        List list = (List)callQtumService("getaddressutxos", paramList);

        List mempool = getMempool(addresses);
        List<String> prevtxidList = new ArrayList<>();
        if (mempool != null && !mempool.isEmpty()) {
            for(Object obj: mempool) {
                HashMap map = (HashMap)obj;
                if (map.get("prevtxid") == null) {
                    Integer index = (Integer)map.get("index");
                    HashMap transaction = this.getTransaction((String)map.get("txid"));
                    if (transaction != null) {
                        List voutList = (List)transaction.get("vout");
                        Map vout = (Map)voutList.get(index);
                        Map scriptPubKey = (Map)vout.get("scriptPubKey");
                        map.put("script", scriptPubKey.get("hex"));
                        map.put("outputIndex", index);
                    }
                } else {
                    prevtxidList.add((String)map.get("prevtxid"));
                }
            }
        }

        HashMap info = (HashMap)callQtumService("getinfo", null);
        Integer blocks = (Integer)info.get("blocks");

        for(Iterator it = list.iterator(); it.hasNext();) {
            HashMap map = (HashMap)it.next();
            String txid = (String)map.get("txid");
            if (prevtxidList.contains(txid)) {
                it.remove();
                continue;
            }

            if (map.get("height") != null && (Integer) map.get("height") >= 0) {
                map.put("confirmations", blocks - (Integer) map.get("height") + 1);
            } else {
                map.put("confirmations", 0);
                map.put("height", -1);
            }
        }

        return list;
    }

    @Override
    public HashMap getInfo() {
        return (HashMap) callQtumService("getinfo", null);
    }

    @Override
    public List getUnspentByAddresses(String addressesJson) {
        List<String> addresses = BeanUtils.jsonToList(addressesJson, String.class);
        return this.getUnspentByAddresses(addresses);
    }

    @Override
    public HashMap getUtxo(String utxoId, Integer outputIndex) {
        List<Object> list = new ArrayList<>();
        list.add(utxoId);
        list.add(outputIndex);

        return (HashMap)callQtumService("gettxout", list);
    }

    @Override
    public List getTransHistoryByAddress(List<String> addresses, String contractAddress, Integer limit, Integer offset, Long startBlock, Long endBlock) {
        String params = "";
        for(int i = 0; i < addresses.size(); i++) {
            String address = addresses.get(i);
            if (i != 0) {
                params += "&";
            }
            params += "addresses=" + address;
        }
        if (contractAddress != null) {
            params += "&contractAddress=" + contractAddress;
        }
        if (startBlock != null) {
            params += "&startBlock=" + startBlock;
        }
        if (endBlock != null) {
            params += "&endBlock=" + endBlock;
        }

        String url = httpUrl + "/qtum/history/transaction/" + limit + "/" + offset;// + "?" + params;

        List result = NetworkUtil.callHttpReq(HttpMethod.GET, url, params, null, List.class);
        return result;
    }
    @Override
    public List getTransHistoryByAddress(String addressesJson, String contractAddress, Integer limit, Integer offset, Long startBlock, Long endBlock) {
        List<String> addresses = BeanUtils.jsonToList(addressesJson, String.class);
        return getTransHistoryByAddress(addresses, contractAddress, limit, offset, startBlock, endBlock);


    }

    @Override
    public List<HashMap> callContract(String contract, List<String> params) {
        List<HashMap> result = new ArrayList<>();
        for (String param: params) {
            List<Object> list = new ArrayList<>();
            list.add(contract);
            list.add(param);
            HashMap map = (HashMap)callQtumService("callcontract", list);
            map.put("hash", param);
            result.add(map);
        }

        return result;

    }

    @Override
    public Double estimateFee(Integer nBlocks) {
        List<Object> list = new ArrayList<>();
        list.add(nBlocks);

        Double d = new Double(callQtumService("estimatefee", list).toString());
        if (d < 0.004) {
            d = 0.0045;
        }
        return d;
    }

    @Override
    public String sendRawTransaction(String rawTransaction) {
        List<Object> list = new ArrayList<>();
        list.add(rawTransaction);

        return (String)callQtumService("sendrawtransaction", list);
    }

    @Override
    public HashMap getTransaction(String txhash) {
        List txidParamList = new ArrayList();
        txidParamList.add(txhash);
        txidParamList.add(true);
        HashMap rawTraction = (HashMap)callQtumService("getrawtransaction", txidParamList);
        return rawTraction;
    }

    @Override
    public List getEventLog(Long blockStart, Long blockEnd, List<String> contractAddrList) {
        Map<String, List> addressMap = new HashMap<>();
        addressMap.put("addresses", contractAddrList);

        List<Object> paramsList = new ArrayList<>();
        paramsList.add(blockStart);
        paramsList.add(blockEnd);
        paramsList.add(addressMap);
        List result = (List)this.callQtumService("searchlogs", paramsList);
        return result;
    }

    @Override
    public HashMap getDGPInfo() {
        return (HashMap)callQtumService("getdgpinfo", null);
    }

    public List<HashMap> getMempool(List<String> addresses) {
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("addresses", addresses);

        List<Map<String, Object>> paramList = new ArrayList<>();
        paramList.add(addressMap);

        List list = (List) callQtumService("getaddressmempool", paramList);
        return list;
    }

    private Object callQtumService(String method, Object params) {
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("method", method);
        if (params != null) {
            bodyMap.put("params", params);
        }

        Map<String, String> header = new HashMap<>();
        NetworkUtil.addAuth(username, password, header);
        HashMap result = null;
        try {
            result = NetworkUtil.callHttpReq(HttpMethod.POST, url, BeanUtils.objectToJson(bodyMap), header, HashMap.class);
        } catch (HttpServerErrorException e) {
            throw new BlockChainException(e.getResponseBodyAsString(), ErrorCode.QTUM_ERROR);
        }
        return result.get("result");
    }

    @Override
    public BigDecimal convertQtumAmount(BigDecimal satoshis) {
        return satoshis.divide(new BigDecimal(10).pow(8));
    }


    @Override
    public BigDecimal covertTokenAmount(BigDecimal amount, Integer decimal) {
        return amount.divide(new BigDecimal(10).pow(decimal),6,BigDecimal.ROUND_DOWN);
    }
}
