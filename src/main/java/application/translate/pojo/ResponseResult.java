package application.translate.pojo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 百度翻译响应的结果
 * 
 * @author LIu Mingyao
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult {

	private String from;// 被翻译的语言
	private String to;// 翻译成什么语言
	private List<TransResult> trans_result;// 翻译实体对象

}
