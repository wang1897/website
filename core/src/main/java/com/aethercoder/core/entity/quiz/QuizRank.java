package com.aethercoder.core.entity.quiz;

import com.aethercoder.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @auther Guo Feiyan
 * @date 2018/2/26 下午1:29
 */
@Entity
@Table(name = "quiz_rank")
@ApiModel(value="累计答题",description="累计答题记录")
public class QuizRank extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "answer_quiz")
    private Integer answerQuiz;

    @Column(name = "right_quiz")
    private Integer rightQuiz;

    @Column(name = "win_token")
    private BigDecimal winToken;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Integer getAnswerQuiz() {
        return answerQuiz;
    }

    public void setAnswerQuiz(Integer answerQuiz) {
        this.answerQuiz = answerQuiz;
    }

    public Integer getRightQuiz() {
        return rightQuiz;
    }

    public void setRightQuiz(Integer rightQuiz) {
        this.rightQuiz = rightQuiz;
    }

    public BigDecimal getWinToken() {
        return winToken;
    }

    public void setWinToken(BigDecimal winToken) {
        this.winToken = winToken;
    }
}
