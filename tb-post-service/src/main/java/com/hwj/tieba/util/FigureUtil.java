package com.hwj.tieba.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FigureUtil {


    /**
     * 判断字符串是否是数字
     * @param str 需要验证的字符串
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
