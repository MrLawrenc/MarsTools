package application.translate.baidu;

import com.alibaba.fastjson.JSON;

import application.translate.pojo.ResponseResult;
import application.utils.MarsException;
import lombok.Setter;

/**
 * @author LIu Mingyao
 * 2019年3月25日下午3:51:40
 * <p>
 * 百度翻译入口
 */
@Setter
public class BaiDuTrans {

    /**
     * doc:http://api.fanyi.baidu.com/api/trans/product/apidoc
     */
    public static final BaiDuTrans obj;
    private String APP_ID;
    private String SECURITY_KEY;

    private BaiDuTrans() {
    }

    static {
        obj = new BaiDuTrans();
    }

    /**
     * @param query 需要翻译的内容
     * @param from  输入的语言。语言种类支持： zh中文 jp日语 en英语 ru俄语 kor韩语 wyw文言文
     * @param to    需要翻译成什么语言
     * @return 翻译结果
     * 得到翻译结果:
     * api接口响应正常是:{"from":"en","to":"zh","trans_result":[{"src":"i love you","dst":"\u6211\u7231\u4f60"}]}<br>
     * api接口响应异常是:{"error_code":"54001","error_msg":"Invalid Sign"}
     */
    public static String getTransResult(String query, String from, String to) {
        TransApi api = new TransApi(obj.APP_ID, obj.SECURITY_KEY);
        String transResult = api.getTransResult(query, from, to);
        // 调用接口出错的情况
        if (transResult.contains("error_code")) {
            throw new MarsException("调用百度翻译api接口出错  错因是:" + transResult.split("error_msg\":")[1].replace("\"", "").replace("}", ""));
        }
        ResponseResult rs = JSON.parseObject(transResult, ResponseResult.class);
        return rs.getTransResult().get(0).getDst();
    }

    public static String getTransResult(String query) {
        return getTransResult(query, "auto", "auto");
    }

    /**
     * 注：java自身认识 \ u 类型的unicode编码,可以自动转化
     * 百度翻译返回的翻译结果是unicode字符,该方法可以将unicode编码的字符转换为中文
     */
    @Deprecated
    private static String unicodeToCn(String unicode) {
        /** 以 \ u 分割，因为java注释也能识别unicode，因此中间加了一个空格 */
        String[] strs = unicode.split("\\\\u");
        StringBuilder returnStr = new StringBuilder();
        // 由于unicode字符串以 \ u 开头，因此分割出的第一个字符是""。
        for (int i = 1; i < strs.length; i++) {
            returnStr.append((char) Integer.valueOf(strs[i], 16).intValue());
        }

        return returnStr.toString();
    }

}
