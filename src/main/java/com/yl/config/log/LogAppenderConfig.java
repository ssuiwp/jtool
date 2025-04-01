package com.yl.config.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @Author:xingpf
 * @Date: 2020/4/30  14:22
 * @Descriptions:动态生成logback.xml配置文件的appender
 */
public class LogAppenderConfig {


    public LogAppenderConfig(String logBasePath) {
        if (logBasePath == null || logBasePath.isEmpty()) {
            this.logBasePath = "./logs/";
        } else {
            this.logBasePath = appendIfMissing(logBasePath, "/");
        }
    }

    public LogAppenderConfig() {
        this(null);
    }

    /**
     * 日志打印根目录
     */
    private final String logBasePath;
//    private final String logPattern;
//    private final String logFile;
//    private final String logLevel;

    /**
     * 本地静态ConcurrentHashMap存储已有的日志文件名，线程安全
     */
    private final Map<String, Logger> logFileNameMap = new ConcurrentHashMap<>();


    /**
     * 根据logFileName获取logger对象
     *
     * @param logFileName
     * @return
     */
    public Logger getLogger(String logFileName) {
        Logger logger = logFileNameMap.get(logFileName);
        if (logger != null) {
            return logger;
        }
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        logger = createLogger(logFileName, loggerContext, createDefaultAppenderList(logFileName, loggerContext));
        logFileNameMap.put(logFileName, logger);
        return logger;
    }


    /**
     * 根据传入logFileName创建当前Logger对象输出日志的文件名
     *
     * @param logFileName 要创建的日志文件名称
     * @return
     */
    private synchronized Logger createLogger(String logFileName,
                                             LoggerContext loggerContext,
                                             Appender... appenderList) {

        //创建logger对象
        Logger logger = loggerContext.getLogger(logFileName);
        logger.setAdditive(false);

        for (Appender appender : appenderList) {
            logger.addAppender(appender);
        }

        return logger;

    }

    private Appender<ILoggingEvent>[] createDefaultAppenderList(String logFileName,
                                                                LoggerContext loggerContext) {
        String logPattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%X{tid}] [%t] %c [%class.%method\\(\\) %line{}L]%n-> %m%n";
        AsyncAppender asyncAppender = createDefaultAsyncAppender(logFileName, logPattern, loggerContext);

        /*设置动态日志控制台输出*/
        ConsoleAppender consoleAppender = createDefaultConsoleAppender(logPattern, loggerContext);

        return new Appender[]{consoleAppender, asyncAppender};
    }

    private AsyncAppender createDefaultAsyncAppender(String logFileName,
                                                     String logPattern,
                                                     LoggerContext loggerContext) {
        AsyncAppender asyncAppender = new AsyncAppender();
        asyncAppender.setContext(loggerContext);

        //创建appender，滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件
        asyncAppender.setDiscardingThreshold(0);
        asyncAppender.setQueueSize(512);

        RollingFileAppender appender = createRollingFileAppender(logFileName, logPattern, loggerContext);
        asyncAppender.addAppender(appender);

        asyncAppender.start();
        return asyncAppender;
    }

    private RollingFileAppender createRollingFileAppender(String logFileName,
                                                          String logPattern,
                                                          LoggerContext loggerContext) {
        RollingFileAppender appender = new RollingFileAppender();

        appender.setContext(loggerContext);
        // 设置appender的名称
        appender.setName(logFileName);
        // 创建活动日志（当天日志）打印文件
        appender.setFile(OptionHelper.substVars(logBasePath + logFileName + "/collection.log", loggerContext));
        // 设置日志是否追加到文件结尾，true:时是,false:否
        appender.setAppend(true);
        // 日志是否线程安全写入文件，true：是，false：否，默认false
        appender.setPrudent(false);

        // 定义滚动策略，按时间及大小进行滚动
        SizeAndTimeBasedRollingPolicy rollingPolicy = new SizeAndTimeBasedRollingPolicy();
//        SizeBasedTriggeringPolicy rollingPolicy = new SizeBasedTriggeringPolicy();

        // 定义归档文件路径及名称
        String filePath = OptionHelper.substVars(logBasePath + logFileName + "/collection/.%d{yyyy-MM-dd}.%i.log", loggerContext);
        rollingPolicy.setParent(appender);
        rollingPolicy.setContext(loggerContext);
        // 设置单个文件大小
        rollingPolicy.setMaxFileSize(FileSize.valueOf("50MB"));
        // 设置归档文件名
        rollingPolicy.setFileNamePattern(filePath);
        // 设置归档文件保留的最大数量，这里设置30天
        rollingPolicy.setMaxHistory(30);
        // 设置全部日志文件最大体积
        rollingPolicy.setTotalSizeCap(FileSize.valueOf("2GB"));
        rollingPolicy.start();

        // 设置appender记录日志的滚动策略
//        appender.setRollingPolicy(rollingPolicy);
        appender.setTriggeringPolicy(rollingPolicy);
        //设置输出到日志文件的格式
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        // 日志格式
//        encoder.setPattern("%d#|###|#%m%n");
        encoder.setPattern(logPattern);
        encoder.start();
//         %clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr([%p]){faint} %clr(${PID}){magenta}%clr{faint} %clr([%t]){faint} %green([%c]) %cyan([%M]) [%L] -> %n\t%m%n
        //<pattern>%black(控制台-) %red(%d{yyyy-MM-dd HH:mm:ss}) %green([%thread]) %highlight(%-5level) %boldMagenta(%logger) - %cyan(%msg%n)</pattern>
        appender.setEncoder(encoder);
        appender.start();
        return appender;
    }

    private ConsoleAppender createDefaultConsoleAppender(String logPattern,
                                                         LoggerContext loggerContext) {
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(loggerContext);

        //设置输出到控制台的日志文件格式
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(loggerContext);
//        encoder1.setPattern("%d %p (%file:%line\\)- %m%n");
        encoder1.setPattern(logPattern);
        encoder1.start();

        consoleAppender.setEncoder(encoder1);
        consoleAppender.start();
        return consoleAppender;
    }

    private Appender<ILoggingEvent> createDefaultAppenderBak(String logFileName,
                                                             LoggerContext loggerContext) {

        //创建appender，滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件
        RollingFileAppender appender = new RollingFileAppender();
        appender.setContext(loggerContext);
        // 设置appender的名称
        appender.setName(logFileName);
        // 创建活动日志（当天日志）打印文件
        appender.setFile(OptionHelper.substVars(logBasePath + logFileName + "/collection.log", loggerContext));
        // 设置日志是否追加到文件结尾，true:时是,false:否
        appender.setAppend(true);
        // 日志是否线程安全写入文件，true：是，false：否，默认false
        appender.setPrudent(false);

        // 定义滚动策略，按时间及大小进行滚动
        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
        // 定义归档文件路径及名称
        String filePath = OptionHelper.substVars(logBasePath + logFileName + "/collection/.%d{yyyy-MM-dd}.%i.log", loggerContext);
        policy.setParent(appender);
        policy.setContext(loggerContext);
        // 设置单个文件大小
        policy.setMaxFileSize(FileSize.valueOf("50MB"));
        // 设置归档文件名
        policy.setFileNamePattern(filePath);
        // 设置归档文件保留的最大数量，这里设置30天
        policy.setMaxHistory(30);
        // 设置全部日志文件最大体积
        policy.setTotalSizeCap(FileSize.valueOf("2GB"));
        policy.start();

        //设置输出到日志文件的格式
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        // 日志格式
        encoder.setPattern("%d#|###|#%m%n");
        encoder.start();

        //设置输出到控制台的日志文件格式
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(loggerContext);
        encoder1.setPattern("%d %p (%file:%line\\)- %m%n");
        encoder1.start();

        /*设置动态日志控制台输出*/
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(encoder1);
        consoleAppender.start();
//        logger.addAppender(consoleAppender);


        // 设置appender记录日志的滚动策略
        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();
        return null;
    }


    private static String appendIfMissing(String str, String suffix) {
        if (str == null) {
            return null;
        }
        if (!str.endsWith(suffix)) {
            return str.concat(suffix);
        } else {
            return str;
        }
    }
}

