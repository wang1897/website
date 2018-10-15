package com.aethercoder.core.entity.quiz;

import com.aethercoder.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Entity
@Table(name = "quiz_answer")
@ApiModel(value="每日答题记录",description="每日答题记录")

public class QuizAnswer extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "answer_time")
    private Date answerTime;

    @Column(name = "quiz")
    private Long quiz;

    @Column(name = "right_answer")
    private Integer rightAnswer;

    @Column(name = "answer")
    private Integer answer = 0;

    @Column(name = "win_token")
    private BigDecimal winToken;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Date getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(Date answerTime) {
        this.answerTime = answerTime;
    }

    public Long getQuiz() {
        return quiz;
    }

    public void setQuiz(Long quiz) {
        this.quiz = quiz;
    }

    public Integer getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(Integer rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public Integer getAnswer() {
        return answer;
    }

    public void setAnswer(Integer answer) {
        this.answer = answer;
    }

    public BigDecimal getWinToken() {
        return winToken;
    }

    public void setWinToken(BigDecimal winToken) {
        this.winToken = winToken;
    }
}
