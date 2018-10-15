package com.aethercoder.core.entity.json;

import java.math.BigDecimal;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/1
 * @modified By:
 */

public class GuessRanking {
    private Integer rank;
    private BigDecimal amount;
    private  String name;
    private int winCount;
    private int joinNum;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getJoinNum() {
        return joinNum;
    }

    public void setJoinNum(int joinNum) {
        this.joinNum = joinNum;
    }
}
