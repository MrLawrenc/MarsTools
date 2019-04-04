package application.utils;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @Description 发送请求
 * @author LIu Mingyao
 * @date 2019年3月26日上午10:47:43
 */
public class HttpUtil {

	/**
	 * @Description 发送不需要设置请求头的get请求
	 * @param url 请求地址
	 * @return 返回的html文档
	 * @author LIu Mingyao
	 */
	public static String httpGet(String url) {
		HttpGet get = new HttpGet(url);
		CloseableHttpClient client = HttpClients.createDefault();
		String string = "";
		CloseableHttpResponse response = null;
		try {
			response = client.execute(get);

			string = EntityUtils.toString(response.getEntity(), "utf-8");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (response != null) response.close();
				if (client != null) client.close();
				get.abort();
			} catch (IOException e) {
				System.out.println("关闭client/response发生异常!");
				e.printStackTrace();
			}
		}

		return string;
	}

	/**
	 * @Description 发送不需要设置请求头的get请求
	 * @param url 请求地址
	 * @return 返回的reponse对象
	 * @author LIu Mingyao
	 */
	public static CloseableHttpResponse httpGet4Response(String url) {
		HttpGet get = new HttpGet(url);
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			return client.execute(get);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (client != null) client.close();
				get.abort();
			} catch (IOException e) {
				System.out.println("关闭client/response发生异常!");
				e.printStackTrace();
			}
		}
		return response;
	}
	/**
	 * @Description 需要设置header和params的get请求
	 * @param url 请求的链接
	 * @param params 需要设置的参数map
	 * @param headerMap 需要设置头信息map
	 * @return 响应html/json内容
	 * @author LIu Mingyao
	 */
	public static CloseableHttpResponse headerAndParamsGet(String url, Map<String, String> params,
			Map<String, String> headerMap) {

		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		HttpGet get = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(url);

			// 设置get请求参数
			for (String key : params.keySet()) {
				uriBuilder.addParameter(key, params.get(key));
			}

			get = new HttpGet(uriBuilder.build());

			// 设置请求头信息
			for (String key : headerMap.keySet()) {
				get.addHeader(key, headerMap.get(key));
			}

			response = client.execute(get);

		} catch (Exception e1) {
			throw new MarsException("发送get请求评论数据异常" + e1);
		}
		return response;
	}

}
