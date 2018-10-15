package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/9
 * @modified By:
 */
@Entity
@Table(name = "inviting_reward")
public class InvitingReward extends BaseEntity {
    private static final long serialVersionUID = -1L;

    private String rewardName;

    private BigDecimal rewardAmount;

    private Long unit;

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }
}
