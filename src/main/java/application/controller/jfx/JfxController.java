package application.controller.jfx;

import application.async.SearchMusicTask;
import application.music.kuwo.KuwoMusic;
import application.music.kuwo.playingView.PlayingPanel;
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
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * @author MrLawrenc
 * date  2020/6/30 23:23
 * <p>
 * jfx控制器
 */
@SuppressWarnings("all")
@Slf4j
public class JfxController implements Initializable {

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

        log.info("init jfx controller ");
    }

    /**
     * 音乐相关组件初始化
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
     * 截图按钮点击事件
     */
    public void screenShot() {
        ScreenShot screenShot = ScreenShot.initScreenShot();
        screenShot.showScreenPanel(mainStage);
    }

    /**
     * 翻译事件
     */
    public void trans(ActionEvent event) {
        String in = inText.getText();
        String result;
        try {
            result = BaiDuTrans.getTransResult(in);
        } catch (MarsException e) {
            result = "Translation error,Please retry!";
            log.error(result, e);
        }
        outText.setText(result);

    }

    /**
     * 根据searchMusicText内容搜歌
     */
    public void search() {
        musicList.setCellFactory(null);
        // 酷我搜索会将空格替换为+
        String searchStr = searchMusicText.getText().replaceAll(" ", "+");
        if (StringUtil.isEmpty(searchStr)) {
            log.debug("*****************请先输入搜索内容*****************");
            searchMusicText.setText("请先输入搜索内容");
            return;
        }

        // 搜索之前先清空之前内容
        if (data != null) {
            data.clear();
        }

        MarsLogUtil.info(getClass(), "正在搜索歌曲.......");
        SearchMusicTask searchMusicTask = new SearchMusicTask(searchStr);

        CompletableFuture.runAsync(searchMusicTask);
        searchMusicTask.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue == null) {
                MarsLogUtil.info(getClass(), "搜索歌曲完毕.......");
            } else {
                data.add(newValue);
            }
        });
        /*
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
     * 播放音乐
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
     * 绑定所有设定的快捷键
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

}
