package com.smart.workflow.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串替换
 *
 * @author violet
 * @version 1.0
 * @date 2021/01/25 18:04
 */
public class StringUtils {
    public static String convertStr(String str, Map<?, ?> map) {
        if (str == null || map == null) {
            return str;
        }
        String regex = "\\{\\w+}";
        //拆分为数组
        String[] split = str.split(regex);

        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;

        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(str);
        while (matcher.find()) {
            //变量名
            String paramName = matcher.group();
            //获取变量值
            Object value = map.get(paramName.substring(1, paramName.length() - 1));

            stringBuilder.append(split[index++]).append(value);
        }
        if (index < split.length) {
            stringBuilder.append(split[split.length - 1]);
        }
        return stringBuilder.toString();
    }

}
