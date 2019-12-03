package application.controller;

import application.music.kuwo.KuwoMusic;
import application.music.kuwo.playingView.PlayingPanel;
import application.music.pojo.kuwo.KuwoLiLabel;
import application.music.pojo.kuwo.KuwoPojo;
import application.screenshot.ScreenShot;
import application.translate.baidu.BaiDuTrans;
import application.utils.LyricShowUtil;
import application.utils.MarsException;
import application.utils.MarsLogUtil;
import application.utils.StringUtil;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.Mnemonic;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Setter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.*;

//@SuppressWarnings("all")
public class MyController implements Initializable {

    @FXML
    private Button myBtn;

    @FXML
    private TextArea inText;
    @FXML
    private TextArea outText;
    @FXML
    private Button playMusic;
    @FXML
    private TextField searchMusicText;
    @FXML
    private ListView<KuwoPojo> musicList;
    @FXML
    private Button screenBtn;
    @FXML
    private TextField lrcText;

    private static KuwoPojo nowMusic;
    private static KuwoPojo selectMusic;
    @Setter
    public Scene scene;
    @Setter
    public Stage mainStage;

    private KuwoMusic kuwoMusic = KuwoMusic.obj;

    /**
     * 作为成员变量，保证了暂停再次播放的时候是同一首歌
     */
    private static MediaPlayer player;

    private ObservableList<KuwoPojo> data;

    private static LyricShowUtil lyricShowUtil;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // 初始化音乐相关信息
        initMusicInfo();

        System.out.println("初始化controller,在加载该类对应的fxml问件时就会被调用");
    }

    /**
     * @Description 音乐相关组件初始化
     * @author LIu Mingyao
     */
    public void initMusicInfo() {
        lyricShowUtil = new LyricShowUtil();
        // 初始化listview的可观察列表(搜索音乐之后的展示列表)
        data = musicList.getItems();

        // 监听音乐选择列表
        musicList.getSelectionModel().selectedItemProperty()
                .addListener((ObservableValue<? extends KuwoPojo> observable, KuwoPojo oldValue,
                              KuwoPojo newValue) -> {
                    System.out.println(newValue + "   newValue");
                    selectMusic = newValue;
                });

    }

    /**
     * @param event
     * @Description 翻译
     * @author LIu Mingyao
     */
    public void trans(ActionEvent event) {
        String in = inText.getText();
        String result;
        try {
            result = BaiDuTrans.getTransResult(in, "auto", "auto");
        } catch (MarsException e) {
            result = "翻译出错";
            MarsLogUtil.debug(getClass(), "翻译出错", e);
        }
        outText.setText(result);

    }

    /**
     * @Description 根据searchMusicText内容搜歌
     * @author LIu Mingyao
     */
    public void search() {
        musicList.setCellFactory(null);

        String searchStr = searchMusicText.getText().replaceAll(" ", "+");// 酷我搜索会将空格替换为+
        // searchStr = "2";
        if (StringUtil.isEmpty(searchStr)) {
            MarsLogUtil.debug(getClass(), "*****************请先输入搜索内容*****************");
            return;
        }

        // 搜索之前先清空之前内容
        if (data != null) {
            data.clear();
        }

        MarsLogUtil.info(getClass(), "正在搜索歌曲.......");

        String musciListHTML = kuwoMusic.searchMusic(searchStr);
        List<KuwoLiLabel> labelList = kuwoMusic.parseLiLabelList(musciListHTML);
        if (labelList.size() == 0 && musciListHTML.contains("天翼飞")) {
            throw new MarsException("请先联网");
        }
        long a = System.currentTimeMillis();

        int playMusicNum = labelList.size() < 10 ? labelList.size() : 10;
        // System.out.println(playMusicNum);
        List<FutureTask<KuwoPojo>> s = new ArrayList<FutureTask<KuwoPojo>>(playMusicNum);
        // 防止并发修改异常（还没有return完所有的kuwopojo就开始获取get了）
        CountDownLatch cdl = new CountDownLatch(playMusicNum);

        ExecutorService service = Executors.newFixedThreadPool(playMusicNum);

        // 最多只展示10首歌,开多线程爬虫可以使时间缩短至一个爬虫的时间（100多）.酷我一页结果默认是25首歌
        for (int i = 0; i < playMusicNum; i++) {
            KuwoLiLabel label = labelList.get(i);

            FutureTask<KuwoPojo> futureTask = new FutureTask<KuwoPojo>(() -> {
                KuwoPojo kuwoPojo = kuwoMusic.parseMusicInfo1(label);

                synchronized (KuwoPojo.class) {// 确保countDownLatch能正确自减1
                    cdl.countDown();
                    return kuwoPojo;
                }
            });

            s.add(futureTask);
            service.submit(futureTask);
            // new Thread(futureTask, "获取第" + i + "首歌线程").start();
        }

        try {
            // cdl.await();
            cdl.await(5L, TimeUnit.SECONDS);
        } catch (InterruptedException e1) {
            MarsLogUtil.debug(getClass(), "cdl出现异常", e1);
        }

        for (int i = 0; i < playMusicNum && i < 10; i++) {
            KuwoPojo pojo = null;
            try {
                pojo = s.get(i).get();
            } catch (Exception e) {
                MarsLogUtil.info(getClass(), "  获取歌曲列表异常\tpojo:" + pojo, e);
            }

            if (pojo == null) {
                continue;
            }

            data.add(pojo);
        }
        s = null;
        service.shutdown();
        long b = System.currentTimeMillis();
        MarsLogUtil.info(getClass(), "本次搜索共耗时 b - a=" + (b - a));

        // Platform.runLater(() -> {
        //
        // });

        /**
         * 将Music对象的部分属性(name)取出来展示在ListView面板
         */
        musicList.setCellFactory(TextFieldListCell.forListView(new StringConverter<KuwoPojo>() {

            @Override
            public String toString(KuwoPojo music) {
                // TODO Auto-generated method stub
                return music != null ? music.getMname() : "";
            }

            @Override
            public KuwoPojo fromString(String string) {
                // TODO Auto-generated method stub
                return null;
            }
        }));
    }

    /**
     * @param event
     * @Description 播放音乐
     * @author LIu Mingyao
     */
    public void play(ActionEvent event) {

        if (selectMusic == null) {
            System.out.println("*****************请先选择音乐再播放*****************");
            return;
        }

        if (player != null && selectMusic != nowMusic) {// 切歌
            String statu = player.getStatus().toString();
            if (!statu.equals(MediaPlayer.Status.STOPPED.toString())) {
                lyricShowUtil.lyricThread.interrupt();
                player.stop();
            }
        }
        nowMusic = selectMusic;
        player = nowMusic.getPlayer();

        MarsLogUtil.info(getClass(), "====正在播放=======" + nowMusic.getMname());

        // 歌词同步
        lyricShowUtil.showLyricInfo(lrcText, nowMusic, player);

        // 打开专用播放面板
        PlayingPanel.obj.openPlayingState(mainStage, player, lyricShowUtil);

    }

    public void stop(ActionEvent event) {

    }

    public void pause(ActionEvent event) {

    }

    public void nextPageMusic(ActionEvent event) {
        System.out.println("====下一页======");

    }

    /**
     * @Description 主面板快捷键绑定, 在main方法中被调用
     * @author LIu Mingyao
     */
    public void shortcutKeys() {

        // 绑定截图快捷键
        KeyCombination screenKey = KeyCombination.valueOf("ctrl+alt+p");
        Mnemonic mc = new Mnemonic(screenBtn, screenKey);
        scene.addMnemonic(mc);

        // 翻译快捷键
        KeyCombination searchKey = KeyCombination.valueOf("ctrl+alt+i");
        Mnemonic search = new Mnemonic(myBtn, searchKey);
        scene.addMnemonic(search);
    }

    /**
     * @Description 截图按钮点击事件
     * @author LIu Mingyao
     */
    public void screenShot() {
        ScreenShot screenShot = ScreenShot.initScreenShot();
        screenShot.showScreenPanel(mainStage);
    }

}
