package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.AndroidDao;
import com.aethercoder.core.entity.android.Android;
import com.aethercoder.core.service.AndroidService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.i18n.Message;
import com.aethercoder.foundation.service.LocaleMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2017/9/26 上午11:14
 */
@Service
public class AndroidServiceImpl implements AndroidService {

    private String androidTable = "androidPackage";
    private String descriptionField = "description";

    @Autowired
    private AndroidDao androidDao;

    @Autowired
    private LocaleMessageService localeMessageService;

    @Override
    public List<Android> findAndroidAll() {
        List<Android> androids = androidDao.findAll();
        androids.forEach(android -> {
            translateAndroidAll(android, CommonConstants.I18N_SHOW_DEFAULT);
        });
        return androids;
    }


    @Override
    public Android findAndroidById(Long id) {
        Android android = androidDao.findOne(id);
        translateAndroidAll(android, CommonConstants.I18N_SHOW_DEFAULT);
        return android;
    }

    @Override
    public Android findAndroidLatest() {
        Android android = androidDao.findFirstBySourceIsTrueOrderByIdDesc();
        translateAndroid(android,null, CommonConstants.I18N_SHOW_DEFAULT);
        return android;
    }

    @Override
    public Android findIOSLatest() {
        Android android = androidDao.findFirstBySourceIsFalseOrderByIdDesc();
        translateAndroid(android,null, CommonConstants.I18N_SHOW_DEFAULT);
        return android;
    }
    private void translateAndroid(Android android, String language, int type) {
        String questionI18n = localeMessageService.getMessageByTableFieldId(androidTable, descriptionField, android.getId() + "", type, language);
        android.setDescription(questionI18n);
    }
    private void translateAndroidAll(Android android,  int type) {
        String questionI18nZh = localeMessageService.getMessageByTableFieldId(androidTable, descriptionField, android.getId() + "", type, WalletConstants.LANGUAGE_TYPE_ZH);
        android.setDescription(questionI18nZh);

        String questionI18nko = localeMessageService.getMessageByTableFieldId(androidTable, descriptionField, android.getId() + "", type, WalletConstants.LANGUAGE_TYPE_KO);
        android.setDescriptionKo(questionI18nko);

        String questionI18nEn = localeMessageService.getMessageByTableFieldId(androidTable, descriptionField, android.getId() + "", type, WalletConstants.LANGUAGE_TYPE_EN);
        android.setDescriptionEn(questionI18nEn);

        String questionI18nJa = localeMessageService.getMessageByTableFieldId(androidTable, descriptionField, android.getId() + "", type, WalletConstants.LANGUAGE_TYPE_JA);
        android.setDescriptionJa(questionI18nJa);
    }

    @Override
    public Android saveAndroid(Android android) {
        //check安卓版本号
       /* if (!StringUtils.isEmpty(android.getVersionCode()) && !NumberUtil.isInteger(android.getVersionCode())) {
            throw new AppException(ReqExceptionEntity.ErrorCode.CHECK_ANDROID_CODE);
        }*/
        androidDao.save(android);
        saveAndroidMessage(android);
        return android;
    }

    private void saveAndroidMessage(Android android) {
        if (android.getDescription() != null) {
            Message androidMsg = new Message();
            androidMsg.setMessage(android.getDescription());
            androidMsg.setLanguage(WalletConstants.LANGUAGE_TYPE_ZH);
            androidMsg.setTable(androidTable);
            androidMsg.setField(descriptionField);
            androidMsg.setResourceId(android.getId() + "");
            localeMessageService.saveMessage(androidMsg);
        }
        if (android.getDescriptionEn() != null) {
            Message androidMsg = new Message();
            androidMsg.setMessage(android.getDescriptionEn());
            androidMsg.setLanguage(WalletConstants.LANGUAGE_TYPE_EN);
            androidMsg.setTable(androidTable);
            androidMsg.setField(descriptionField);
            androidMsg.setResourceId(android.getId() + "");
            localeMessageService.saveMessage(androidMsg);
        }
        if (android.getDescriptionKo() != null) {
            Message androidMsg = new Message();
            androidMsg.setMessage(android.getDescriptionKo());
            androidMsg.setLanguage(WalletConstants.LANGUAGE_TYPE_KO);
            androidMsg.setTable(androidTable);
            androidMsg.setField(descriptionField);
            androidMsg.setResourceId(android.getId() + "");
            localeMessageService.saveMessage(androidMsg);
        }
        if (android.getDescriptionJa() != null) {
            Message androidMsg = new Message();
            androidMsg.setMessage(android.getDescriptionJa());
            androidMsg.setLanguage(WalletConstants.LANGUAGE_TYPE_JA);
            androidMsg.setTable(androidTable);
            androidMsg.setField(descriptionField);
            androidMsg.setResourceId(android.getId() + "");
            localeMessageService.saveMessage(androidMsg);
        }
    }

    @Override
    public Android updateAndroid(Android android) {
        Android Uandroid = androidDao.findOne(android.getId());
        BeanUtils.copyProperties(android, Uandroid);
        Android android1 = androidDao.save(Uandroid);
        saveAndroidMessage(Uandroid);
        return android1;
    }

    @Override
    public void deleteAndroid(Long id) {
        androidDao.delete(id);
    }
}
