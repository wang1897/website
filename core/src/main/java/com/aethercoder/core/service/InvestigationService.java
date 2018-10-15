package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.Investigation;
import io.rong.models.SMSSendCodeResult;
import io.rong.models.SMSVerifyCodeResult;
import org.springframework.data.domain.Page;

import java.util.List;


/**
 * Created by jiawei.tao on 2017/10/24.
 */
public interface InvestigationService {
    Investigation save(Investigation event);

    Investigation update(Investigation investigation);

    Investigation findById(long id);

    Investigation findByUserId(String accountNo);

    /**
     * 发送短信验证码
     *
     * @param mobile
     * @param templateId
     * @return
     */
    SMSSendCodeResult sendCode(String mobile, String templateId);

    /**
     * 验证码验证
     *
     * @param sessionId
     * @param code
     * @return
     */
    SMSVerifyCodeResult verifyCode(String sessionId, String code);

    Page<Investigation> findInvestigationByPage(Integer page, Integer size, String userName, String phone, String email, Boolean isEmailed, String investigationNo);

    Page<Investigation> findInvestigationForICOByPage(Integer page, Integer size, String userName, String phone, String email, String status);

    void updateSuccessByList(List<Long> investigationIDList);
}
