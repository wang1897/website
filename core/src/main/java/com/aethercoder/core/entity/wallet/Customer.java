package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/8
 * @modified By:
 */
@Entity
@Table(name = "t_customer")
public class Customer extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Column(name = "logo")
    private String logo;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "address")
    private String address;

    @Column(name = "country_info_id")
    private Long countryInfoId;

    @Column(name = "customer_des")
    private String customerDes;

    @Column(name = "status")
    private Integer status;

    @Column(name = "customer_no")
    private String customerNo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "customer_uuid")
    private String customerUuid;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "generate_string")
    private String generateString;

    @Column(name = "credit_card")
    private String creditCard;

    @Column(name = "time_zone_id")
    private Long timeZoneId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phoneNumber;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @Transient
    private String language;

    @Transient
    private String country;

    @Transient
    private String areaCode;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Transient
    private String countryName;

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Column(name = "cancellation_time")
    private Date cancellationTime;

    @Column(name = "last_login_time")
    private Date lastLoginTime;

    @Column(name = "qr_code_pic")
    private String qrCodePic;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "qr_code_url")
    private String qrCodeUrl;

    @Column(name = "first_login")
    private Boolean firstLogin;

    @Transient
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public Date getCancellationTime() {
        return cancellationTime;
    }

    public void setCancellationTime(Date cancellationTime) {
        this.cancellationTime = cancellationTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getQrCodePic() {
        return qrCodePic;
    }

    public void setQrCodePic(String qrCodePic) {
        this.qrCodePic = qrCodePic;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getCountryInfoId() {
        return countryInfoId;
    }

    public void setCountryInfoId(Long contactInfoId) {
        this.countryInfoId = contactInfoId;
    }

    public String getCustomerDes() {
        return customerDes;
    }

    public void setCustomerDes(String customerDes) {
        this.customerDes = customerDes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCustomerUuid() {
        return customerUuid;
    }

    public void setCustomerUuid(String customerUuid) {
        this.customerUuid = customerUuid;
    }

    public String getGenerateString() {
        return generateString;
    }

    public void setGenerateString(String generateString) {
        this.generateString = generateString;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(String creditCard) {
        this.creditCard = creditCard;
    }

    public Long getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(Long timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    @JsonIgnore(value = false)
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    @JsonIgnore(value = false)
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    @Override
    public String toString() {
        return "Customer{" +
                "logo='" + logo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", address='" + address + '\'' +
                ", countryInfoId=" + countryInfoId +
                ", customerDes='" + customerDes + '\'' +
                ", status=" + status +
                ", customerNo='" + customerNo + '\'' +
                ", customerUuid='" + customerUuid + '\'' +
                ", generateString='" + generateString + '\'' +
                ", creditCard='" + creditCard + '\'' +
                ", timeZoneId=" + timeZoneId +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", createTime=" + createTime +
                ", language='" + language + '\'' +
                ", country='" + country + '\'' +
                ", countryName='" + countryName + '\'' +
                ", cancellationTime=" + cancellationTime +
                ", lastLoginTime=" + lastLoginTime +
                ", qrCodePic='" + qrCodePic + '\'' +
                ", qrCodeUrl='" + qrCodeUrl + '\'' +
                '}';
    }
}
