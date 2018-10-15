package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;
import com.aethercoder.core.entity.member.MemberLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hepengfei on 2017/8/30.
 */
@Entity
@Table(name = "wallet_account")
public class Account extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "account_no")
    private String accountNo;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "accountId")
    Set<Address> addresses = new HashSet(0);

    @Column(name = "header")
    private String header;

    public String getBiggerHeader() {
        return biggerHeader;
    }

    public void setBiggerHeader(String biggerHeader) {
        this.biggerHeader = biggerHeader;
    }

    @Column(name = "bigger_header")
    private String biggerHeader;

    @Column(name = "user_name")
    private String userName     ;

    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password     ;

    @Column(name = "email")
    private String email        ;

    @Column(name = "activate_type",nullable = false,columnDefinition="INT(1) default 0")
    private String activateType = "0";

    /*1:安卓 0：IOS 2:Web*/
    @Column(name = "source_type", columnDefinition="INT(1) default 0")
    private String sourceType = "0";

    @Column(name = "rong_token")
    private String rongToken;

    @Column(name = "rong_version")
    private Long rongVersion;

    @Column(name = "share_code")
    private String shareCode        ;

    @Column(name = "invite_code")
    private String inviteCode        ;

    @Column(name = "receive_number")
    private Integer receiveNumber        ;

    @Column(name = "invited_daily")
    private Integer invitedDaily        ;

    @Column(name = "new_type")
    private Boolean newType;

    @Column(name = "invite_qbe")
    private BigDecimal inviteQbe ;

    @Version
    @Column(name = "version")
    private Long versionForLock;

    @Column(name = "level")
    private Long level;

    @Column(name = "assets")
    private BigDecimal assets;

    @Column(name = "language")
    private String language;

    @Column(name = "sys_black")
    private Boolean sysBlack;

    @Column(name = "level_status")
    private Boolean levelStatus;

    @Column(name = "is_daka")
    private Boolean isDaka;

    public Boolean getAuthority() {
        return authority;
    }

    public void setAuthority(Boolean authority) {
        this.authority = authority;
    }

    @Column(name = "authority")
    private Boolean authority;

    public Boolean getLevelStatus() {
        return levelStatus;
    }

    public void setLevelStatus(Boolean levelStatus) {
        this.levelStatus = levelStatus;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "login_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date loginTime;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @Column(name = "words")
    private String words;

    @Transient
    private Integer inviteCodeCount;

    @Transient
    private MemberLevel memberLevel;

    public MemberLevel getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(MemberLevel memberLevel) {
        this.memberLevel = memberLevel;
    }

    @Transient
    private Integer countAccountsByAll;

    @Transient
    private Integer countAccountsByDay;

    @Transient
    private Integer countAccountsByMouth;

    @Transient
    private Integer countAccountsByMouthOfGrowth;

    @Transient
    private Integer allInviteCodeCount;

    @Transient
    private String captchaToken;

    @Transient
    private String captchaCode;

    @Transient
    private String deviceId;

    @Transient
    private BigDecimal ethAssets;

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Set<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<Address> addressSet) {
        this.addresses = addressSet;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getWords() {
        return words;
    }

    @JsonIgnore
    public void setWords(String words) {
        this.words = words;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivateType() {
        return activateType;
    }

    public void setActivateType(String activateType) {
        this.activateType = activateType;
    }

    public String getRongToken() {
        return rongToken;
    }

    public void setRongToken(String rongToken) {
        this.rongToken = rongToken;
    }

    public Long getRongVersion() {
        return rongVersion;
    }

    public void setRongVersion(Long rongVersion) {
        this.rongVersion = rongVersion;
    }

    public Integer getReceiveNumber() {
        return receiveNumber;
    }

    public void setReceiveNumber(Integer receiveNumber) {
        this.receiveNumber = receiveNumber;
    }

    public Integer getInviteCodeCount() {
        return inviteCodeCount;
    }

    public void setInviteCodeCount(Integer inviteCodeCount) {
        this.inviteCodeCount = inviteCodeCount;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getNewType() {
        return newType;
    }

    public void setNewType(Boolean newType) {
        this.newType = newType;
    }

    public Long getVersionForLock() {
        return versionForLock;
    }

    public void setVersionForLock(Long versionForLock) {
        this.versionForLock = versionForLock;
    }

    public Integer getAllInviteCodeCount() {
        return allInviteCodeCount;
    }

    public void setAllInviteCodeCount(Integer allInviteCodeCount) {
        this.allInviteCodeCount = allInviteCodeCount;
    }

    public Integer getInvitedDaily() {
        return invitedDaily;
    }

    public void setInvitedDaily(Integer invitedDaily) {
        this.invitedDaily = invitedDaily;
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

    public BigDecimal getInviteQbe() {
        return inviteQbe;
    }

    public void setInviteQbe(BigDecimal inviteQbe) {
        this.inviteQbe = inviteQbe;
    }

    public Integer getCountAccountsByAll() {
        return countAccountsByAll;
    }

    public void setCountAccountsByAll(Integer countAccountsByAll) {
        this.countAccountsByAll = countAccountsByAll;
    }

    public Integer getCountAccountsByDay() {
        return countAccountsByDay;
    }

    public void setCountAccountsByDay(Integer countAccountsByDay) {
        this.countAccountsByDay = countAccountsByDay;
    }

    public Integer getCountAccountsByMouth() {
        return countAccountsByMouth;
    }

    public void setCountAccountsByMouth(Integer countAccountsByMouth) {
        this.countAccountsByMouth = countAccountsByMouth;
    }

    public Integer getCountAccountsByMouthOfGrowth() {
        return countAccountsByMouthOfGrowth;
    }

    public void setCountAccountsByMouthOfGrowth(Integer countAccountsByMouthOfGrowth) {
        this.countAccountsByMouthOfGrowth = countAccountsByMouthOfGrowth;
    }

    public Long getLevel() {
        return level;
    }

    public void setLevel(Long level) {
        this.level = level;
    }

    public BigDecimal getAssets() {
        return assets;
    }

    public void setAssets(BigDecimal assets) {
        this.assets = assets;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Boolean getSysBlack() {
        return sysBlack;
    }

    public void setSysBlack(Boolean sysBlack) {
        this.sysBlack = sysBlack;
    }

    public Boolean getIsDaka() {
        return isDaka;
    }

    public void setIsDaka(Boolean isDaka) {
        this.isDaka = isDaka;
    }


    @Override
    public String toString() {
        return this.accountNo;
    }

    public BigDecimal getEthAssets() {
        return ethAssets;
    }

    public void setEthAssets(BigDecimal ethAssets) {
        this.ethAssets = ethAssets;
    }
}
