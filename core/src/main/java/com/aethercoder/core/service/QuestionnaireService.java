package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.Questionnaire;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/2/28
 * @modified By:
 */
public interface QuestionnaireService {
    Boolean questionnaireInfo(String accountNo);

    void submitQuestionnaire(Questionnaire questionnaire,String accountNo);
}
