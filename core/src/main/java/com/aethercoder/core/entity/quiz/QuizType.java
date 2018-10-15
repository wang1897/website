package com.aethercoder.core.entity.quiz;

import com.aethercoder.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by guofeiyan on 2018/02/08.
 */
@Entity
@Table(name = "quiz_type")
@ApiModel(value="每日答题记录",description="每日答题记录")
public class QuizType extends BaseEntity{


    private static final long serialVersionUID = -1L;

    @Column(name = "name")
    private String name;

    @Column(name = "win_token")
    private BigDecimal winToken;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getWinToken() {
        return winToken;
    }

    public void setWinToken(BigDecimal winToken) {
        this.winToken = winToken;
    }
}
