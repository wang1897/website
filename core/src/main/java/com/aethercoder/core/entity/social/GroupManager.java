package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @auther Guo Feiyan
 * @date 2017/12/6 上午11:18
 */
@Entity
@Table(name = "group_manager", schema = "qbao_schema")
@ApiModel(value="申请成为群主对象",description="申请成为群主")
public class GroupManager extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Basic
    @Column(name = "account_no")
    private String accountNo;

    @Basic
    @Column(name = "group_name")
    private String groupName;

    @Basic
    @Column(name = "address")
    private String address;

    @Basic
    @Column(name = "transfer_status")
    private Integer transferStatus;

    @Basic
    @Column(name = "gear_type")
    private Integer gearType;

    @Basic
    @Column(name = "bonus")
    private BigDecimal bonus;

    @ApiModelProperty(position = 1, value="QBAO用户id")
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @ApiModelProperty(position = 2, value="群名称")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @ApiModelProperty(position = 3, value="QBAO用户地址 默认主地址")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @ApiModelProperty(position = 4, value="转账状态 0-未转帐／1-已转账")
    public Integer getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(Integer transferStatus) {
        this.transferStatus = transferStatus;
    }

    @ApiModelProperty(position = 5, value="奖励档位\n" +
            "0-无奖励\n" +
            "1-100QBT\n" +
            "2-300QBT\n" +
            "3-500QBT\n" +
            "4-1000QBT")
    public Integer getGearType() {
        return gearType;
    }

    public void setGearType(Integer gearType) {
        this.gearType = gearType;
    }

    @ApiModelProperty(position = 6, value="额外奖励金额 \\n若有多次 累加金额")
    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }
}
