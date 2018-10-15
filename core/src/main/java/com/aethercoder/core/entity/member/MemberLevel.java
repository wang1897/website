package com.aethercoder.core.entity.member;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/1/23
 * @modified By:
 */
@Entity
@Table(name = "member_level")
public class MemberLevel extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "level")
    private Integer level;

    @Column(name = "money")
    private Integer money;

    @Column(name = "icon")
    private String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Transient
    private Integer moneyMax;

    public void setMoney(Integer money) {
        this.money = money;
    }

    public void setLevel(Integer level) {

        this.level = level;
    }

    public Integer getMoney() {

        return money;
    }

    public Integer getLevel() {

        return level;
    }

    public Integer getMoneyMax() {
        return moneyMax;
    }

    public void setMoneyMax(Integer moneyMax) {
        this.moneyMax = moneyMax;
    }

    @Override
    public String toString() {
        return "{" +
                "level=" + level +
                ", money=" + money +
                ", moneyMax=" + moneyMax +
                ", icon=" + icon +
                '}';
    }
}
