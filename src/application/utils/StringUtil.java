package application.utils;

/**
 * 
 * @Description 字符串相关操作
 * @author LIu Mingyao
 * @date 2019年3月25日下午2:21:26
 */
public class StringUtil {

	/**
	 * 
	 * @Description 判断传入的字符串是否是空串
	 * @author LIu Mingyao
	 * @param str 需要判断的字符串
	 * @return true为空串
	 */
	public static boolean isEmpty(String str) {
		return str.trim().isEmpty() || str == null ? true : false;
	}

}
