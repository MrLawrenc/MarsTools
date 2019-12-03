package application.async;

import application.music.kuwo.KuwoMusic;
import application.music.pojo.kuwo.KuwoLiLabel;
import application.music.pojo.kuwo.KuwoPojo;
import application.utils.MarsException;
import application.utils.MarsLogUtil;
import javafx.concurrent.Task;

import java.util.List;

/**
 * @author : LiuMingyao
 * @date : 2019/12/3 22:36
 * @description : 异步搜歌
 */
public class SearchMusicTask extends Task<KuwoPojo> {
    private KuwoMusic kuwoMusic = KuwoMusic.obj;
    private String searchStr;

    public SearchMusicTask(String searchStr) {
        this.searchStr = searchStr;
    }

    @Override
    protected KuwoPojo call() throws Exception {
        String musicListHtml = kuwoMusic.searchMusic(searchStr);
        List<KuwoLiLabel> labelList = kuwoMusic.parseLiLabelList(musicListHtml);
        if (labelList.size() == 0 && musicListHtml.contains("天翼飞")) {
            throw new MarsException("请先联网");
        }
        long l = System.currentTimeMillis();
        int playMusicNum = Math.min(labelList.size(), 10);

        // 最多只展示10首歌,开多线程爬虫可以使时间缩短至一个爬虫的时间（100多）.酷我一页结果默认是25首歌
        for (int i = 0; i < playMusicNum; i++) {
            KuwoLiLabel label = labelList.get(i);
            KuwoPojo kuwoPojo = kuwoMusic.parseMusicInfo1(label);
            updateValue(kuwoPojo);
        }
        MarsLogUtil.info(getClass(), "搜索耗时:" + (System.currentTimeMillis() - l));
        return null;
    }
}