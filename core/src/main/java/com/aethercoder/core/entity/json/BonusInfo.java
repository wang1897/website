package com.aethercoder.core.entity.json;

import java.math.BigDecimal;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/3
 * @modified By:
 */
public class BonusInfo {
    private Integer drawLevel;

    private Integer winnerCount;

    private BigDecimal avgAmount;

    private Long unit;

    private String zhName;

    private String enName;

    private String koName;

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getKoName() {
        return koName;
    }

    public void setKoName(String koName) {
        this.koName = koName;
    }

    public Integer getDrawLevel() {
        return drawLevel;
    }

    public void setDrawLevel(Integer drawLevel) {
        this.drawLevel = drawLevel;
    }

    public Integer getWinnerCount() {
        return winnerCount;
    }

    public void setWinnerCount(Integer winnerCount) {
        this.winnerCount = winnerCount;
    }

    public BigDecimal getAvgAmount() {
        return avgAmount;
    }

    public void setAvgAmount(BigDecimal avgAmount) {
        this.avgAmount = avgAmount;
    }
}
