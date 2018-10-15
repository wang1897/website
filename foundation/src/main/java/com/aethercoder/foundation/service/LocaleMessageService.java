package com.aethercoder.foundation.service;

import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.dao.LanguageDao;
import com.aethercoder.foundation.dao.MessageDao;
import com.aethercoder.foundation.entity.i18n.Language;
import com.aethercoder.foundation.entity.i18n.Message;
import com.aethercoder.basic.utils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class LocaleMessageService {


    private static Logger logger = LoggerFactory.getLogger(LocaleMessageService.class);


    @Autowired
    private MessageSource messageSource;

    public String getLocalErrorMessage(String errorCode, String[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage(errorCode, args, locale);
        return errorMessage;
    }

    public String getLocalMessage(String messageCode, String... args) {

        Locale locale = LocaleContextHolder.getLocale();
        String errorMessage = messageSource.getMessage(messageCode, args, locale);
        return errorMessage;
    }

    public String getLocalMessage(String messageCode,Locale locale, String... args) {

        String errorMessage = messageSource.getMessage(messageCode, args, locale);
        return errorMessage;
    }

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private LanguageDao languageDao;

    public Message saveMessage(Message message) {
        Assert.notNull(message.getTable(),"table is null");
        Assert.notNull(message.getMessage(), "message is null");
        Assert.notNull(message.getLanguage(), "language is null");

        if (message.getCode() == null) {
            if (message.getField() == null || message.getResourceId() == null) {
                Assert.isTrue(false, "field and resourceId is null");
            }
        }

        if (message.getField() == null) {
            if (message.getCode() == null) {
                Assert.isTrue(false, "code is null");
            }
        }

        if (message.getField() != null) {
            Message message1 = messageDao.findByTableAndFieldAndResourceIdAndLanguage(message.getTable(), message.getField(),
                    message.getResourceId(), message.getLanguage());
            if (message1 != null) {
                BeanUtils.copyPropertiesWithoutNull(message, message1);
                return messageDao.save(message1);
            }
        }
        if (message.getCode() != null) {
            Message message1 = messageDao.findByTableAndCodeAndLanguage(message.getTable(), message.getCode(), message.getLanguage());
            if (message1 != null) {
                BeanUtils.copyPropertiesWithoutNull(message, message1);
                return messageDao.save(message1);
            }
        }
        return messageDao.save(message);
    }


    public Message saveMessage(String table, String field, String resourceId, String language, String messageStr) {
        Message message1 = messageDao.findByTableAndFieldAndResourceIdAndLanguage(table, field,
                resourceId, language);
        if (message1 != null) {
            message1.setMessage(messageStr);
            return messageDao.save(message1);
        }
        Message message = new Message(table, field, resourceId, language, messageStr);
        return saveMessage(message);
    }

    /**
     *
     * @param table
     * @param field
     * @param showDefault 0:show Default language 1: show error message 2: show blank
     * @return
     */
    public String getMessageByTableFieldId(String table, String field, String resourceId, int showDefault, String language) {
        if (language == null) {
            language = getLanguage();
        }
        Message message = messageDao.findByTableAndFieldAndResourceIdAndLanguage(table, field, resourceId, language);
        /*if (message == null && language.length() > 2) {
            language = language.substring(0, 2);
            message = messageDao.findByTableAndFieldAndResourceIdAndLanguage(table, field, resourceId, language);
        }*/
        if (message == null) {
            if (showDefault == CommonConstants.I18N_SHOW_DEFAULT) {
                Language language1 = languageDao.findByIsDefault(true);
                if (language1.getLanguageCode().equals(language)) {
                    showDefault = 1;
                } else {
                    message = messageDao.findByTableAndFieldAndResourceIdAndLanguage(table, field, resourceId, language1.getLanguageCode());
                    if (message != null) {
                        return message.getMessage();
                    }
                    showDefault = 1;
                }
            }
            if (showDefault == CommonConstants.I18N_SHOW_NOT_EXIST) {
                String[] messageParam = new String[]{table + "-" + field + "-" + resourceId};
                return getLocalMessage("MESSAGE_NOT_FOUND", messageParam);
            }
            if (showDefault == CommonConstants.I18N_SHOW_EMPTY) {
                return "";
            }
        }
        return message.getMessage();
    }

    /**
     *
     * @param table
     * @param field
     * @param showDefault 0:show Default language 1: show error message 2: show blank
     * @return
     */
    public String getMessageByTableFieldId(String table, String field, String resourceId, int showDefault) {
        return getMessageByTableFieldId(table, field, resourceId, showDefault, null);
    }

    /**
     *
     * @param table
     * @param code
     * @param showDefault
     * @return 0:show Default language 1: show error message 2: show blank
     */
    public String getMessageByTableCode(String table, String code, int showDefault) {
        String language = getLanguage();
        Message message = messageDao.findByTableAndCodeAndLanguage(table, code, language);
        if (message == null) {
            if (showDefault == 0) {
                Language language1 = languageDao.findByIsDefault(true);
                if (language1.getLanguageCode().equals(language)) {
                    showDefault = 1;
                } else {
                    message = messageDao.findByTableAndCodeAndLanguage(table, code, language1.getLanguageCode());
                    if (message != null) {
                        return message.getMessage();
                    }
                    showDefault = 1;
                }
            }
            if (showDefault == 1) {
                String[] messageParam = new String[]{table + "-" + code};
                return getLocalMessage("MESSAGE_NOT_FOUND", messageParam);
            }
            if (showDefault == 2) {
                return "";
            }
        }
        return message.getMessage();
    }

    public List<Object> getMessagesByTableField (String table, String field,String language,String message){
        List<Message> messageList = messageDao.findByTableAndFieldAndLanguageAndMessageLike(table, field,language,message);

        if (messageList != null && messageList.size() > 0) {
            List customerIdList = new ArrayList();
            for (Message message1 : messageList) {
                customerIdList.add(Long.valueOf(message1.getResourceId()));
            }
            return customerIdList;
        }
        return null;
    }

    public String getLanguage() {
        Locale locale = LocaleContextHolder.getLocale();
        return locale.getLanguage();
    }

    public void deleteMessageByTableFieldId(String table, String field, String resourceId) {
        List<Message> messageList = messageDao.findByTableAndFieldAndResourceId(table, field, resourceId);
        messageDao.delete(messageList);
    }
}
