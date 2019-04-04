package application.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Character.UnicodeScript;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat.Encoding;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

public class Zhou {

	/**
	 * @Description 获取unicode编码
	 * @param c
	 * @return
	 * @author LIu Mingyao
	 */
	public static String getUnicode(char c) {
		String returnUniCode = null;
		returnUniCode = String.valueOf((int) c);
		return returnUniCode;

	}

	/**
	 * Ascii转换为字符串
	 * 
	 * @param value
	 * @return
	 */
	public static String asciiToString(String value) {
		StringBuffer sbu = new StringBuffer();
		String[] chars = value.split(",");
		for (int i = 0; i < chars.length; i++) {
			sbu.append((char) Integer.parseInt(chars[i]));
		}
		return sbu.toString();
	}

	public static void main(String[] args) throws Exception {
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		System.out.println(sdf.format(new Date(1553681389552l)));

		// TODO Auto-generated method stub
		String httpGet = httpGet("https://www.wjx.cn/jq/35713991.aspx");
		// System.out.println(httpGet);
		String rn = httpGet.split("var rndnum=")[1].split("var simple")[0].replaceAll("\"", "")
				.replaceAll(";", "").trim();
		String jqnonce = httpGet.split("var jqnonce=")[1].split("var isChuangGuan")[0]
				.replaceAll("\"", "").replaceAll(";", "").trim();

		

		/**
		 * 下面是js源码
		 * function dataenc(a) {
		 * var b = ktimes % 10;
		 * 0 == b && (b = 1);//==优先级大于&&，并且&&有短路功能，如果b=0，那么才将b赋值为1，否则b就为余数
		 * for (var d = [], c = 0; c < a.length; c++) {
		 * var f = a.charCodeAt(c) ^ b;//charCodeAt返回a的第c个字符的unicode编码
		 * d.push(String.fromCharCode(f))//unicode编码转换为字符串，再加入到d中
		 * }
		 * return d.join("")
		 * }
		 */
		int ktime = 365;
		// 模拟js算出jqsign
		StringBuffer g = new StringBuffer();
		String d[] = new String[]{};
		for (int c = 0; c < jqnonce.length(); c++) {
			int b = ktime % 10;
			if (b == 0) b = 1;

			char[] temp = jqnonce.toCharArray();
			String unicodeValue = getUnicode(temp[c]);

			int f = Integer.valueOf(unicodeValue.toString()).intValue() ^ b;

			if (c != 0) {
				g.append(",");
			}
			g.append(String.valueOf(f));
			String h = asciiToString(g.toString());

		}
		String jqsign = d.toString();
		System.out.println("rn   " + rn + "jqnonce  " + jqnonce + "  jqsign  " + jqsign);
		/**
		 * submittype=1 useget=1 t是距离1970毫秒数 starttime开始时间 经过uri编码 curID问卷35713991 进入调查界面的html中有
		 */

		long t = new Date().getTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String format = simpleDateFormat.format(new Date());
		String starttime = URLEncoder.encode(format, "utf-8");
		String url3 = "https://www.wjx.cn/joinnew/processjq.ashx?submittype=1&curID=35713991&t=" + t
				+ "&starttime=" + starttime + "&ktimes=590&rn=" + rn + "&hlv=1&jqnonce=" + jqnonce
				+ "&jqsign=" + jqsign;
		String url1 = "https://www.wjx.cn/joinnew/processjq.ashx?submittype=1&curID=35713991&t=1553662394040&starttime=2019%2F3%2F27%2012%3A28%3A54&ktimes=275&rn=1984807950.07244852&hlv=1&jqnonce="
				+ jqnonce + "&jqsign=1a7%3D%3D3%60%3C(a441(104d(g757(ag65c307f613";
		String url2 = "https://www.wjx.cn/joinnew/processjq.ashx?submittype=1&curID=35713991&t=" + t
				+ "&starttime=" + starttime + "&ktimes=284&rn=1984807950.52129701&hlv=1&jqnonce="
				+ jqnonce + "&jqsign=4bg7e0fg)f26%60)075a)e%6022)441%3D2a11%60bga";

		postParams(url3);

	}

	public static String httpGet(String url) {
		HttpGet get = new HttpGet(url);
		CloseableHttpClient client = HttpClients.createDefault();
		String string = "";
		CloseableHttpResponse response = null;
		try {
			response = client.execute(get);

			string = EntityUtils.toString(response.getEntity(), "utf-8");

			// for (org.apache.http.Header header : get.getAllHeaders()) {
			// System.out.println(header.getName() + " : " + header.getValue());
			// }
			//
			// for (org.apache.http.Header header : response.getAllHeaders()) {
			// System.out.println(header.getName() + " : " + header.getValue());
			// }

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
	 * uri带的参数：
	 * submittype: 1
	 * curID: 35713991
	 * t: 1553660868671
	 * starttime: 2019/3/27 12:26:40
	 * ktimes: 326
	 * rn: 1984807950.05620931
	 * hlv: 1
	 * jqnonce: b5850f41-4855-4891-89cf-12a922c02442
	 * jqsign: d3>36`27+2>33+2>?7+>?e`+74g?44e64224
	 * 表单带的参数：
	 * submitdata: 1$1}2$1}3$2}4$1}5$2}6$1!1,2!1,3!1,4!1,5!1,6!1}7$1!1,2!1,3!1,4!1,5!1,6!1,7!1,8!1,9!1,10!1,11!1,12!1,13!1,14!1,15!1,16!1,17!1,18!1,19!1,20!1,21!1,22!1,23!1,24!1,25!1}8$1!1,2!1,3!1,4!1
	 * 
	 * @Description TODO
	 * @author LIu Mingyao
	 */
	public static void postParams(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();

		String entityStr = null;
		CloseableHttpResponse response = null;

		try {

			HttpPost httpPost = new HttpPost(url);

			/*
			 * 添加请求参数
			 */
			// 创建请求参数
			List<NameValuePair> list = new LinkedList<>();
			BasicNameValuePair param1 = new BasicNameValuePair("submitdata",
					"1$2}2$2}3$2}4$2}5$2}6$1!2,2!2,3!2,4!2,5!2,6!2}7$1!2,2!2,3!2,4!2,5!2,6!2,7!2,8!2,9!2,10!2,11!2,12!2,13!2,14!2,15!2,16!2,17!2,18!2,19!2,20!2,21!2,22!2,23!2,24!2,25!2}8$1!2,2!2,3!2,4!2");
			list.add(param1);
			// 使用URL实体转换工具
			UrlEncodedFormEntity entityParam = new UrlEncodedFormEntity(list, "UTF-8");
			httpPost.setEntity(entityParam);

			/*
			 * 添加请求头信息
			 */
			httpPost.addHeader("User-Agent",
					"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.addHeader("Accept", "*/*");
			httpPost.addHeader("Host", "www.wjx.cn");
			httpPost.addHeader("Origin", "https://www.wjx.cn");
			httpPost.addHeader("Referer", "https://www.wjx.cn/jq/35713991.aspx");
			// httpPost.addHeader("Accept", "*/*");
			// httpPost.addHeader("Accept", "*/*");
			httpPost.addHeader("Cookie",
					"acw_tc=2f624a1a15536592063994925e186df411a367cc7bed8511496e205ac73513; .ASPXANONYMOUS=1C63ZuMa1QEkAAAAMjMxMGVlN2MtMzAzYi00YjliLTg0OTktNWVlZTQ2NDUxNjc2H788ZToQWY_P35ORlYzIY-lEteQ1; UM_distinctid=169bd4e1bca8e-032dfbfb51d761-7a1437-1fa400-169bd4e1bcba84; Hm_lvt_21be24c80829bd7a683b2c536fcf520b=1553659207; CNZZDATA4478442=cnzz_eid%3D1417264940-1553657846-%26ntime%3D1553663246; jac35713991=88275944; Hm_lpvt_21be24c80829bd7a683b2c536fcf520b=1553665762");

			response = httpClient.execute(httpPost);
			System.out.println("状态码为:" + response.getStatusLine().getStatusCode());
			for (org.apache.http.Header header : response.getAllHeaders()) {
				if (header.getName().toString().equals("Content-Length")) {
					System.out.println("哈哈哈哈哈哈哈哈哈哈哈哈哈");
				}
				System.out.println(header.getName() + " = " + header.getValue());
			}

			HttpEntity entity = response.getEntity();
			entityStr = EntityUtils.toString(entity, "UTF-8");

			// System.out.println(Arrays.toString(response.getAllHeaders()));

		} catch (ClientProtocolException e) {
			System.err.println("Http协议出现问题");
			e.printStackTrace();
		} catch (ParseException e) {
			System.err.println("解析错误");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IO异常");
			e.printStackTrace();
		} finally {
			// 释放连接
			if (null != response) {
				try {
					response.close();
					httpClient.close();
				} catch (IOException e) {
					System.err.println("释放连接出错");
					e.printStackTrace();
				}
			}
		}

		// 打印响应内容
		System.out.println(entityStr);
	}

}
