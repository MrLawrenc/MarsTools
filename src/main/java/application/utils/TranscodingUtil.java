package application.utils;

/**
 * @Description 转码工具类 <a>默认都是与utf-8之间的互相转换
 * @author LIu Mingyao
 * @date 2019年3月27日下午6:44:31
 */
public class TranscodingUtil {

	/**
	 * @Description 将可显示，即可打印的unicode编码的字符串转换为String
	 * @param unicode 可打印的unicode字符 如：\u0041\u002d
	 * @return 结果字符串 \u0041\u002d 转换为 A-
	 * @author LIu Mingyao
	 */
	public static String UnicodeDecode(String unicode) {

		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			end = unicode.indexOf("\\u", start + 2);
			String charStr = "";
			if (end == -1) {
				charStr = unicode.substring(start + 2, unicode.length());
			} else {
				charStr = unicode.substring(start + 2, end);
			}
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
			buffer.append(new Character(letter).toString());
			start = end;
		}
		return buffer.toString();
	}

	/**
	 * @Description 获取unicode编码的数值
	 * @param string 需要转换的字符串 如：传入A- 返回{64,45}
	 * @return int数组，比如传入A 返回 64 ; 传入 - 返回 45
	 * @author LIu Mingyao
	 */
	public static int[] getUnicodeValue(String string) {

		char[] chars = string.toCharArray();
		int values[] = new int[chars.length];
		String unicode = null;

		for (int i = 0; i < chars.length; i++) {
			unicode = String.valueOf((int) chars[i]);
			int value = Integer.valueOf(unicode.toString()).intValue();
			values[i] = value;
		}

		return values;
	}

	/**
	 * @Description 对传入的ascii字符编码为utf-8字符串，传入格式为value="96,45,65";
	 * @param value 如传入"65,45"
	 * @return 转码后的字符串 如："65,45" 转换为 "A-"
	 * @author LIu Mingyao
	 */
	public static String asciiToString(String value) {
		StringBuffer sbu = new StringBuffer();
		String[] chars = value.split(",");
		for (int i = 0; i < chars.length; i++) {
			sbu.append((char) Integer.parseInt(chars[i]));
		}
		return sbu.toString();
	}
	/**
	 * @Description 将字符串转化为了可显示的unicode字符串 如A 变为 \u0041 ; A- 变为 \u0041\u002d
	 * @param gbString 需要转化的字符串
	 * @return 编码后的字符串
	 * @author LIu Mingyao
	 */
	public static String UnicodeEncode(String string) {
		char[] utfBytes = string.toCharArray();
		String unicodeBytes = "";
		for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
			String hexB = Integer.toHexString(utfBytes[byteIndex]); // 转换为16进制整型字符串
			if (hexB.length() <= 2) {
				hexB = "00" + hexB;
			}
			unicodeBytes = unicodeBytes + "\\u" + hexB;
		}
		return unicodeBytes;
	}

	public static void main(String[] args) {

		int[] value = getUnicodeValue("A-");

		System.out.println("第一个字符对应的unicode值为" + value[0]);

		System.out.println(asciiToString("65,45"));

		System.out.println(UnicodeEncode("A-"));

		System.out.println(UnicodeDecode("\\u0041\\u002d"));

	}

}
