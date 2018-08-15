package com.finance.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class basic_tools {
    public static boolean HasDigit(String content) {
        boolean flag = false;
        Pattern p = Pattern.compile(".*\\d+.*");
        Matcher m = p.matcher(content);
        if (m.matches()) {
            flag = true;
        }
        return flag;
    }
}
