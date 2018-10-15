package com.aethercoder;

import com.aethercoder.core.dao.TokenCalendarDao;
import com.aethercoder.core.service.WalletService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class ApplicationTests {

	@Value( "${rongCloud.appKey}" )
	private String appKey;
	@Value( "${rongCloud.appSecret}" )
	private String appSecret;

	@Autowired
	private TokenCalendarDao tokenCalendarDao;
	@Autowired
	private WalletService walletService;

	@Test
	public void contextLoads() {
        System.out.println("11111");
//
//		TokenCalendar tokenCalendar = tokenCalendarDao.findOne(54L);
//
//		String[] platform = {"ios","android"};
//		String fromuserid = null;
//
//		String[] tags = {tokenCalendar.getLanguageType()};
//		TagObj audience = new TagObj(tags,null,false);
//		MsgObj message = null;
//
//		String alert = tokenCalendar.getContent();
//		Map<String,String> extMap = new HashMap<>();
//		extMap.put("type","0");
//		PlatformNotification iosNote = new PlatformNotification(alert,extMap);
//		PlatformNotification andNote = new PlatformNotification(alert,extMap);
//		Notification notification = new Notification(alert,iosNote,andNote);
//
//		PushMessage broadcastPushPushMessage = new PushMessage(platform,fromuserid,audience,message,notification);
//
//		RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
//
//		try {
//			CodeSuccessResult pushBroadcastPushResult = rongCloud.push.broadcastPush(broadcastPushPushMessage);
//			System.out.println("broadcastPush:  " + pushBroadcastPushResult.toString());
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//		walletService.startWithdrawBatch();
		System.out.println("end");
	}

}
