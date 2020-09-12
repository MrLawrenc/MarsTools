package application.translate.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 翻译对象
 * 
 * @author LIu Mingyao
 *
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransResult {
	private String src;// 需要翻译的内容
	private String dst;// 需要翻译的内容
}
