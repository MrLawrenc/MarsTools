package application.translate.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 百度翻译响应的结果
 *
 * @author LIu Mingyao
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult {

    /**
     * 被翻译的语言
     */
    private String from;
    /**
     * 翻译成什么语言
     */
    private String to;
    /**
     * 翻译实体对象
     */
    @JSONField(name = "trans_result")
    private List<TransResult> transResult;

}
