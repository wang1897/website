package com.aethercoder.core.util;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/20
 * @modified By:
 */
@Component
public class PushUtil {

    @Value("${jPush.appKey}")
    private String appKey;

    @Value("${jPush.appSecret}")
    private String appSecret;

    @Value("${jPush.nevFlag}")
    private Boolean nevFlag;

    /**
     * 给所有平台的所有用户发通知
     */
    public void sendAllsetNotification(String message) {
        JPushClient jpushClient = new JPushClient(appSecret, appKey);
        Map<String, String> extras = new HashMap<String, String>();
        // 添加附加信息
        extras.put("extMessage", "我是额外的通知");

        PushPayload payload = buildPushObject_all_alias_alert(message, extras);

        try {
            PushResult result = jpushClient.sendPush(payload);
            System.out.println(result);
        } catch (APIConnectionException e) {
            System.out.println(e);
        } catch (APIRequestException e) {
            System.out.println(e);
            System.out.println("Error response from JPush server. Should review and fix it. " + e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Msg ID: " + e.getMsgId());
        }
    }

    /**
     * 给所有平台的所有用户发消息
     */
    public void sendAllMessage(String message) {
        JPushClient jpushClient = new JPushClient(appSecret, appKey);
        Map<String, String> extras = new HashMap<String, String>();
        // 添加附加信息
        extras.put("extMessage", "我是额外透传的消息");

        PushPayload payload = buildPushObject_all_alias_Message(message, extras);

        try {
            PushResult result = jpushClient.sendPush(payload);
            //System.out.println(result);
        } catch (APIConnectionException e) {
            System.out.println(e);
        } catch (APIRequestException e) {
            System.out.println(e);
            System.out.println("Error response from JPush server. Should review and fix it. " + e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Msg ID: " + e.getMsgId());
        }
    }

    /**
     * 给所有平台的一个或者一组用户发送信息
     */
    public void sendAlias(String message, List<String> aliasList) {
        JPushClient jpushClient = new JPushClient(appSecret, appKey);
        Map<String, String> extras = new HashMap<String, String>();
        // 添加附加信息
        extras.put("type","0");
        extras.put("url","https://www.baidu.com/");
        PushPayload payload = allPlatformAndAlias(message, extras, aliasList);

        try {
            PushResult result = jpushClient.sendPush(payload);
            System.out.println(result);
        } catch (APIConnectionException e) {
            System.out.println(e);
        } catch (APIRequestException e) {
            System.out.println(e);
            System.out.println("Error response from JPush server. Should review and fix it. " + e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Msg ID: " + e.getMsgId());
        }
    }

    /**
     * 给平台的一个或者一组标签发送消息
     */
    public void sendTag(String message, String type, String url, String groupNo,String gameId,List<String> tagsList) {
        JPushClient jpushClient = new JPushClient(appSecret, appKey);
        // 附加字段
        Map<String, String> extras = new HashMap<String, String>();
        extras.put("type", type);
        extras.put("url", url);
        extras.put("groupNo", groupNo);
        extras.put("gameId", gameId);

        PushPayload payload = allPlatformAndTag(message, extras, tagsList);

        try {
            PushResult result = jpushClient.sendPush(payload);
            System.out.println(result);
        } catch (APIConnectionException e) {
            System.out.println(e);
        } catch (APIRequestException e) {
            System.out.println(e);
            System.out.println("Error response from JPush server. Should review and fix it. " + e);
            System.out.println("HTTP Status: " + e.getStatus());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Msg ID: " + e.getMsgId());
        }
    }

    /**
     * 发送通知
     */
    private PushPayload buildPushObject_all_alias_alert(String message, Map<String, String> extras) {
        return PushPayload.newBuilder()
                // 设置平台
                .setPlatform(Platform.all())
                // 按什么发送 tag alia
                .setAudience(Audience.all())
                // 发送消息
                .setNotification(
                        Notification
                                .newBuilder()
                                .setAlert(message)
                                .addPlatformNotification(
                                        AndroidNotification.newBuilder().addExtras(extras).build())
                                .addPlatformNotification(
                                        IosNotification.newBuilder().addExtras(extras).build())
                                .build())
                //设置平台环境  True 表示推送生产环境，False 表示要推送开发环境   默认是开发
                .setOptions(Options.newBuilder().setApnsProduction(nevFlag).build()).build();
    }

    /**
     * 发送透传消息
     */
    private PushPayload buildPushObject_all_alias_Message(String message, Map<String, String> extras) {
        return PushPayload.newBuilder().
                // 设置平台
                        setPlatform(Platform.all())
                // 按什么发送 tag alia
                .setAudience(Audience.all())
                // 发送通知
                .setMessage(Message.newBuilder().setMsgContent(message).addExtras(extras).build())
                //设置平台环境  True 表示推送生产环境，False 表示要推送开发环境   默认是开发
                .setOptions(Options.newBuilder().setApnsProduction(nevFlag).build()).build();
    }

    /**
     * 生成向一个或者一组用户发送的消息
     */
    private PushPayload allPlatformAndAlias(String alert, Map<String, String> extras, List<String> tagsList) {
        return PushPayload.newBuilder()
                // 设置平台
                .setPlatform(Platform.all())
                // 按什么发送 tag alia
                //.setAudience(Audience.alias(aliasList))
                .setAudience(Audience.tag(tagsList))
                // 发送通知
                .setNotification(
                        Notification
                                .newBuilder()
                                .setAlert(alert)
                                .addPlatformNotification(
                                        AndroidNotification.newBuilder().addExtras(extras).build())
                                .addPlatformNotification(
                                        IosNotification.newBuilder().addExtras(extras).build())
                                .build())
                //设置平台环境  True 表示推送生产环境，False 表示要推送开发环境   默认是开发
                .setOptions(Options.newBuilder().setApnsProduction(nevFlag).build()).build();
    }

    /**
     * 生成向一组标签进行推送的消息
     */
    private PushPayload allPlatformAndTag(String alert, Map<String, String> extras,List<String> tagsList) {
        return PushPayload.newBuilder()
                // 设置平台为all
                .setPlatform(Platform.all())
                // 按什么发送 tag alia
                .setAudience(Audience.tag(tagsList))
                // 发送通知
                .setNotification(
                        Notification
                                .newBuilder()
                                .setAlert(alert)
                                .addPlatformNotification(
                                        AndroidNotification.newBuilder().addExtras(extras).build())
                                .addPlatformNotification(
                                        IosNotification.newBuilder().addExtras(extras).build())
                                .build())
                //设置平台环境  True 表示推送生产环境，False 表示要推送开发环境   默认是开发
                .setOptions(Options.newBuilder().setApnsProduction(nevFlag).build()).build();
    }


    //测试
//    public static void main(String[] args) {
//        new PushUtil().sendAll("这是java后台发送的一个通知。。。。");
//        List<String> sendAlias = new ArrayList<>();
//        sendAlias.add("1001");
//        new PushUtil().sendAlias("这是java后台发送的一个按照alia的通知", sendAlias);
//        new PushUtil().sendAllMessage("");
//    }
}
