package application.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Description 日志工具类
 * @author LIu Mingyao
 * @date 2019年4月20日下午3:30:30
 */
public class MarsLogUtil {
	private MarsLogUtil() {}

	private static LogFactory factory = LogFactory.getFactory();

	private static Log getInstance(Class<? extends Object> clz) {
		return factory.getInstance(clz);
	}

	public static void info(Class<? extends Object> clz, String msg, Exception e) {
		getInstance(clz).info(msg, e);
	}

	public static void info(Class<? extends Object> clz, String msg) {
		getInstance(clz).info(msg);
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
