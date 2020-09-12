package application.music.online.kuwo;

import application.music.online.kuwo.pojo.Music;
import application.music.online.kuwo.pojo.kuwo.KuwoComment;
import application.music.online.kuwo.pojo.kuwo.KuwoLiLabel;
import application.music.online.kuwo.pojo.kuwo.KuwoLyric;
import application.music.online.kuwo.pojo.kuwo.KuwoPojo;
import application.utils.HttpUtil;
import application.utils.MarsException;
import application.utils.MarsLogUtil;
import application.utils.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KuwoMusic {
    public final static KuwoMusic obj = new KuwoMusic();

    private KuwoMusic() {
    }

    public void getAll() throws Exception {
        System.out.println(URLEncoder.encode("九张机", "utf-8"));// 第二个参数指定的是第一个参数的编码方式

    }

    /**
     * 根据传入的字符串搜索音乐 type种类： all综合 music音乐 artist歌手 album专辑 mv MV playlist歌单 yric歌词 。当前默认是搜歌曲
     * str 需要搜索的字符串
     * 返回结果的html 建议使用parseLiLabelList解析
     * Exception
     */
    public String searchMusic(String str) {
        String url = "http://sou.kuwo.cn/ws/NSearch?type=music&key=" + str;
        return HttpUtil.httpGet(url);
    }

    // <p class=down><a href=javascript:void(0); title=九张机下载 onclick=showDownMusic2014('MUSIC_26319191')

    /**
     * 根据歌曲列表html解析出当前所有歌曲li标签的内容,并存入liLabels集合中
     *
     * @param html 搜歌返回的html页面
     *             包含了当前页的所有歌曲信息
     */
    public List<KuwoLiLabel> parseLiLabelList(String html) {

        Document document = Jsoup.parse(html);
        Elements elements = document.select("li.clearfix");
        List<KuwoLiLabel> liLabels = new ArrayList<KuwoLiLabel>();
        for (Element element : elements) {
            // 歌曲位置，在当前页是第几首歌
            String mLocal = element.select("p").first().text();

            // 歌曲id
            String mId = element.select("input").first().attr("mid");

            // 新的播放地址
            Element newPlayA = element.select("a").first();
            String newPlayUrl = newPlayA.attr("href");
            String newMusicName = newPlayA.attr("title");

            // 专辑相关 可能只有p标签没有a标签,如：<p class=a_name></p>。在这儿不用判断albumA为空是因为底层select没有找到a也会new一个Elements对象返回
            Elements albumA = element.select("p.a_name a");
            String albumUrl = albumA.attr("href");// 如果没有是空串,不是null
            String albumName = albumA.attr("title");

            // 歌手信息
            Elements singerA = element.select("p.s_name a");
            String singerInfoUrl = singerA.attr("href");
            String singerName = singerA.attr("title");

            // 旧的播放地址
            Elements oldPlayA = element.select("p.listen a");
            String oldPlayUrl = oldPlayA.attr("href");
            String oldTitle = oldPlayA.attr("title");

            // mv信息,可能没有,不判断同上面的专辑
            Elements mvA = element.select("p.video a");
            String mvUrl = mvA.attr("href");
            String mvName = mvA.attr("title");

            liLabels.add(new KuwoLiLabel(mId, mLocal, newPlayUrl, newMusicName, albumUrl, albumName,
                    singerInfoUrl, singerName, oldPlayUrl, oldTitle, mvUrl, mvName));

            // System.out.println("这是第" + mLocal + "首歌曲 歌曲id是 " + mId + " 在线播放链接是(新)：" + newPlayUrl + " (旧)："
            // + oldPlayUrl + " 歌曲名字是：" + newMusicName + " 所在专辑地址是：" + albumUrl + " 所在专辑名字是：" + albumName
            // + " 歌手名字是：" + singerName + " 歌手详情页地址是：" + singerInfoUrl + " mv地址是：" + mvUrl + " mv名称是：" + mvName);
        }
        return liLabels;

    }

    // * 评论请求链接1： http://comment.kuwo.cn/com.s?type=get_comment&uid=0&prod=newWeb&digest=15&sid=26319191&page=1&rows=20&f=web&gid=0e63cabb-8732-44cd-b2ab-d68ba12418c0&jpcallback=getCommentListFn&_=1553654192084
    // * 评论请求链接2： http://comment.kuwo.cn/com.s?type=get_rec_comment&uid=0&prod=newWeb&digest=15&sid=26319191&page=1&rows=10&f=web&gid=0e63cabb-8732-44cd-b2ab-d68ba12418c0&jpcallback=getRecCommentListFn&_=1553589501601

    /**
     * 根据歌曲id获取评论信息
     *
     * @param label 搜索歌曲结果html的label标签,包含了歌曲的各种信息
     * @param isHot 是否是获取热评，为false就直接获取所有评论，默认不是热评
     *              音乐评论对象
     */

    public KuwoComment commentInfo(KuwoLiLabel label, Boolean isHot) {
        isHot = ((isHot == null || isHot == false) ? false : true);

        // 所有评论，按时间排序，默认每页默认20条
        String url1 = "http://comment.kuwo.cn/com.s?type=get_comment";
        // 热评，按点赞数排序，默认每页默认10条
        String url2 = "http://comment.kuwo.cn/com.s?type=get_rec_comment";
        Map<String, String> params = new HashMap<String, String>();

        String url = null;
        if (isHot) {
            url = url2;
            params.put("rows", "10");
        } else {
            url = url1;
            params.put("rows", "20");
        }

        CloseableHttpResponse playHtmlresponse = getPlayHtmlResponse(label);
        Header[] playHtmlHeaders = playHtmlresponse.getHeaders("Set-Cookie");
        String gid = "";
        String uid = "0";
        for (Header header : playHtmlHeaders) {
            if (header.getValue().contains("gid"))
                gid = header.getValue().split(";")[0].split("=")[1];
            if (header.getValue().contains("uid"))
                uid = header.getValue().split(";")[0].split("=")[1];
        }

        params.put("type", "get_comment");
        params.put("uid", uid);
        params.put("prod", "newWeb");
        params.put("digest", "15");
        params.put("sid", label.getMId());
        params.put("page", "1");
        params.put("f", "web");
        params.put("gid", gid);
        params.put("jpcallback", "getCommentListFn");
        params.put("_", String.valueOf(new Date().getTime()));

        CloseableHttpResponse commentResponse = HttpUtil.headerAndParamsGet(url, params,
                new HashMap<String, String>());

        String commentJson = "";
        KuwoComment comment = null;
        try {
            commentJson = EntityUtils.toString(commentResponse.getEntity(), "utf-8");
            Pattern pattern = Pattern.compile("jsondata=.*?;");
            Matcher matcher = pattern.matcher(commentJson);

            if (matcher.find()) {
                comment = JSONObject.parseObject(
                        matcher.group().replace(";", "").replace("jsondata=", ""),
                        KuwoComment.class);
                comment.setMId(label.getMId());
            }
        } catch (Exception e) {
            MarsLogUtil.error(getClass(), "解析评论失败", e);
        }
        return comment;
    }

    /**
     * 通过"http://www.kuwo.cn/webmusic/st/getMuiseByRid?rid=MUSIC_" + mid 链接获取音乐对象，包含了音乐播放源等信息
     *
     * @param mid 音乐id
     *            填充后的音乐对象
     */
    public KuwoPojo parseMusicInfo1(KuwoLiLabel label) {

        String mid = label.getMId();
        String url = "http://www.kuwo.cn/webmusic/st/getMuiseByRid?rid=MUSIC_" + mid;
        String result = HttpUtil.httpGet(url);

        Document document = Jsoup.parse(result);
        String mp3size = document.select("mp3size").text();
        String auther_url = document.select("auther_url").text();
        String artist_pic240 = document.select("artist_pic240").text();
        String artist_pic = document.select("artist_pic").text();

        String mp3path = document.select("mp3path").text();
        String aacpath = document.select("aacpath").text();
        String path = document.select("path").text();

        String mp3dl = document.select("mp3dl").text();
        String aacdl = document.select("aacdl").text();
        String wmadl = (document.select("wmadl").text());// 为空就使用其他两种格式的前缀
        wmadl = wmadl.isEmpty() ? mp3dl : wmadl;

        String mp3URI = (StringUtil.isEmpty(mp3dl) ? null : "http://" + mp3dl + mp3path);
        String aacURI = (StringUtil.isEmpty(aacdl) ? null : "http://" + aacdl + aacpath);
        String wmaURI = (StringUtil.isEmpty(wmadl) ? null : "http://" + wmadl + path);

        // 防止部分歌曲没有uri地址
        String playUri = (mp3URI != null
                ? mp3URI
                : (aacURI != null ? aacURI : (wmaURI != null ? wmaURI : "")));

        if (playUri.equals("")) {
            MarsLogUtil.info(getClass(), "获取" + label.getMId() + "歌曲播放地址失败!");
            return null;
        } else {
            // MarsLogUtil.info(getClass(), "Media playURI:"+playUri);
            Media media = new Media(playUri);
            MediaPlayer player = new MediaPlayer(media);
            return new KuwoPojo(mid, label.getNewMusicName(), mp3size, auther_url, artist_pic,
                    artist_pic240, mp3URI, aacURI, wmaURI, player, label);
        }
    }

    /**
     * 方法暂时不可用，请用parseMusicInfo1
     *
     * @param
     */
    @Deprecated
    public Music parseMusicInfo2(String mid) {

        // String url = "http://antiserver.kuwo.cn/anti.s";
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String> params = new HashMap<String, String>();

        params.put("format", "aac|mp3");
        params.put("rid", "MUSIC_" + mid);
        params.put("type", "convert_url");
        params.put("response", "res");

        // 使用谷歌有跨越问题，请求头看不完整因此使用Edge浏览器
        // map.put("Accept", "*/*");
        // map.put("Accept-Encoding", "gzip, deflate");
        // map.put("Accept-Language", "en-US, en; q=0.8, zh-Hans-CN; q=0.5, zh-Hans; q=0.3");
        // map.put("Cache-Control", "no-cache");
        // map.put("Connection", "Keep-Alive");
        // map.put("Cookie",
        // "Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1553566816,1553608447; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1553608451");
        //
        // // Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1553566816,1553608447; gid=824a76c4-8842-467b-ae93-93b27e401783; gtoken=7bkbcwH3pUh3; reqid=628adb58X56c4X4367X8886Xa215f1d7bcfd; JSESSIONID=1e9ktr1fttvsa1gp9lae44llcw
        // map.put("GetContentFeatures.DLNA.ORG", "1");
        // map.put("Host", "antiserver.kuwo.cn");
        // map.put("Range", "bytes=0-");
        //
        // map.put("Accept-Encoding", "identity;q=1, *;q=0");
        // map.put("chrome-proxy", "frfr");
        // map.put("Referer", "http://www.kuwo.cn/yinyue/" + mid);
        //
        // // map.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");
        // map.put("",
        // "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763");

        map.put("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("Accept-Language", "zh-CN,zh;q=0.9");
        map.put("Connection", "keep-alive");
        map.put("Host", "antiserver.kuwo.cn");
        map.put("Range", "bytes=0-");
        map.put("Upgrade-Insecure-Requests", "1");
        map.put("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.86 Safari/537.36");

        // map.put("Referer", "http://www.kuwo.cn/yinyue/" + mid);

        map.put("Cookie",
                "Hm_lvt_cdb524f42f0ce19b169a8071123a4797=1553566260,1553599010,1553611781,1553649796; Hm_lpvt_cdb524f42f0ce19b169a8071123a4797=1553650903");

        return null;

    }

    /**
     * 请求播放页面(http://www.kuwo.cn/yinyue/26319191) 获取响应对象的cookie信息 为获取评论所需要的参数gid做准备(这种只返回响应对象的只包含头信息，不包含响应文本)
     *
     * @param label 搜歌结果显示的html列表中的label
     */
    public CloseableHttpResponse getPlayHtmlResponse(KuwoLiLabel label) {

        String url = null;

        if (StringUtil.isEmpty(label.getNewPlayUrl())) {
            if (StringUtil.isEmpty(label.getOldPlayUrl())) {
                throw new MarsException("获取不到播放页面的url地址!");
            }
            url = label.getOldPlayUrl();
        } else {
            url = label.getNewPlayUrl();
        }
        System.out.println(url);
        return HttpUtil.httpGet4Response(url);
    }

    /**
     * 根据歌曲label获取歌词信息
     *
     * @param label 歌词信息数组，每个时间段对应的歌词 如：{"time":"3.86","lineLyric":"词：张富贵"}这种数组
     */
    public List<KuwoLyric> getLyric(KuwoLiLabel label) {
        String url = null;

        if (StringUtil.isEmpty(label.getNewPlayUrl())) {
            if (StringUtil.isEmpty(label.getOldPlayUrl())) {
                throw new MarsException("获取不到播放页面的url地址!");
            }
            url = label.getOldPlayUrl();
        } else {
            url = label.getNewPlayUrl();
        }

        HttpGet get = new HttpGet(url);
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = client.execute(get);
            String lrcHtml = EntityUtils.toString(response.getEntity(), "utf-8");
            Elements elements = Jsoup.parse(lrcHtml).select("head script");
            Pattern pattern = Pattern.compile("\\[.*?\\]");// [在正则中是特殊字符需要\转义，.*表示匹配多个字符 ?表示不贪婪，匹配遇到的第一个
            Matcher matcher = pattern.matcher(elements.html());
            if (matcher.find()) {
                String jsonLrc = matcher.group();
                return JSON.parseArray(jsonLrc, KuwoLyric.class);
            }

        } catch (Exception e) {
            System.out.println("获取歌词信息出错  对应的歌曲是：" + label.getNewMusicName());
            e.printStackTrace();
        }

        return null;
    }

}
