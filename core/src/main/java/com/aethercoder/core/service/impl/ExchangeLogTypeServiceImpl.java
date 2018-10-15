package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ExchangeLogTypeDao;
import com.aethercoder.core.entity.wallet.ExchangeLogType;
import com.aethercoder.core.service.ExchangeLogTypeService;
import com.aethercoder.foundation.dao.MessageDao;
import com.aethercoder.foundation.entity.i18n.Message;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/9
 * @modified By:
 */
@Service
public class ExchangeLogTypeServiceImpl implements ExchangeLogTypeService {

    @Autowired
    private MessageDao messageDao;
    @Autowired
    private LocaleMessageService localeMessageService;
    @Autowired
    private ExchangeLogTypeDao exchangeLogTypeDao;

    @Override
    public List<ExchangeLogType> getExchangeLogTypeByLanguageForAdmin() {
        List<ExchangeLogType> exchangeLogTypeList = exchangeLogTypeDao.findAll();
        for (ExchangeLogType exchangeLogType : exchangeLogTypeList) {
            String resourceId = exchangeLogType.getId().toString();
//            String typeKO = localeMessageService.getMessageByTableFieldId(WalletConstants.EXCHANGELOGTYPE_TABLE_NAME, WalletConstants.EXCHANGELOGTYPE_TYPE_NAME, resourceId + "",  CommonConstants.I18N_SHOW_DEFAULT, WalletConstants.MAESSAGE_LANGUAGE_KO);
//            exchangeLogType.setKoName(typeKO);
//
//            String typeEn = localeMessageService.getMessageByTableFieldId(WalletConstants.EXCHANGELOGTYPE_TABLE_NAME, WalletConstants.EXCHANGELOGTYPE_TYPE_NAME, resourceId + "",  CommonConstants.I18N_SHOW_DEFAULT, WalletConstants.MAESSAGE_LANGUAGE_EN);
//            exchangeLogType.setEnName(typeEn);
            Message koMessage = messageDao.findByTableAndFieldAndResourceIdAndLanguage(WalletConstants.EXCHANGELOGTYPE_TABLE_NAME, WalletConstants.EXCHANGELOGTYPE_TYPE_NAME, resourceId, WalletConstants.MAESSAGE_LANGUAGE_KO);
            if (koMessage == null){
                exchangeLogType.setKoName(exchangeLogType.getTypeName());
            }else {
                exchangeLogType.setKoName(koMessage.getMessage());
            }

            Message enMessage = messageDao.findByTableAndFieldAndResourceIdAndLanguage(WalletConstants.EXCHANGELOGTYPE_TABLE_NAME, WalletConstants.EXCHANGELOGTYPE_TYPE_NAME, resourceId, WalletConstants.MAESSAGE_LANGUAGE_EN);
            if (enMessage == null){
                exchangeLogType.setEnName(exchangeLogType.getTypeName());
            }else {
                exchangeLogType.setEnName(enMessage.getMessage());
            }
        }
        return exchangeLogTypeList;
    }

    @Override
    public void saveExchangeLogTypeForAdmin(ExchangeLogType exchangeLogType) {
        ExchangeLogType exchangeLogType1 = exchangeLogTypeDao.findOne(exchangeLogType.getId());
        if (exchangeLogType1 != null || exchangeLogType.getId() == 0L){
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }

        // 新建
        exchangeLogTypeDao.save(exchangeLogType);

        updateExchangeLogNames(exchangeLogType);

    }

    private void updateExchangeLogNames(ExchangeLogType exchangeLogType) {
        String id = exchangeLogType.getId().toString();
        String koName = exchangeLogType.getKoName();
        String enName = exchangeLogType.getEnName();
        String typeName = exchangeLogType.getTypeName();
        String table = WalletConstants.EXCHANGELOGTYPE_TABLE_NAME;
        String field = WalletConstants.EXCHANGELOGTYPE_TYPE_NAME;
        localeMessageService.saveMessage(table, field, id, WalletConstants.MAESSAGE_LANGUAGE_ZH, typeName);
        localeMessageService.saveMessage(table, field, id, WalletConstants.MAESSAGE_LANGUAGE_KO, koName);
        localeMessageService.saveMessage(table, field, id, WalletConstants.MAESSAGE_LANGUAGE_EN, enName);

    }

    @Override
    public void updateExchangeLogTypeForAdmin(ExchangeLogType exchangeLogType) {
        ExchangeLogType targetExchangeLogType = exchangeLogTypeDao.findOne(exchangeLogType.getId());
        if (targetExchangeLogType == null) {
            throw new AppException(ErrorCode.EXCHANGELOGTYPE_CHECK_NOT_EXSIT);
        } else {
            BeanUtils.copyPropertiesWithoutNull(exchangeLogType, targetExchangeLogType);
            exchangeLogTypeDao.save(targetExchangeLogType);
            updateExchangeLogNames(targetExchangeLogType);
        }
    }

    @Override
    public List<ExchangeLogType> getExchangeLogTypeByLanguage(String language) {
        List<ExchangeLogType> exchangeLogTypeList = exchangeLogTypeDao.findAll();
        for (ExchangeLogType exchangeLogType : exchangeLogTypeList) {
            String resourceId = exchangeLogType.getId().toString();
            Message message = messageDao.findByTableAndFieldAndResourceIdAndLanguage(WalletConstants.EXCHANGELOGTYPE_TABLE_NAME, WalletConstants.EXCHANGELOGTYPE_TYPE_NAME, resourceId, language);
            if (message != null) {
                exchangeLogType.setTypeName(message.getMessage());
            }
        }
        return exchangeLogTypeList;
    }

}
