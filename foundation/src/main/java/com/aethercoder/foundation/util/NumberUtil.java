package com.aethercoder.foundation.util;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * Created by hepengfei on 2017/8/30.
 */
public class NumberUtil {

    private static char[] array = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
            .toCharArray();


    public static String formatNumberToString(Integer num, String format) {
        return new DecimalFormat(format).format(num);
    }

    public static String formatBigDecimalToCurrency(BigDecimal num) {
        if (num == null || num.equals(new BigDecimal(0))) {
            return "0.00";
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(6);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumIntegerDigits(10);
        numberFormat.setMinimumIntegerDigits(1);
        return numberFormat.format(num.doubleValue());
    }

    //10进制转为其他进制，除留取余，逆序排列
    public static String number_10_to_N(long number, int N) {
        Long rest = number;
        Stack<Character> stack = new Stack<Character>();
        StringBuilder result = new StringBuilder(0);
        while (rest != 0) {
            stack.add(array[new Long((rest % N)).intValue()]);
            rest = rest / N;
        }
        for (; !stack.isEmpty();) {
            result.append(stack.pop());
        }
        String shareCode = StringUtils.leftPad(result.toString(),6,"A");
        return shareCode;

    }

    /*方法二：推荐，速度最快
  * 判断是否为整数
  * @param str 传入的字符串
  * @return 是整数返回true,否则返回false
*/

    public static boolean isInteger(String number) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(number).matches();
    }
}
