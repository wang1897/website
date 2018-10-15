package com.aethercoder.activity.websocket.guessGamble;

import com.aethercoder.activity.contants.ActivityContants;
import com.aethercoder.activity.dao.guessGamble.GuessGambleDao;
import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import com.aethercoder.activity.entity.json.GambleResult;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by hepengfei on 27/02/2018.
 */
@Component
public class MessageSender {
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    private WebSocketConnectEventListener webSocketConnectEventListener;

    @Autowired
    private GuessGambleDao guessGambleDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String qName = "/guessGamble/";
    private String qName2 = "/response";

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }


    public void sendEventToClient(GambleResult gambleResult) {
        Long gambleId = gambleResult.getGamble_id();
        simpMessageSendingOperations.convertAndSend(qName + gambleId + qName2, gambleResult);

    }

    @Scheduled(fixedRate = 10000)
    public void sendJoinGambleResultScheduled() {
        List<GuessGamble> guessGambles = guessGambleDao.queryGuessGamblesByStatus(ActivityContants.GUESS_GAMBLE_STATUS_RUNNING);
        for (int i = 0; i<guessGambles.size();i++){
            GuessGamble guessGamble = guessGambles.get(i);
            Object gambleResultObject = redisTemplate.opsForHash().get(RedisConstants.REDIS_NAME_GAMBLE, guessGamble.getId());
            GambleResult gambleResult = new GambleResult();
            gambleResult.setGamble_id(guessGamble.getId());
            if (gambleResultObject!=null){
                gambleResult = BeanUtils.jsonToObject(gambleResultObject.toString(),GambleResult.class);
            }
            sendEventToClient(gambleResult);
        }

    }
}
