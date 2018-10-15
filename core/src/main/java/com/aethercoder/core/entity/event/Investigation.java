package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * @author Created by jiawei.tao on 2017/10/24.
 */
@Entity
public class Investigation extends BaseEntity {
    private static final long serialVersionUID = -1L;

    private Integer investigationNo;
    private String questionAnswer;
    private String accountNo;
    private String userName;
    private String email;
    private String phone;
    private String cardNo;
    private Integer cardType;

    private Boolean isEmailed = false;
    private String nationality;
    private String gender;
    private String age;

    private String img1;
    private String img2;
    private String img3;

    private String status;

    @Basic
    @Column( name = "investigation_no", nullable = false )
    public Integer getInvestigationNo() {
        return investigationNo;
    }

    public void setInvestigationNo(Integer investigationNo) {
        this.investigationNo = investigationNo;
    }

    @Basic
    @Column( name = "question_answer", nullable = true, length = 1000 )
    public String getQuestionAnswer() {
        return questionAnswer;
    }

    public void setQuestionAnswer(String questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    @Basic
    @Column( name = "account_no", nullable = true, length = 10 )
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Basic
    @Column( name = "user_name", nullable = true, length = 100 )
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Basic
    @Column( name = "email", nullable = true, length = 255 )
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column( name = "phone", nullable = true, length = 20 )
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Basic
    @Column( name = "card_type", nullable = true, length = 1 )
    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }
    @Basic
    @Column( name = "card_no", nullable = true, length = 20 )
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Basic
    @Column( name = "nationality", nullable = true, length = 3 )
    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Basic
    @Column( name = "gender", nullable = true, length = 1 )
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Basic
    @Column( name = "age", nullable = true )
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Basic
    @Column( name = "is_emailed")
    public Boolean getIsEmailed() {
        return isEmailed;
    }

    public void setIsEmailed(Boolean isEmailed) {
        this.isEmailed = isEmailed;
    }

    @Basic
    @Column( name = "img1")
    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    @Basic
    @Column( name = "img2")
    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    @Basic
    @Column( name = "img3")
    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    @Basic
    @Column( name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    @JsonIgnore(false)
    @JsonProperty
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreateTime() {
        return super.getCreateTime();
    }
}
