package application.config;

import application.translate.baidu.BaiDuTrans;
import application.utils.MarsException;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrLawrenc
 * date  2020/6/30 23:14
 * <p>
 * 配置文件解析
 */
@Slf4j
public class Configuration {

    private static final String RESOURCE_NAME = "tools.mars";
    public static String toolsVersion;

    private Configuration() {
    }

    /**
     * 读取到项目的资源配置文件,并将每行数据存入集合中
     */
    public static void initProp() {
        InputStream inputStream = null;
        List<String> properties = new ArrayList<>();
        try {
            //优先读取工程目录
            File file = new File(RESOURCE_NAME);
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = Configuration.class.getClassLoader().getResourceAsStream(RESOURCE_NAME);
            }
            Preconditions.checkNotNull(inputStream, "input stream is null");
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    properties.add(line);
                }
            }
        } catch (IOException e) {
            throw new MarsException("read tools.mars config error", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignored) {
            }
        }
        if (properties.isEmpty()) {
            throw new MarsException("config is empty,must add app id and key!");
        }

        log.info("config list : {}", JSON.toJSONString(properties));
        assemblyProps(properties);
    }

    public String[] getTransProp() {

        return null;
    }

    /**
     * 装配配置文件中的所有属性
     */
    private static void assemblyProps(List<String> props) {
        for (String prop : props) {
            String[] split = prop.split("=");
            switch (split[0]) {
                case "baiduTrans.appId":
                    BaiDuTrans.obj.setAPP_ID(split[1]);
                    break;
                case "baiduTrans.securityKey":
                    BaiDuTrans.obj.setSECURITY_KEY(split[1]);
                    break;
                case "tools.version":
                    toolsVersion = split[1];
                    break;
                default:
            }
        }
    }

}
