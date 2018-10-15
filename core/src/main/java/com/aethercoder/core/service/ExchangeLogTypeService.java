package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.ExchangeLogType;
import com.aethercoder.foundation.entity.i18n.Message;

import java.util.List;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/9
 * @modified By:
 */
public interface ExchangeLogTypeService {

    List<ExchangeLogType> getExchangeLogTypeByLanguageForAdmin();

    void saveExchangeLogTypeForAdmin(ExchangeLogType exchangeLogType);

    void updateExchangeLogTypeForAdmin(ExchangeLogType exchangeLogTypeList);

    List<ExchangeLogType>  getExchangeLogTypeByLanguage(String language);
}
