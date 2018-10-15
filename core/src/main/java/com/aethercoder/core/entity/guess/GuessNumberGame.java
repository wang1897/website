package com.aethercoder.core.entity.guess;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Entity
@Table(name = "guess_number_game")
@ApiModel(value="竞猜活动",description="竞猜活动")
public class GuessNumberGame extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Column(name = "game_id")
    private Long gameId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "game_start_time")
    private Date gameStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "game_end_time")
    private Date gameEndTime;

    @Column(name = "luck_number")
    private String luckNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "luck_time")
    private Date luckTime;

    @Column(name = "join_number")
    private Integer joinNumber;

    @Column(name = "is_delete")
    private Boolean isDelete = false;


    @Column(name = "is_show")
    private Boolean isShow = false;

    @Column(name = "zh_name")
    private String zhName;

    @Column(name = "ko_name")
    private String koName;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "begin_block")
    private Integer beginBlock;

    @Column(name = "end_block")
    private Integer endBlock;

    @Column(name = "luck_block")
    private Integer luckBlock;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "guessId")
    Set<GuessUnit> guessUnits = new HashSet(0);

    @Transient
    private String type;

    @Transient
    private Integer level;

    @Transient
    private String accountNo;

    @Transient
    private String accountName;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @ApiModelProperty(position = 1, value="游戏id")
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }


    @ApiModelProperty(position = 6, value="活动开始时间")
    public Date getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(Date eventStartTime) {
        this.gameStartTime = eventStartTime;
    }

    @ApiModelProperty(position = 7, value="活动结束时间")
    public Date getGameEndTime() {
        return gameEndTime;
    }

    public void setGameEndTime(Date gameEndTime) {
        this.gameEndTime = gameEndTime;
    }

    @ApiModelProperty(position = 10, value="中奖数字")
    public String getLuckNumber() {
        return luckNumber;
    }

    public void setLuckNumber(String luckNumber) {
        this.luckNumber = luckNumber;
    }

    @ApiModelProperty(position = 11, value="开奖时间")
    public Date getLuckTime() {
        return luckTime;
    }

    public void setLuckTime(Date luckTime) {
        this.luckTime = luckTime;
    }

    @ApiModelProperty(position = 11, value="参与人数")
    public Integer getJoinNumber() {
        return joinNumber;
    }

    public void setJoinNumber(Integer joinNumber) {
        this.joinNumber = joinNumber;
    }

    @ApiModelProperty(position = 12, value = "是否失效0-未失效／1失效")
    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
    @ApiModelProperty(position = 13, value = "是否显示0-不显示／1显示")
    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }
    @ApiModelProperty(position = 14, value = "中文名称")
    public String getZhName() {
        return zhName;
    }

    public void setZhName(String zhName) {
        this.zhName = zhName;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Integer getBeginBlock() {
        return beginBlock;
    }

    public void setBeginBlock(Integer beginBlock) {
        this.beginBlock = beginBlock;
    }

    public Integer getEndBlock() {
        return endBlock;
    }

    public void setEndBlock(Integer endBlock) {
        this.endBlock = endBlock;
    }

    public Integer getLuckBlock() {
        return luckBlock;
    }

    public void setLuckBlock(Integer luckBlock) {
        this.luckBlock = luckBlock;
    }

    public Set<GuessUnit> getGuessUnits() {
        return guessUnits;
    }

    public void setGuessUnits(Set<GuessUnit> guessUnits) {
        this.guessUnits = guessUnits;
    }

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
}
