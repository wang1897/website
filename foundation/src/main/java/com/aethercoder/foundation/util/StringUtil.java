package com.aethercoder.foundation.util;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @auther Guo Feiyan
 * @date 2017/10/10 下午4:37
 */
public class StringUtil {

    /**
     * 检测是否有emoji字符
     * @Deprecated  该方法对于中文字符处理有问题
     * @param source
     * @return 一旦含有就抛出
     */
    @Deprecated
    public static boolean containsEmoji(String source) {
        if (StringUtils.isBlank(source)) {
            return false;
        }
            //\u20a0-\u32ff\ud83c\udc00-\ud83d\udeff\udbb9\udce5-\udbb9\udcee
        int len = source.length();
        final String regex = "([\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff])";
        Matcher matchEmo = Pattern.compile(regex).matcher(source);
        if (matchEmo.find()) {
            return true;
        }

        return false;
    }

    /**
     * @throw AppException 如果长度太长
     */
    @Deprecated
    public static boolean isLengthOverThrowEx(String source, Integer length) {
        if (length < source.length()){
            throw new AppException(ErrorCode.NOTICE_CONTENT_LENGTH_MSG, new String[]{source});
        }
        return false;
    }

    /**
     * @throw AppException 如果包含非法字符
     */
    @Deprecated
    public static boolean containsIllegalcharsThrowEx(String source) {
        // 判断名字中是否含有非法字符。
        Pattern pattern = Pattern.compile("[&<>\"'/]");
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            throw new AppException(ErrorCode.NOTICE_CONTENT_INVALID_MSG, new String[]{source});
        }
        return false;
    }

    /**
     * 非法字符，长度是否超过数据库设置
     * @param target 字符串
     * @param length 长度
     * @return 一旦含有就抛出错误，没错则返回false
     */
    public static boolean isIllegalDBVercharWithoutEmoji(String target,Integer length){
        if(isLengthOverThrowEx(target,length) && containsIllegalcharsThrowEx(target)){
            return true;
        }
        return  false;
    }

    /**
     * EMOJI表情，非法字符，长度是否超过数据库设置
     * @param target 字符串
     * @param length 长度
     * @return 一旦含有就抛出错误，没错则返回false
     * @throw AppException 如果包含表情
     */
    @Deprecated
    public static boolean isIllegalDBVercharThrowEx(String target,Integer length){
        if (containsEmoji(target)){
            throw new AppException(ErrorCode.CANNOT_CONTAIN_EMOJI, new String[]{target});
        }
        if(isLengthOverThrowEx(target,length) && containsIllegalcharsThrowEx(target)){
            return true;
        }
        return  false;
    }
}
