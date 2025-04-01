package com.test;

import ch.qos.logback.classic.Logger;
import com.yl.config.log.LogAppenderConfig;

/**
 * @author suiwp
 * @date 2025/4/1 15:47
 */
public class TestLogAppend {
    public static void main(String[] args) {
//        log();
        t(new String[]{"a", "b"});
        t((String) null);
    }

    public static void log() {
        LogAppenderConfig logAppenderConfig = new LogAppenderConfig();
        Logger testLog = logAppenderConfig.getLogger("test_log");
        testLog.info("1---------- aosjhdfpoajsdofj");
        testLog.info("2---------- aosjhdfpoajsdofj");
        testLog.error("2---------- aosjhdfpoajsdofj");

    }

    private static void t(String... a) {
        System.out.println(a);
        for (String s : a) {
            System.out.println(s);
        }
        System.out.println();
    }
}
