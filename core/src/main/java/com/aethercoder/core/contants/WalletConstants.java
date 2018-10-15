package com.aethercoder.core.contants;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hepengfei on 2017/8/30.
 */
public interface WalletConstants {
    Integer ACCOUNT_NO_MIN = 00000000;
    Integer ACCOUNT_NO_MAX = 99999999;
    List<String> RESERVED_ACCOUNT_NO_REG_EXP = Arrays.asList("6666.*", ".*6666", "8888.*", ".*8888", "00000000", "11111111", "22222222",
            "33333333", "44444444", "55555555", "66666666", "77777777", "88888888", "99999999");

    //security
    Integer CAPTCHA_EXPIRE_MINUTE = 30;
    String ORDER_TOKEN_KEY = "QPS12O*asd$34%509T.JIM&23nfusa!";
    String JWT_SECURITY = "7HTqPBOE*1!cpMCeqDEP{?AQnT.zOE+~.xyhgCeqPcaYzN";
    Long JWT_EXPIRATION = 1800000L; //30 min
    Long JWT_APP_EXPIRATION = 259200000L; // 3 days

    String IN_ACCOUNTNO = "100";
    String OUT_ACCOUNTNO = "101";
    String FEE_ACCOUNTNO = "102";

    String DEFAULT_HEADER_PATH = "upload/images/header";
    String DEFAULT_ICON_PATH = "upload/images/icon";
    String DEFAULT_PKG_PATH = "upload/images/pkg";
    String DEFAULT_IDCARD_PATH = "upload/images/IDCard";
    String DEFAULT_TICKET_PATH = "upload/images/ticket";
    String QRCODE_BG_ZH_PATH = "upload/images/qrcode_bg_zh";
    String QRCODE_BG_EN_PATH = "upload/images/qrcode_bg_en";
    String QRCODE_BG_KO_PATH = "upload/images/qrcode_bg_ko";

    String DEFAULT_HEADER = "default.png";
    String DEFAULT_HEADER_NAME = "default.png";
    String ACCOUNT_INACTIVE = "1";
    String ACCOUNT_ACTVATED = "0";

    String SYS_CONFIG_URL = "remote_url";
    String SYS_CONFIG_WALLET_SEED = "wallet_seed";
    String SYS_CONFIG_WALLET_ADDRESS = "wallet_address";
    String SYS_CONFIG_FEE = "fee";
    String SYS_CONFIG_GAS_LIMIT = "gas_limit";
    String SYS_CONFIG_GAS_PRICE = "gas_price";
    String SYS_DEFAULT_EXCHANGE_QBE = "DEFAULT_EXCHANGE_QBE";
    String SYS_DEFAULT_API_ADDRESS_END_TIME = "DEFAULT_API_ADDRESS_END_TIME";
    String SYS_SUM_EXCHANGE_QBT_AMOUNT = "SUM_EXCHANGE_QBT_AMOUNT";
    String SYS_EXCHANGE_QBT_END_TIME = "EXCHANGE_QBT_END_TIME";
    //remote url
    String UNSPENT_URL = "outputs/Unspent";
    String RAW_TRANSACTION_URL = "send-raw-transaction";
    String CONTRACT_URL = "contracts/{addressContract}/call";
    String DGPINFO_URL = "blockchain/dgpinfo";
    String ESTIMATE_FEE_PER_KB_URL = "estimate-fee-per-kb";

    String DECIMALS = "decimals";
    String UINT8 = "uint8";
    String UINT256 = "uint256";

    String QBT_TOKEN_NAME = "QBT";
    String QTUM_TOKEN_NAME = "QTUM";
    String TSL_TOKEN_NAME = "TSL";
    String QBT_ICO_TOKEN_NAME = "QBT-ICO";
    String USER_REWARDS = "user_rewards";

    String QBT_TOKEN_NAME_TEST = "QBT-test";

    String QBAO_DEFAULT_NAME = "Qbao";


    Integer APPLY_STATUS_NOT_APPLY = 0;
    Integer APPLY_STATUS_APPLIED = 1;
    Integer APPLY_STATUS_CANCELED = 2;
    Integer APPLY_STATUS_DONE = 3;
    Integer APPLY_STATUS_EXCHAGED = 4;

    String EVENT_EXPRESSION_VAR_SOURCE = "source";

    //合约名称
    String CONTRACTNAME = "QBT";
    Boolean ADDRESSDEFAULT = true;
    Boolean COMMON_NOT_DELETE = false;

    String GETLAPSE_VALID = "1";
    String GETLAPSE_NOT_VALID = "0";
    //社交-好友-关系列表
    //10：发出好友邀请
    Integer SEND_FRIEND_INVITATION = 10;
    //11：收到好友邀请
    Integer RECEIVE_FRIEND_INVITATION = 11;
    // 12：拒绝好友邀请
    Integer NO_FRIEND_INVITATION = 12;
    //20：加为好友
    Integer ADD_FRIEND = 20;
    //30：删除好友
    Integer DELETE_FRIEND = 30;
    //40：拉黑好友
    Integer SHIELDING_FRIEND = 40;

    //设置操作名。
    String CONTACT_OPERATION_ACCEPT_RESPONSE = "AcceptResponse";
    String CONTACT_OPERATION_REQUEST = "Request";

    String QBAO_ADMIN = "1000";
    String CUSTOMER_ONE = "1001";
    String GROUP_SYSTEM = "1002";
    String PAY_ASSISTANT = "1003";
    String CONFIG_CUSTOMER_SERVICE_PREFIX = "CUSTOMER_SERVICE_URL_";

    String ICO_START_TIME = "ico_start_time";
    String ICO_IS_FINISHED = "ico_is_finished";

    String TIMESTAMP_DEFAULT = "2000-01-01 00:00:00";
    String DEFADEFAULT_ACCOUNT_DATE_MIN = "1990-01-01 00:00:00";
    String DEFADEFAULT_ACCOUNT_DATE_MAX = "2050-01-01 00:00:00";

    String IS_IOS_VERIFY = "IS_IOS_VERIFY";

    String IS_ABROAD = "IS_ABROAD";

    String GET_DATA_CITY_IP = "data/GeoLite2-City.mmdb";

    String EMAIL_VALIDATION = "finish mailbox verification";//"完成邮箱验证";

    String QBAO_FUND_HOMEPAGE = "Homepage";//"官网首页";
    String FUND_ICO_SUCCESS_TYPE = "deposit into the smart contract";

    String PASSWORD_RESET = "reset password";//"进行密码重置";

    //aethercoder邮箱配置Template
    String IO_TEMPLATE = "SendEmailToQbaoIOUser.html";

    //QQ邮箱
    String FUND_TEMPLATE = "SendEmailToFundUser.html";
    // 问卷调查审核结果成功模板
    String FUND_ICO_SUCCESS_TEMPLATRE = "ICOApplySuccessTemplate.html";
    // 问卷调查审核结果失败模板
    String FUND_ICO_FAIL_TEMPLATRE = "ICOApplyFailTemplate.html";

    Integer INVESTIGATION_SOURCE = 2;

    String IOS_VERSION = "iosversion";
    String REQUEST_HEADER_LANGUAGE = "accept-language";

    Integer DEFAULT_PAGE = 0;
    Integer DEFAULT_PAGE_MAX = 100000000;

    Integer DEFAULT_PAGE_SIZE = 10;

    String STATUS_NOT_CHECK = "0";
    String STATUS_APPROVED = "1";
    String STATUS_DECLINED = "2";

    String ICO_QUALIFICATION_NO_APPLY = "0";
    String ICO_QUALIFICATION_CHECKING = "1";
    String ICO_QUALIFICATION_FAILED = "2";
    String ICO_QUALIFICATION_APPROVED = "3";

    Integer GROUP_ROLE_MEMBER = 0;
    Integer GROUP_ROLE_HOSTER = 2;
    Integer GROUP_ROLE_ADMIN = 1;
    Integer GROUP_ROLE_SYSTEM = 9;

    String ICO_ADDRESS = "ico_address";

    Integer GROUP_LEVEL_NORMAL = 0;
    Integer GROUP_LEVEL_DELUXE = 1;
    Integer GROUP_LEVEL_SVIP = 2;
    Integer GROUP_LEVEL_COMPANY = 3;
    Integer GROUP_LEVEL_COMMEND = 4;
    Integer GROUP_LEVEL_HOT = 5;
    Integer GROUP_LEVEL_SYSTEM = 9;

    String GROUP_OPERATION_CREATE = "Create";

    String GROUP_OPERATION_ADD = "Add";

    String GROUP_OPERATION_QUIT = "Quit";

    String GROUP_OPERATION_DISMISS = "Dismiss";

    String GROUP_OPERATION_KICKED = "Kicked";

    String GROUP_OPERATION_RENAME = "Rename";

    String GROUP_OPERATION_BULLETIN = "Bulletin";

    String GROUP_OPERATION_TRANSFER = "Transfer";

    //群主招募结束
    String GROUP_MANAGER_END_TIME = "group_manager_end_time";

    BigDecimal ZERO = new BigDecimal(0);

    //提币 扣除fee
    String DEDUCT_FEE = "deduct_fee";

    //群主申请转账状态 0-未转帐／1-已转账
    Integer NOT_TRANSFER = 0;
    //无奖励
    Integer NOT_GEAR = 0;

    //0-提币 -／ 1 -充值 + ／2-发红包 -／3-收红包 +／4 活动 + / 5 新人奖励+ /6 红包退款 + ／7 新人红包 + ／8 手续费 -/ 9 建群支付 -/10 QBE兑换扣除 -／11 QBE兑换QBT +/12 提币手续费 入账 +/13 提币 复式记账用 -/14 充值 复式记账用 +/15 游戏中奖+ / 16 每日答题奖励 +／17 投注金额 -/18 投注退款+
    Integer MENTION_TYPE = 0;
    Integer RECHANGER_TYPE = 1;
    Integer SEND_RED_BAG = 2;
    Integer RECEIVE_RED_BAG = 3;
    Integer EVENT_TYPE = 4;
    Integer TAKE_BONUS = 5;
    Integer REFUNDS_RED_PACKET = 6;
    Integer USER_REWARDS_TYPE = 7;
    Integer FEE_TYPE = 8;
    Integer BUILD_GROUP = 9;
    Integer EXCHANGE_QBE_MUTIPLY = 10;
    Integer QBE_EXCHANGE_QBT = 11;
    Integer FEE_TYPE_ERAN = 12;
    Integer MENTION_ERAN = 13;
    Integer RECHARGER_ERAN = 14;
    Integer GAME_AWARD = 15;
    Integer QUIZ_AWARD_TYPE = 16;
    Integer GAMBLE_AMOUNT_TYPE = 17;
    Integer GAMBLE_REFUND_TYPE = 18;
    Integer NATIVE_PAY = 19;

    //交易状态 0-未确认／1-已确认／2-失败
    Integer UNCONFIRMED = 0;
    Integer CONFIRMED = 1;
    Integer FAILED = 2;

    //batch
    String BATCH_NAME_INK = "INK";
    String BATCH_NAME_QBT = "QBT";
    String BATCH_NAME_TSL = "TSL";
    String BATCH_NAME_QTUM = "QTUM";
    String BATCH_NAME_ENT = "ENT";
    Integer BATCH_TIMESLOT = 5;
    Integer BATCH_TIMESLOT_ONE = 1;

    Integer RED_PACKET_TYPE_NORMAL = 0;
    Integer RED_PACKET_TYPE_COMPETE = 1;

    String IS_GAP = "1";

    String QBAO_ENERGY = "Qbao Energy";
    String QBAO_ENERGY_ADRESS = "QBE";

    Integer DEFAULT_RECEVIE_NUMBER = 1;
    BigDecimal DEFAULT_INVITE_AMOUNT = new BigDecimal(2000);
    Integer RECEVIE_NUMBER = -1;

    //32进制
    Integer HEXADECIMAL_THREE_TWO = 32;

    // 邀请好友相关
    String INVITE_HTML_URL = "invite_html_url";
    String INVITE_FRIEND_TEXT = "INVITE_FRIEND_TEXT";
    String INVITE_FRIEND_URL = "invite_friend_url";
    String INVITE_NUMBER = "invite_number";
    String INVITE_REWARDS = "invite_rewards";

    // Rate
    String GET_CURRENCY_RATE_API_URL = "currency_rate_api_url";
    String RATE_API_APP_ID = "RATE_API_APP_ID";
    String CURRENCY_CNY = "CNY";
    String CURRENCY_KRW = "KRW";
    String CURRENCY_USD = "USD";

    String SEND_RED_PACKET_BATCH_CLASS_NAME = "com.aethercoder.core.dao.batch.AutoCreateRedPacketBatch";
    String SEND_RED_PACKET_BATCH_NAME_PRIX = "AutoSendPacketBy";

    String BUILD_GROUP_QBT_FEE = "build_group_qbt_fee";
    String BUILD_GROUP_QBE_FEE = "build_group_qbe_fee";

    String QUIZ_SIZE = "QUIZ_SIZE";
    String QUIZ_AWARD = "QUIZ_AWARD";

    BigDecimal TAKE_BOUNS_MAX = new BigDecimal(5000);

    //token相关
    int APPID_MIN_VALUE = 100000000;
    int APPID_MAX_VALUE = 999999999;

    Integer DEFAULT_INVIYED_DAILY = 30;

    //报表
    BigDecimal QBE_LESS_THAN = new BigDecimal(10000);

    //系统公告状态
    Integer NOTICE_STATUS_WAITING = 1;
    Integer NOTICE_STATUS_ISSUE = 2;
    Integer NOTICE_STATUS_RETRIEVE = 0;

    //媒体列表
    Integer MEDIA_STATUS_ISSUE = 1;
    Integer MEDIA_STATUS_RETRIEVE = 0;


    //上传文件类型
    Integer SAVE_FILE_TYPE_TICKET = 1;
    Integer SAVE_FILE_TYPE_EVENT = 2;

    //币种最新价格
    String Contract_Last_Price = "last";
    String CONTRACT_PRICE_USD = "price_usd";

    //竞猜游戏状态
    Integer GUESS_NUMBER_STATUS_NOT_APPLY = 0;
    Integer GUESS_NUMBER_STATUS_RUNNING = 1;
    Integer GUESS_NUMBER_STATUS_NOT_AWARD = 2;
    Integer GUESS_NUMBER_STATUS_END = 3;

    //WithdrawApply
    Integer FEE_TRADING_TREMS_WATING = 1;

    //group_notice
    Integer GROUP_NOTICE_CONTENT_LENGTH = 500;
    Integer GROUP_NOTICE_CONTENT_TITLE = 200;

    //多语言
    String LANGUAGE_TYPE_ZH = "zh";
    String LANGUAGE_TYPE_ZH_NAME = "中国";
    String DEFAULT_NUMBER = "86";
    String LANGUAGE_TYPE_EN = "en";
    String LANGUAGE_TYPE_KO = "ko";
    String LANGUAGE_TYPE_JA = "ja";

    //中奖等级
    Integer AWARD_LEVEL_SPECIAL = 0;
    Integer AWARD_LEVEL_ONE = 1;
    Integer AWARD_LEVEL_TWO = 2;
    Integer AWARD_LEVEL_THREE = 3;
    Integer AWARD_LEVEL_FOURTH = 4;
    Integer AWARD_LEVEL_No = 5;

    //提币请求状态
    Integer WITHDRAW_STATUS_APPLIED = 0;
    Integer WITHDRAW_STATUS_WAITING = 1;
    Integer WITHDRAW_STATUS_EXCHANGED = 2;
    Integer WITHDRAW_STATUS_FAILED = 3;
    Integer WITHDRAW_STATUS_PENDING = 4;

    //小游戏开关
    String SYS_GAME_FLAG = "GAME_FLAG";

    //获取当前有效竞猜有效三种状态
    String NOT_AWARD = "0";
    String JOIN_GAME = "1";
    String NOT_JOIN_GAME = "2";

    Integer TOKEN_CALENDAR_STATUS_DELETE = 0;
    Integer TOKEN_CALENDAR_STATUS_DRAFT = 1;
    Integer TOKEN_CALENDAR_STATUS_APPROVED = 2;

    //method 游戏分类
    String GUESS_NUMBER_METHOD = "findAllGuessNumberGame";


    String GUESS_PERSON_LIMIT_KEY = "GUESS_PERSON_LIMIT";
    BigDecimal INVITING_REWARD = BigDecimal.valueOf(500);
    BigDecimal INVITED_REWARD = BigDecimal.valueOf(1000);
    BigDecimal UPLOAD_HEADER_REWARD = BigDecimal.valueOf(500);
    String SUM_QBAO_ENERGY = "SUM_QBAO_ENERGY";
    BigDecimal GAINED_REWARD = BigDecimal.valueOf(2000);
    BigDecimal GAINING_REWARD = BigDecimal.valueOf(1000);
    BigDecimal INVITED_WALLET_ACHIEVING_REWARD = BigDecimal.valueOf(5000);
    BigDecimal INVITING_WALLET_ACHIEVING_REWARD = BigDecimal.valueOf(2000);

    //活动开始时间
    String INVITEING_REWARD_START = "INVITEING_REWARD_START";
    //活动结束时间
    String INVITEING_REWARD_END = "INVITEING_REWARD_END";

    //幸运用户中奖字符串
    String LUCK_STRING_QBAO = "QBAO";
    String LUCK_STRING_NT = "NT";
    BigDecimal LUCK_REWARD_THREE = BigDecimal.valueOf(10000);
    BigDecimal LUCK_REWARD_FOUR = BigDecimal.valueOf(30000);
    BigDecimal LUCK_REWARD_FIVE = BigDecimal.valueOf(100000);
    BigDecimal FOUR_LUCK_REWARD_THREE = BigDecimal.valueOf(25000);

    //邀请奖励名称
    String INVITED_REWARD_NAME = "新用户邀请奖励";
    String INVITING_REWARD_NAME = "邀请人奖励";
    String UPLOAD_REWARD_NAME = "上传头像奖励";
    String GAINED_REWARD_NAME = "用户出师奖励";
    String GAINING_REWARD_NAME = "邀请人出师奖励";
    String INVITED_WALLET_REWARD_NAME = "用户钱包成就奖励";
    String INVITING_WALLET_REWARD_NAME = "邀请人钱包成就奖励";
    String INVITED_FOUR_TO_THREE = "用户四位中三个";
    String INVITING_FOUR_TO_THREE = "邀请人四位中三个";
    String INVITED_SIX_TO_THREE = "用户六位中三个";
    String INVITING_SIX_TO_THREE = "邀请人六位中三个";
    String INVITED_SIX_TO_FOUR = "用户六位中四个";
    String INVITING_SIX_TO_FOUR = "邀请人六位中四个";
    String INVITED_SIX_TO_FIVE = "用户六位中五个";
    String INVITING_SIX_TO_FIVE = "邀请人六位中五个";

    BigDecimal QUESTIONNAIRE_REWARD = BigDecimal.valueOf(2500);

    //exchangeLogType表名，字段名，id
    String EXCHANGELOGTYPE_TABLE_NAME = "t_exchange_type";
    String EXCHANGELOGTYPE_TYPE_NAME = "type_name";

    String WITHDRAW_POOL_LACK_LIMIT= "WITHDRAW_POOL_LACK_LIMIT";

    String WITHDRAW_POOL_LOTS_FAIL= "WITHDRAW_POOL_LOTS_FAIL";
    String WITHDRAW_WARNING_ACCOUNT_LIST= "WITHDRAW_WARNING_ACCOUNT_LIST";

    //message 默认语言
    String MAESSAGE_LANGUAGE_ZH = "zh";
    String MAESSAGE_LANGUAGE_EN = "en";
    String MAESSAGE_LANGUAGE_KO = "ko";

    //支付类型
    String PAY_TYPE = "扫码支付";

    //推送类型
    String JPUSH_TYPE_CANLADER = "0";
    String JPUSH_TYPE_GAME = "1";
    String JPUSH_TYPE_EVENT = "2";
    String JPUSH_TYPE_NOTICE = "3";
    String JPUSH_TYPE_TO_GROUP = "4";


    Integer CONTRACT_QTUM_TYPE = 0;
    Integer CONTRACT_ETH_TYPE = 1;
    //商户状态
    Integer CUSTOMER_STATUS_ACTIVE = 1;
    Integer CUSTOMER_STATUS_UNACTIVE = 0;

    //阿里云sms
    String SIGH_NAME= "Qbao支付";

    //PAY -支付短信模版id
    String PAY_FAIL_ZH= "SMS_129761828";
    String PAY_SUCCESS_ZH = "SMS_129756786";

    //PAY-订单状态

    //订单支付类型 0-静态扫码支付／1-被动扫码支付
    Integer ORDER_NATIVE_PAY = 0;
    Integer ORDER_SCAN_PAY = 1;

    //商户二维码背景图
    String QRCODE_BG_ZH = "qrcode_bg_zh.png";
    String QRCODE_BG_EN = "qrcode_bg_en.png";
    String QRCODE_BG_KO = "qrcode_bg_ko.png";

    //对账结果未清算通知发送对象
    String CLEARANCE_WARNING_MOBILE_LIST= "CLEARANCE_WARNING_MOBILE_LIST";

    String QBAO_PAY_STR = "【Qbao Pay】";
    String QBAO_PAY_STR_ZH = "【Qbao支付】";
    String QBAO_STR= "【Qbao】";

    //支付短信密码发送手机号
    String QBAO_PAY_PHONE = "QBAO_PAY_PHONE";
    String QBAO_PAY_RATE_URL = "QBAO_PAY_RATE_URL";
    String QBAO_PAY_NOTICE_PHONE = "QBAO_PAY_NOTICE_PHONE";

    // 支付每日上限
    String QBAO_PAY_LIMIT_DAY = "QBAO_PAY_LIMIT_DAY";
    // 支付单笔上限
    String QBAO_PAY_LIMIT_PER = "QBAO_PAY_LIMIT_PER";
    // 支付汇率下调幅度
    String QBAO_PAY_RATE_BONUS = "QBAO_PAY_RATE_BONUS";

    //进群验证状态
        //默认关闭
    Integer JOIN_GROUP_STATUS_DEFAULT = 0;
        //通过身份验证
    Integer JOIN_GROUP_STATUS_IDENTITY = 1;
        //通过口令验证
    Integer JOIN_GROUP_STATUS_COMMAND = 2;

    //进群申请处理结果
        //待处理
    Integer APPLICATION_RESULT_PENDING = 0;
        //已同意
    Integer APPLICATION_RESULT_AGREED = 1;
        //已拒绝
    Integer APPLICATION_RESULT_REFUSED = 2;

    //进群口令
    String JOIN_GROUP_COMMAND = "JOIN_GROUP_COMMAND";

    //资产类型
    Integer ASSETS_TYPE_QTUM = 0;
    Integer ASSETS_TYPE_ETH = 1;

    //清算对账状态(0:正常/无需对账 1:待处理 2:已处理）
    Integer CLEARANCE_ACCOUNT_STATUS_NORMAL = 0;
    Integer CLEARANCE_ACCOUNT_STATUS_CONFIRM = 1;
    Integer CLEARANCE_ACCOUNT_STATUS_FINISH = 2;

    //币月历大事件分享地址
    String TOKEN_CALENDER_EVENT_URL = "TOKEN_CALENDER_EVENT_URL";
    //群分享地址
    String GROUP_URL  = "GROUP_URL";
    //精彩小游戏地址
    String GUESS_URL = "GUESS_URL";
    //每日答题分享地址
    String ACTIVITIES_URL = "ACTIVITIES_URL";

    // 通讯消息OperationStatus
    String OPERATION_STATUS_NO_SHOWN = "0";
    String OPERATION_STATUS_PENDING = "1";
    String OPERATION_STATUS_AGREED = "2";
    String OPERATION_STATUS_REFUSED = "3";

    //客服地址
    String CUSTOMER_SERVICE_URL_ZN = "CUSTOMER_SERVICE_URL_ZN";
    String CUSTOMER_SERVICE_URL_EN = "CUSTOMER_SERVICE_URL_EN";
    String CUSTOMER_SERVICE_URL_KO = "CUSTOMER_SERVICE_URL_KO";
}
