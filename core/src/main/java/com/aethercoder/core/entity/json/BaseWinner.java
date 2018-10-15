package com.aethercoder.core.entity.json;

import com.aethercoder.core.entity.guess.GuessAward;
import com.aethercoder.core.entity.guess.GuessUnit;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/2
 * @modified By:
 */
public class BaseWinner {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date drawTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date luckTime;

    private Integer joinNumber;

    private String luckNumber;

    private String drawNumber;

    private Integer drawLevel;

    private String header;

    private String zhName;

    private String koName;

    private String enName;

    public String getKoName() {
        return koName;
    }

    public void setKoName(String koName) {
        this.koName = koName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    private List<GuessAward> guessAwards;

    private Set<GuessUnit> guessUnitSet;

    public Set<GuessUnit> getGuessUnitSet() {
        return guessUnitSet;
    }

    public void setGuessUnitSet(Set<GuessUnit> guessUnitSet) {
        this.guessUnitSet = guessUnitSet;
    }

    public List<GuessAward> getGuessAwards() {
        return guessAwards;
    }

    public void setGuessAwards(List<GuessAward> guessAwards) {
        this.guessAwards = guessAwards;
    }

    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }

    public Date getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(Date drawTime) {
        this.drawTime = drawTime;
    }

    public Date getLuckTime() {
        return luckTime;
    }

    public void setLuckTime(Date luckTime) {
        this.luckTime = luckTime;
    }

    public String getLuckNumber() {
        return luckNumber;
    }

    public void setLuckNumber(String luckNumber) {
        this.luckNumber = luckNumber;
    }

    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    public Integer getDrawLevel() {
        return drawLevel;
    }

    public void setDrawLevel(Integer drawLevel) {
        this.drawLevel = drawLevel;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }


    public Integer getJoinNumber() {
        return joinNumber;
    }

    public void setJoinNumber(Integer joinNumber) {
        this.joinNumber = joinNumber;
    }

    @Override
    public String toString() {
        return "BaseWinner{" +
                "drawTime=" + drawTime +
                ", luckTime=" + luckTime +
                ", joinNumber=" + joinNumber +
                ", luckNumber='" + luckNumber + '\'' +
                ", drawNumber='" + drawNumber + '\'' +
                ", drawLevel=" + drawLevel +
                ", header='" + header + '\'' +
                '}';
    }
}
