package com.aethercoder.i18n;

import com.aethercoder.TestApplication;
import com.aethercoder.foundation.dao.MessageDao;
import com.aethercoder.foundation.service.LocaleMessageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by hepengfei on 23/02/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class MessageServiceTest {
    @Autowired
    private LocaleMessageService messageService;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testSaveMessage() {
//        Message newMsg = new Message();
//        newMsg.setField("Name");
//        newMsg.setTable("WalletAccount");
//        newMsg.setLanguage("en");
//        newMsg.setResourceId("5");
//        newMsg.setMessage("test");
//        Message message = messageService.saveMessage(newMsg);
//        String msg = messageService.getMessageByTableFieldId("WalletAccount", "Name", "5", 0);
////        Assert.assertEquals(msg, "test");
//        messageDao.delete(message);
    }

    @Test
    public void testSaveExistMessageField() {
//        Message newMsg = new Message();
//        newMsg.setField("Name");
//        newMsg.setTable("WalletAccount");
//        newMsg.setLanguage("en");
//        newMsg.setResourceId("6");
//        newMsg.setMessage("test1");
//        Message message = messageService.saveMessage(newMsg);
//
//        newMsg = new Message();
//        newMsg.setField("Name");
//        newMsg.setTable("WalletAccount");
//        newMsg.setLanguage("en");
//        newMsg.setResourceId("6");
//        newMsg.setMessage("test2");
//        message = messageService.saveMessage(newMsg);
//        String msg = messageService.getMessageByTableFieldId("WalletAccount", "Name", "6", 0);
//        Message pMsg = (Message)redisTemplate.opsForValue().get(CommonConstants.REDIS_CACHE_NAME_MESSAGE + CommonConstants.REDIS_CACHE_PREFIX
//                + CommonConstants.REDIS_KEY_WALLET_MESSAGE + CommonConstants.REDIS_KEY_WALLET_MESSAGE_FIELD + "WalletAccount-Name-6-en");
//        Assert.assertEquals(pMsg.getMessage(), "test2");
//
//        Locale locale = Locale.SIMPLIFIED_CHINESE;
//        LocaleContextHolder.setLocale(locale);
//        msg = messageService.getMessageByTableFieldId("WalletAccount", "Name", "6", 1);
//        Assert.assertEquals(msg, "消息WalletAccount-Name-6不存在");
//        System.out.println(msg);
//
//        msg = messageService.getMessageByTableFieldId("WalletAccount", "Name", "6", 0);
//        Assert.assertEquals(msg, "消息WalletAccount-Name-6不存在");
//        System.out.println(msg);
//
//        msg = messageService.getMessageByTableFieldId("WalletAccount", "Name", "6", 2);
//        Assert.assertEquals(msg, "");
//        System.out.println(msg);
//
//        messageDao.delete(message);
    }
}
