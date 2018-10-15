package com.aethercoder.foundation.contants;

/**
 * Created by hepengfei on 2017/8/30.
 */
public interface CommonConstants {

    //security
//    String SYS_AUTH_PATH = "/**/auth/**";
    String SYS_ADMIN_PATH = "/**/admin/**";
    String SYS_FUND_PATH = "/**/fund/**";
    String SYS_CUSTOMER_PLATFORM_PATH = "/**/customer/platform/**";
    String SYS_FILE_PATH = "/**/file/**";
    String[] NO_SECURITY_PATH = {SYS_FILE_PATH, "/wallet/startWithdrawBatch","/contract/insertApi","/user/saveUser", "/user/activateUser", "/user/checkIsEmail", "/user/login", "/user/resetPwdEmail",
            "/user/resetPwd", "/adminAccount/login", "/s/gt", "/s/pay/gt", "/account/createAccount", "/account/importAccount",
            "/android/findAndroidLatest", "/android/findIOSLatest", "/groupManager/saveGroupManager", "/s/gc", "/encryption/*", "/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**",
            "/invite/accountInvitedInfo", "/customer/platform/loginCustomer", "/url/getCustomerInfo","/customer/admin/checkAdminCustomerByToken","/health","/tokenCalendar/getTokenCalendarEventInfo","/customer/platform/getCountryInfoForCustomer","/customer/platform/getCodeSms","/customer/platform/resetCustomerPwd","/group/getGroupShareUrlInfo","/activity/guessGamble/getGambleGameShareParams"};
    String[] NO_SIGN_PATH = {SYS_FILE_PATH, "/wallet/startWithdrawBatch","/contract/insertApi","/android/findAndroidLatest", "/android/findIOSLatest", "/encryption/*", "/v2/api-docs", "/configuration/ui", "/**/swagger-resources/**", "/configuration/**",
            "/swagger-ui.html", "/webjars/**", "/activity-websocket/**","/invite/accountInvitedInfo", "/url/getCustomerInfo","/customer/admin/checkAdminCustomerByToken","/health","/tokenCalendar/getTokenCalendarEventInfo","/group/getGroupShareUrlInfo","/activity/guessGamble/getGambleGameShareParams"
    };
    String[] NO_ENCRYPTION_PATH = {SYS_FILE_PATH, "/android/findAndroidLatest", "/android/findIOSLatest", "/encryption/*", "/v2/api-docs", "/configuration/ui", "/**/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**",
            "/activity-websocket/**","/health"};
    String[] NO_ENCRYPTION_RESPONSE_PATH = {SYS_FILE_PATH, "/android/findAndroidLatest", "/android/findIOSLatest", "/encryption/*", "/v2/api-docs", "/configuration/ui", "/s/gc", "/**/swagger-resources/**", "/configuration/**", "/swagger-ui.html", "/webjars/**",
            "/activity-websocket/**","/health"};


    String AES_KEY = "UI#0c-+}'aqhY,z*";
    String AES_CIPHER = "AES/CFB/NoPadding";
    String ENCRYPTION_IV = "&9c(eOYB$01N@~aU";
    String CHARACTER_ENCODE = "UTF-8";
    String API_SIGN_SALT_ORDER = "2(LOSme*e,37}&am@wUs1>y!";
    String API_SIGN_SALT2_ORDER = "w8&_asd-C$Eq+M%aA1wuNSF2";
    String API_SIGN_SALT = "&/*w+cWO@CMGp{|aq1=mHRmq";
    String API_SIGN_SALT2 = "unvhRYweIIDklsY_8pM+se%$";
    String DB_KEY = "UI#0c}tao'qhY,z*";
//    String SHA512_SALT = "67#$qpcY+";
    String SHA512_SALT = "";

    Integer CAPTCHA_EXPIRE_MINUTE = 30;


    //http header key
    String HEADER_WEB_TOKEN_KEY = "Authorization";
    String HEADER_APP_TOKEN_KEY = "X-T";
    String HEADER_ADMIN_FLAG_KEY = "X-AD";
    String HEADER_TIMESTAMP_KEY = "X-TS";
    String HEADER_SIGN_KEY = "X-S";
    String HEADER_SIGN_RANDOM = "X-R";
//    String HEADER_NO_ENCRYPTION = "X-NO-ENC-XXX";

    //batch
    Integer BATCH_TASK_STATUS_ACTIVE = 0;
    Integer BATCH_TASK_STATUS_COMPLETED = 1;
    Integer BATCH_TASK_STATUS_CANCELLED = 2;
    Integer BATCH_TASK_STATUS_RUNNING = 3;
    Integer BATCH_FREQUENCY_HOURLY = 0;
    Integer BATCH_FREQUENCY_DAILY = 1;
    Integer BATCH_FREQUENCY_WEEKLY = 2;
    Integer BATCH_FREQUENCY_MONTHLY = 3;
    Integer BATCH_FREQUENCY_YEARLY = 4;
    Integer BATCH_FREQUENCY_MINUTELY = 5;
    Integer BATCH_RESULT_SUCCESS = 0;
    Integer BATCH_RESULT_FAIL = 1;

    Integer DEFAULT_PAGE = 0;
    Integer DEFAULT_PAGE_SIZE = 10;

    //redis security
    String REDIS_CACHE_PREFIX = "cache:";
    String REDIS_KEY_SIGN = "sign:";
    String REDIS_CACHE_NAME_ACCOUNT = "account";
    String REDIS_CACHE_NAME_LANGUAGE = "language";
    String REDIS_KEY_WALLET_LANGUAGE = "language:";
    String REDIS_CACHE_NAME_MESSAGE = "message";
    String REDIS_KEY_WALLET_MESSAGE = "message:";
    String REDIS_KEY_WALLET_MESSAGE_FIELD = "field:";
    String REDIS_KEY_WALLET_MESSAGE_CODE = "code:";
    String REDIS_KEY_NATIVE_PAY_ORDER = "nativePayOrder:";

    //i18n
    int I18N_SHOW_DEFAULT = 0;
    int I18N_SHOW_NOT_EXIST = 1;
    int I18N_SHOW_EMPTY = 2;
}
