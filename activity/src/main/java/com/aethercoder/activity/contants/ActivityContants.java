package com.aethercoder.activity.contants;

/**
 * @auther Guo Feiyan
 * @date 2018/2/27 下午7:48
 */
public interface ActivityContants {

    //投注小游戏 状态
    Integer GUESS_GAMBLE_STATUS_NOT_APPLY = 0;
    Integer GUESS_GAMBLE_STATUS_RUNNING = 1;
    Integer GUESS_GAMBLE_STATUS_CLOSE = 2;
    Integer GUESS_GAMBLE_STATUS_AWARD = 3;
    Integer GUESS_GAMBLE_STATUS_FLOW = 4;

    //i18n
    String LANGANGE_ZH = "zh";
    String LANGANGE_EN = "en";
    String LANGANGE_KO = "ko";

    //选项c=liu 流盘
    Character OPTION_A = 'A';
    Character OPTION_B = 'B';
    Character OPTION_C = 'C';
}