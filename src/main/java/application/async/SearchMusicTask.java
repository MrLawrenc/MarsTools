package application.async;

import application.music.online.kuwo.KuwoMusic;
import application.music.online.kuwo.pojo.kuwo.KuwoLiLabel;
import application.music.online.kuwo.pojo.kuwo.KuwoPojo;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.List;

/**
 * @author : LiuMingyao
 * 2019/12/3 22:36
 * 异步搜歌
 */
@Slf4j
public class SearchMusicTask extends Task<KuwoPojo> {
    private final KuwoMusic kuwoMusic = KuwoMusic.obj;
    private final String searchStr;

    public SearchMusicTask(String searchStr) {
        this.searchStr = searchStr;
    }

    @Override
    protected KuwoPojo call() throws Exception {
        InetAddress testNet = InetAddress.getByName("www.baidu.com");
        if (!testNet.isReachable(1000)) {
            throw new RuntimeException("无法联网");
        }

        String musicListHtml = kuwoMusic.searchMusic(searchStr);
        List<KuwoLiLabel> labelList = kuwoMusic.parseLiLabelList(musicListHtml);


        long l = System.currentTimeMillis();
        int playMusicNum = Math.min(labelList.size(), 10);

        // 最多只展示10首歌,开多线程爬虫可以使时间缩短至一个爬虫的时间（100多）.酷我一页结果默认是25首歌
        for (int i = 0; i < playMusicNum; i++) {
            KuwoLiLabel label = labelList.get(i);
            KuwoPojo kuwoPojo = kuwoMusic.parseMusicInfo1(label);
            updateValue(kuwoPojo);
        }
        log.info("搜索耗时:{}", (System.currentTimeMillis() - l));
        return null;
    }
}