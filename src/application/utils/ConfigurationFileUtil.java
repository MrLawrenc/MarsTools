package application.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import application.translate.baidu.BaiDuTrans;

public class ConfigurationFileUtil {
	private ConfigurationFileUtil() {
	}

	public static final ConfigurationFileUtil obj;
	static {
		obj = new ConfigurationFileUtil();
	}

	/**
	 * 
	 * @Description 读取到项目的资源配置文件,并将每行数据存入集合中
	 * @author LIu Mingyao
	 */
	public List<String> getPropFromConfigFile() {
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		List<String> properties = new ArrayList<String>();
		try {
			inputStream = BaiDuTrans.class.getClassLoader().getResourceAsStream("tools.mars");
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			String line = "";

			while ((line = bufferedReader.readLine()) != null) {
				properties.add(line);
			}
		} catch (IOException e) {
			throw new MarsException("读取tools.mars配置文件错误", e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
					bufferedReader.close();
				}
			} catch (IOException e) {
			}

		}
		if (properties.isEmpty())
			throw new MarsException("配置文件内容为空,请添加项目所必须的appid和key!");
		System.out.println("配置文件属性列表:"+properties);
		return properties;
	}

	public String[] getTransProp() {

		return null;
	}

	/**
	 * 
	 * @Description 装配配置文件中的所有属性
	 * @param props 配置文件属性集合
	 * @author LIu Mingyao
	 */
	public void assemblyProps(List<String> props) {
		for (String prop : props) {
			String[] split = prop.split("=");
			switch (split[0]) {
			case "baiduTrans.appId":
				BaiDuTrans.obj.setAPP_ID(split[1]);

			case "baiduTrans.securityKey":
				BaiDuTrans.obj.setSECURITY_KEY(split[1]);
			}
		}
	}

}
