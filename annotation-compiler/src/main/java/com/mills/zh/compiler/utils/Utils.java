package com.mills.zh.compiler.utils;

/**
 * Created by zhangmd on 2018/11/29.
 */

public class Utils {

    public static String firstCharUpperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}
