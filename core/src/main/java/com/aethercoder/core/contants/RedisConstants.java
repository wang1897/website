package com.aethercoder.core.contants;

/**
 * Created by hepengfei on 26/12/2017.
 */
public interface RedisConstants {
    //redis security
    String REDIS_KEY_TOKEN = "token:";
    String REDIS_KEY_CAPTCHA = "captcha:";
    String REDIS_KEY_WALLET_ACCOUNT = "account:";
    String REDIS_KEY_SMART_CONTRACT = "contract:";
    String REDIS_KEY_SYS_CONFIG = "config:";
    String REDIS_KEY_QUIZ = "quiz:";
    String REDIS_KEY_ACTIVITY = "activity:";

    String REDIS_CACHE_NAME_CONTRACT = "contract";
    String REDIS_CACHE_NAME_CONFIG = "sysconfig";


    String REDIS_NAME_GUESS_NUMBER = "guessNumber:";
    String REDIS_NAME_GUESS_NUMBER_AWARD = "guessGameAwards:";
    String REDIS_NAME_GAMBLE = "gamble:";
    String REDIS_NAME_GAMBLE_RANK = "gambleRank:";
    String REDIS_NAME_WIN = "win:";
    String REDIS_NAME_LOSE = "lose:";
    String REDIS_NAME_CUSTOMER_LOGIN = "customerLogin:";
    String REDIS_NAME_CUSTOMER_LOGIN_LOCK = "customerLoginLock:";
    String REDIS_NAME_CUSTOMER_LOGIN_SMS_CODE = "customerLoginCode:";
    String REDIS_NAME_USERCONTRACT_REQUEST = "userContract:";

    String REDIS_NAME_JOIN_GROUP = "joinGroup:";
}
