package application.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import application.music.kuwo.KuwoMusic;
import application.music.pojo.kuwo.KuwoLiLabel;
import application.music.pojo.kuwo.KuwoPojo;
import application.screenshot.ScreenShot;
import application.translate.baidu.BaiDuTrans;
import application.utils.LyricShowUtil;
import application.utils.MarsException;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Setter;

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

	private KuwoPojo nowMusic;
	private KuwoPojo selectMusic;
	@Setter
	public Scene scene;
	@Setter
	public Stage mainStage;

	private KuwoMusic kuwoMusic = KuwoMusic.obj;
	private MediaPlayer player;// 作为成员变量，保证了暂停再次播放的时候是同一首歌
	private ObservableList<KuwoPojo> data;

	private LyricShowUtil lyricShowUtil;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		// 初始化listview的可观察列表
		data = musicList.getItems();

		// 监听音乐选择列表
		musicList.getSelectionModel().selectedItemProperty()
				.addListener((ObservableValue<? extends KuwoPojo> observable, KuwoPojo oldValue,
						KuwoPojo newValue) -> {
					System.out.println(newValue + "   newValue");
					selectMusic = newValue;
				});

		System.out.println("初始化controller,在加载该类对应的fxml问件时就会被调用");
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
		// 搜索之前先清空之前内容
		if (data != null) data.clear();

		System.out.println("正在搜索歌曲.......");
		String searchStr = searchMusicText.getText();
		String musciListHTML = kuwoMusic.searchMusic(searchStr);
		List<KuwoLiLabel> labelList = kuwoMusic.parseLiLabelList(musciListHTML);

		Long a = new Date().getTime();
		List<FutureTask<KuwoPojo>> s = new ArrayList<FutureTask<KuwoPojo>>();
		// 最多只展示10首歌,开多线程爬虫可以使时间缩短至一个爬虫的时间（100多）
		for (int i = 0; i < labelList.size() && i < 10; i++) {
			KuwoLiLabel label = labelList.get(i);

			FutureTask<KuwoPojo> futureTask = new FutureTask<KuwoPojo>(() -> {
				KuwoPojo music = kuwoMusic.parseMusicInfo1(label);
				return music;
			});
			s.add(futureTask);
			new Thread(futureTask, "音乐搜索多线程" + i).start();
		}
		for (int i = 0; i < 10; i++) {
			try {
				data.add(s.get(i).get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		s = null;
		Long b = new Date().getTime();
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
				return music.getMname();
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
		
		
		
		
		if (player!=null&&selectMusic == nowMusic) {// 暂停/停止  再播放同一首歌的时候
			if (player.getStatus().toString().equals("STOPPED")) {//停止再播放  同一首歌
				
				lyricShowUtil.showLyricInfo(lrcText, nowMusic, player);
			}else {//暂停-->播放
				System.out.println("暂停---》播放");
			}
		} 
		
		if (player == null||selectMusic!=nowMusic) {// 第一次播放的时候  or 切歌，播放当前选中的歌曲
			if (player==null) {
				lyricShowUtil = new LyricShowUtil();
			}else {
				lyricShowUtil.isStop=true;
				player.stop();//说明是切歌，需要先停止之前播放的歌曲
			}
			// 获取当前选中的label
			nowMusic = musicList.getSelectionModel().getSelectedItem();
			Media media = new Media(nowMusic.getMp3PlayUrl());
			player = new MediaPlayer(media);
			System.out.println("====正在播放=======" + nowMusic);
			
			lyricShowUtil.showLyricInfo(lrcText, nowMusic, player);
		}
		
	
		
		
		// player.setVolume(0.1);0.0-1.0
		// System.out.println("音量是：" + player.getVolume());

		player.play();

		// lyricShowUtil.showLyricInfo(lrcText, nowMusic, player);
		// 测试方法作用
		// Timer timer = new Timer();
		// timer.schedule(new TimerTask() {
		// @Override
		// public void run() {
		// System.out.println("=====================================");
		// // System.out.println("结束时间是：" + player.getStopTime().toSeconds());
		// System.out.println("结束时间是：" + player.getStopTime().toMillis());
		// System.out.println("当前时间是：" + player.getCurrentTime());
		// System.out.println("当前播放速度是：" + player.getRate());
		// System.out.println("当前光谱更新间隔是：" + player.getAudioSpectrumInterval());
		// System.out.println("最大波段数是：" + player.getAudioEqualizer().MAX_NUM_BANDS);
		// System.out.println("当前状态是：" + player.getStatus());
		// /**
		// * 音频频谱更新的侦听器。注册侦听器后，启用音频频谱计算；<br>
		// * 删除侦听器后，禁用计算。只能注册一个侦听器，因此如果需要多个观察器，则必须转发事件
		// */
		// System.out.println("频谱监听器是1：" + player.audioSpectrumListenerProperty().getName());
		// System.out.println("频谱监听器是：" + player.audioSpectrumListenerProperty());
		// }
		// }, new Date(), 5000);

	}
	public void stop(ActionEvent event) {
		lyricShowUtil.isStop=true;
		player.stop();
		
		System.out.println("====已经停止=======" + player.getStatus());

	}

	public void pause(ActionEvent event) {
		
		player.pause();
		System.out.println("====已经暂停======" + player.getStatus());

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
