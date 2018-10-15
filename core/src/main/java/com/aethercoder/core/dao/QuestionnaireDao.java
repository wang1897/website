package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/2/28
 * @modified By:
 */
@Repository
public interface QuestionnaireDao extends JpaRepository<Questionnaire,Long> {

}
