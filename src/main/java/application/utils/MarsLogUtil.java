package application.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author LIu Mingyao
 * @Description 日志工具类
 * @date 2019年4月20日下午3:30:30
 */
public class MarsLogUtil {
    private MarsLogUtil() {
    }


    private static Logger getInstance(Class<? extends Object> clz) {
        return LoggerFactory.getLogger(clz);
    }

    public static void info(Class<? extends Object> clz, String msg) {
        getInstance(clz).info(msg);
    }

    public static void info(Class<? extends Object> clz, String msg, Exception e) {
        getInstance(clz).info(msg, e);
    }


    public static void error(Class<? extends Object> clz, String msg, Exception e) {
        getInstance(clz).error(msg, e);
    }

    public static void error(Class<? extends Object> clz, String msg) {
        getInstance(clz).error(msg);
    }

    public static void debug(Class<? extends Object> clz, String msg) {
        getInstance(clz).debug(msg);
    }

    public static void debug(Class<? extends Object> clz, String msg, Exception e) {
        getInstance(clz).debug(msg, e);
    }
}
