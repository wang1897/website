package com.aethercoder.core.entity.error;

import com.aethercoder.core.entity.BaseEntity;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by hepengfei on 22/03/2018.
 */
@Entity
@Table(name = "t_qtum_tx_error")
public class QtumTxError extends BaseEntity{
    @Column(name = "tx_hash")
    private String txHash;

    @Column(name = "tx_raw")
    @Type(type="text")
    private String txRaw;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "fee")
    private String fee;

    @Column(name = "gas_limit")
    private String gasLimit;

    @Column(name = "gas_price")
    private String gasPrice;

    @Column(name = "fee_per_kb")
    private String feePerKb;

    @Column(name = "from_utxo")
    private String fromUtxo;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "device_version")
    private String deviceVersion;

    @Column(name = "device_model")
    private String deviceModel;

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getTxRaw() {
        return txRaw;
    }

    public void setTxRaw(String txRaw) {
        this.txRaw = txRaw;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getFeePerKb() {
        return feePerKb;
    }

    public void setFeePerKb(String feePerKb) {
        this.feePerKb = feePerKb;
    }

    public String getFromUtxo() {
        return fromUtxo;
    }

    public void setFromUtxo(String fromUtxo) {
        this.fromUtxo = fromUtxo;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }
}
