package com.aethercoder.core.entity.json;

import com.aethercoder.core.contants.WalletConstants;

/**
 * @auther Guo Feiyan
 * @date 2018/3/22 下午5:52
 */
public class OrderCloseTitle {

    /*---------------标题名称-----------------------------*/

    private String orderNo;

    private String customerNo;

    private String cleanDate;

    private String orderDate;

    private String accountNo;

    private String type;

    private String unit;

    private String amount;

    private String tokenUnit;

    private String tokenAmount;



    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getCleanDate() {
        return cleanDate;
    }

    public void setCleanDate(String cleanDate) {
        this.cleanDate = cleanDate;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(String tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public String getTokenUnit() {
        return tokenUnit;
    }

    public void setTokenUnit(String tokenUnit) {
        this.tokenUnit = tokenUnit;
    }

    public OrderCloseTitle(String langusge) {
        if (langusge.equals(WalletConstants.LANGUAGE_TYPE_ZH)) {
            this.orderNo = "订单号";
            this.customerNo = "商户编号";
            this.cleanDate = "清算日期";
            this.orderDate = "交易时间";
            this.accountNo = "用户ID";
            this.type = "交易类型";
            this.unit = "代币币种";
            this.amount = "代币金额";
            this.tokenUnit = "法币币种";
            this.tokenAmount = "法币金额";

        }
        if (langusge.equals(WalletConstants.LANGUAGE_TYPE_EN)) {
            this.orderNo = "Order Number";
            this.customerNo = "Customer Number";
            this.cleanDate = "Settlement Date";
            this.orderDate = "Order Date";
            this.accountNo = "User ID";
            this.type = "Transaction Type";
            this.unit = "Order Token";
            this.amount = "Order Amount";
            this.tokenUnit = "Currency Selection";
            this.tokenAmount = "Amount in Currency";

        }
        if (langusge.equals(WalletConstants.LANGUAGE_TYPE_KO)) {
            this.orderNo = "주문 번호";
            this.customerNo = "고객 번호";
            this.cleanDate = "결산일자";
            this.orderDate = "주문 시간";
            this.accountNo = "사용자 ID";
            this.type = "거래 유형";
            this.unit = "거래 토큰";
            this.amount = "거래 금액";
            this.tokenUnit = "기축 통화 종류";
            this.tokenAmount = "기축 통화 금액";

        }
    }
}
