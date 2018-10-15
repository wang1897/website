package com.aethercoder.activity.entity.guessGamble;

import com.aethercoder.foundation.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by hepengfei on 27/02/2018.
 */
@Entity
@Table(name = "t_guess_gamble")
public class GuessGamble extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "option1")
    private String option1;

    @Column(name = "option2")
    private String option2;

    @Column(name = "open_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date openTime;

    @Column(name = "close_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date closeTime;

    @Column(name = "luck_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date luckTime;

    @Column(name = "luck_option")
    private Character luckOption;

    @Column(name = "status")
    private Integer status = 0;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "is_min")
    private Boolean isMin;

    @Column(name = "option1_number")
    private Integer option1_number;

    @Column(name = "option2_number")
    private Integer option2_number;

    @Column(name = "option1_amount")
    private BigDecimal option1_amount;

    @Column(name = "option2_amount")
    private BigDecimal option2_amount;

    @Column(name = "game_id")
    private Long gameId;

    @Transient
    private String title_en;
    @Transient
    private String title_ko;
    @Transient
    private String content_en;
    @Transient
    private String content_ko;
    @Transient
    private String option1_en;
    @Transient
    private String option1_ko;
    @Transient
    private String option2_en;
    @Transient
    private String option2_ko;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public Date getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Date openTime) {
        this.openTime = openTime;
    }

    public Date getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Date closeTime) {
        this.closeTime = closeTime;
    }

    public Date getLuckTime() {
        return luckTime;
    }

    public void setLuckTime(Date luckTime) {
        this.luckTime = luckTime;
    }

    public Character getLuckOption() {
        return luckOption;
    }

    public void setLuckOption(Character luckOption) {
        this.luckOption = luckOption;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getIsMin() {
        return isMin;
    }

    public void setIsMin(Boolean isMin) {
        this.isMin = isMin;
    }

    public Integer getOption1_number() {
        return option1_number;
    }

    public void setOption1_number(Integer option1_number) {
        this.option1_number = option1_number;
    }

    public Integer getOption2_number() {
        return option2_number;
    }

    public void setOption2_number(Integer option2_number) {
        this.option2_number = option2_number;
    }

    public BigDecimal getOption1_amount() {
        return option1_amount;
    }

    public void setOption1_amount(BigDecimal option1_amount) {
        this.option1_amount = option1_amount;
    }

    public BigDecimal getOption2_amount() {
        return option2_amount;
    }

    public void setOption2_amount(BigDecimal option2_amount) {
        this.option2_amount = option2_amount;
    }

    public String getTitle_en() {
        return title_en;
    }

    public void setTitle_en(String title_en) {
        this.title_en = title_en;
    }

    public String getTitle_ko() {
        return title_ko;
    }

    public void setTitle_ko(String title_ko) {
        this.title_ko = title_ko;
    }

    public String getContent_en() {
        return content_en;
    }

    public void setContent_en(String content_en) {
        this.content_en = content_en;
    }

    public String getContent_ko() {
        return content_ko;
    }

    public void setContent_ko(String content_ko) {
        this.content_ko = content_ko;
    }

    public String getOption1_en() {
        return option1_en;
    }

    public void setOption1_en(String option1_en) {
        this.option1_en = option1_en;
    }

    public String getOption1_ko() {
        return option1_ko;
    }

    public void setOption1_ko(String option1_ko) {
        this.option1_ko = option1_ko;
    }

    public String getOption2_en() {
        return option2_en;
    }

    public void setOption2_en(String option2_en) {
        this.option2_en = option2_en;
    }

    public String getOption2_ko() {
        return option2_ko;
    }

    public void setOption2_ko(String option2_ko) {
        this.option2_ko = option2_ko;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
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
        return "GuessGamble{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", option1='" + option1 + '\'' +
                ", option2='" + option2 + '\'' +
                ", openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", luckTime=" + luckTime +
                ", luckOption=" + luckOption +
                ", status=" + status +
                ", amount=" + amount +
                ", isMin=" + isMin +
                ", option1_number=" + option1_number +
                ", option2_number=" + option2_number +
                ", option1_amount=" + option1_amount +
                ", option2_amount=" + option2_amount +
                ", gameId=" + gameId +
                ", title_en='" + title_en + '\'' +
                ", title_ko='" + title_ko + '\'' +
                ", content_en='" + content_en + '\'' +
                ", content_ko='" + content_ko + '\'' +
                ", option1_en='" + option1_en + '\'' +
                ", option1_ko='" + option1_ko + '\'' +
                ", option2_en='" + option2_en + '\'' +
                ", option2_ko='" + option2_ko + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
