package com.aethercoder.core.entity.ticket;
/**
 * @author lilangfeng
 * @date 2018/01/03
 **/

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;
import org.apache.commons.codec.binary.Base64;

@Entity

@Table(name = "ticket")
public class Ticket extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "account_no")
    private String accountNo;
    @Column(name = "type")
    private Long type;
    @Column(name = "title")
    private String title;
    @Column(name = "question")
    private String question;
    @Column(name = "answer")
    private String answer;
    @Column(name = "picture1")
    private String picture1;
    @Column(name = "picture2")
    private String picture2;
    @Column(name = "picture3")
    private String picture3;
    @Column(name = "picture4")
    private String picture4;
    @Column(name = "answer_by")
    private String answerBy;
    @Column(name = "question_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date questionTime;
    @Column(name = "answer_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date answerTime;
    @Column(name = "status")
    private Integer status;
    @Column(name = "source")
    private Integer source;
    @Column(name = "source_version")
    private String sourceVersion;
    @Column(name = "qbao_version")
    private String qbaoVersion;
    @Column(name = "machine")
    private String machine;

    @Transient
    private String name;

    @Transient
    private Integer count;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getPicture1() {
        return picture1;
    }

    public void setPicture1(String picture1) {
        this.picture1 = picture1;
    }

    public String getPicture2() {
        return picture2;
    }

    public void setPicture2(String picture2) {
        this.picture2 = picture2;
    }

    public String getPicture3() {
        return picture3;
    }

    public void setPicture3(String picture3) {
        this.picture3 = picture3;
    }

    public String getPicture4() {
        return picture4;
    }

    public void setPicture4(String picture4) {
        this.picture4 = picture4;
    }

    public String getAnswerBy() {
        return answerBy;
    }

    public void setAnswerBy(String answerBy) {
        this.answerBy = answerBy;
    }

    public Date getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(Date questionTime) {
        this.questionTime = questionTime;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public void setSourceVersion(String sourceVersion) {
        this.sourceVersion = sourceVersion;
    }

    public String getQbaoVersion() {
        return qbaoVersion;
    }

    public void setQbaoVersion(String qbaoVersion) {
        this.qbaoVersion = qbaoVersion;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
//
//    @PrePersist
//    @PreUpdate
//    public void encodeQuestion() {
//        Charset charset = Charset.forName(WalletConstants.CHARACTER_ENCODE);
//        this.question = new String(Base64.encodeBase64(question.getBytes(charset)), charset);
//        if(this.answer != null){
//        this.answer = new String(Base64.encodeBase64(answer.getBytes(charset)), charset);}
//
//    }
//    @PostPersist
//    @PostUpdate
//    @PostLoad
//    public void decodeQuestion() {
//        Charset charset = Charset.forName(WalletConstants.CHARACTER_ENCODE);
//        this.question = new String(Base64.decodeBase64(question.getBytes(charset)), charset);
//        if(this.answer != null){
//        this.answer = new String(Base64.decodeBase64(answer.getBytes(charset)), charset);}
//    }
}
