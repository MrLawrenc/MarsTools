package application.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.FutureTask;

import application.music.kuwo.KuwoMusic;
import application.music.kuwo.playingView.PlayingPanel;
import application.music.pojo.kuwo.KuwoLiLabel;
import application.music.pojo.kuwo.KuwoPojo;
import application.screenshot.ScreenShot;
import application.translate.baidu.BaiDuTrans;
import application.utils.LyricShowUtil;
import application.utils.MarsException;
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
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Setter;
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
	private static MediaPlayer player;// 作为成员变量，保证了暂停再次播放的时候是同一首歌

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
	 * @Description 翻译
	 * @param event
	 * @author LIu Mingyao
	 */
	public void trans(ActionEvent event) {
		String in = inText.getText();
		String result;
		try {
			result = BaiDuTrans.getTransResult(in, "auto", "auto");
		} catch (MarsException e) {

			result = "翻译出错";
			e.printStackTrace();
		}
		outText.setText(result);

	}

	/**
	 * @Description 根据searchMusicText内容搜歌
	 * @author LIu Mingyao
	 */
	public void search() {
		musicList.setCellFactory(null);

		String searchStr = searchMusicText.getText();
		if (StringUtil.isEmpty(searchStr)) {
			System.out.println("*****************请先输入搜索内容*****************");
			return;
		}

		// 搜索之前先清空之前内容
		if (data != null) data.clear();

		System.out.println("正在搜索歌曲.......");

		String musciListHTML = kuwoMusic.searchMusic(searchStr);
		List<KuwoLiLabel> labelList = kuwoMusic.parseLiLabelList(musciListHTML);
		if (labelList.size() == 0 && musciListHTML.contains("天翼飞")) throw new MarsException("请先联网");
		long a = new Date().getTime();

		// 保证集合线程安全
		List<FutureTask<KuwoPojo>> s = new ArrayList<FutureTask<KuwoPojo>>();

		int musicNum = labelList.size();
		// 最多只展示10首歌,开多线程爬虫可以使时间缩短至一个爬虫的时间（100多）.酷我一页结果默认是25首歌
		for (int i = 0; i < musicNum && i < 10; i++) {
			KuwoLiLabel label = labelList.get(i);

			FutureTask<KuwoPojo> futureTask = new FutureTask<KuwoPojo>(() -> {
				return kuwoMusic.parseMusicInfo1(label);
			});

			/**
			 * 使用execute()方法，在使用下面使用futureTask.get()的时候偶尔会报并发修改异常的错。麻烦知道的同学解释下^.^<br>
			 * submit偶尔也会，原因未知
			 * 查了资料说的是submit方法可以返回future对象，用于有返回值得情况，而execute只接受runable接口，返回值为void,奇怪的是我是偶尔会报错（报null和并发修改的错）<br>
			 * 不使用线程池，调用futuretask的get方法也会偶尔报空指针的错误
			 * **********************现在下面调用get()仍然偶尔会报错,原因未知******************************
			 */
			new Thread(futureTask, "获取第" + i + "首歌线程").start();
			s.add(futureTask);
		}

		for (int i = 0; i < musicNum && i < 10; i++) {
			KuwoPojo pojo = null;
			try {
				pojo = s.get(i).get();
			} catch (Exception e) {
				System.out.println(e.getMessage() + "  获取歌曲列表异常\tpojo:" + pojo);
			}
			if (pojo == null) continue;

			data.add(pojo);
		}
		s = null;
		long b = new Date().getTime();
		System.out.println("本次搜索共耗时 b - a=" + (b - a));

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
	 * @Description 播放音乐
	 * @param event
	 * @author LIu Mingyao
	 */
	public void play(ActionEvent event) {

		if (selectMusic == null) {
			System.out.println("*****************请先选择音乐再播放*****************");
			return;
		}

		if (player != null && selectMusic != nowMusic) {// 切歌
			String statu = player.getStatus().toString();
			if (statu.equals(Status.STOPPED.toString())) {
				System.out.println("之前stop和interrupt过了");
			} else {
				lyricShowUtil.lyricThread.interrupt();
				player.stop();
			}
		}
		nowMusic = selectMusic;
		player = nowMusic.getPlayer();

		System.out.println("====正在播放=======" + nowMusic);

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
	 * @Description 主面板快捷键绑定,在main方法中被调用
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
