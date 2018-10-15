package com.aethercoder.activity.entity.json;

import java.math.BigDecimal;

/**
 * @auther Guo Feiyan
 * @date 2018/2/28 下午4:13
 */
public class GambleResult {

    private Integer option1_number = 0;

    private Integer option2_number = 0;

    private BigDecimal option1_amount = new BigDecimal(0);

    private BigDecimal option2_amount = new BigDecimal(0);

    private Long gamble_id;


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

    public Long getGamble_id() {
        return gamble_id;
    }

    public void setGamble_id(Long gamble_id) {
        this.gamble_id = gamble_id;
    }
}
