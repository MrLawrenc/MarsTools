package application.utils;

import java.io.*;

import org.apache.commons.codec.binary.StringUtils;

/**
 * 
 * @Description 文件处理相关方法
 * @author LIu Mingyao
 * @date 2019年3月25日下午2:05:06
 */
public class FileUtil {

	/**
	 * 创建文件并写入(文件存在就追加内容)
	 *
	 * @param content  追加的文件内容
	 * @param filePath 文件路径
	 * @author Liu Ming
	 */
	public static boolean writeFile2Txt(String content, String filePath) {
		boolean flag = true;
		BufferedReader bufferedReader = null;
		try {
			File file = new File(filePath);
//            if (!file.exists()) {
//                file.createNewFile();
//            }

			bufferedReader = new BufferedReader(new StringReader(content));
			// 不加true 或者只是用一个参数构造器就不是在文件末尾追加
			BufferedWriter bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(file, true)));

			char buffer[] = new char[1024];
			int len;
			while ((len = bufferedReader.read(buffer)) != -1) {
				bufferedWriter.write(buffer, 0, len);
			}
			bufferedWriter.flush();
			bufferedReader.close();
			bufferedWriter.close();
		} catch (IOException e) {
			flag = false;
			return flag;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	/**
	 * @Description 指定编码读取文件内容
	 * @param filePath 需要读取的文件
	 * @param encoding 以什么编码去读
	 * @return 返回读取到的文件内容
	 * @throws Exception
	 */
	public static String readFileContent(String filePath, String encoding) {
		StringWriter writer = new StringWriter();
		InputStreamReader reader = null;
		File file = new File(filePath);
		try {
			if (encoding == null || "".equals(encoding.trim())) {
				reader = new InputStreamReader(new FileInputStream(file));
			} else {
				reader = new InputStreamReader(new FileInputStream(file), encoding);
			}
			FileUtil.rederCopy2Writer(reader, writer);
		} catch (Exception e) {
			throw new MarsException("文件读取异常", e);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return StringUtil.isEmpty(writer.toString()) ? "系统提示:此文件中没有任何内容！" : writer.toString();
	}

	/**
	 * 
	 * @Description 将reder所在文件的内容全部复制到writer所在的文件
	 * @author LIu Mingyao
	 * @param reader 输出流
	 * @param writer 输入流
	 * @throws Exception
	 */

	public static void rederCopy2Writer(InputStreamReader reader, StringWriter writer) throws Exception {
		char[] buffer = new char[1024];
		int n = 0;
		while (-1 != (n = reader.read(buffer))) {
			writer.write(buffer, 0, n);
		}
	}

	/**
	 * @Description 给指定文件追加指定内容
	 * @param file    操作的文件
	 * @param content 追加的内容
	 * @throws IOException
	 */
	public static void appendContent2File(File file, String content) throws IOException {
		FileWriter fw = new FileWriter(file, true);
		fw.write(content);
		fw.close();
	}

}