package application.utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import application.music.kuwo.KuwoMusic;
import application.music.pojo.kuwo.KuwoLyric;
import application.music.pojo.kuwo.KuwoPojo;
import javafx.scene.control.TextField;
import javafx.scene.media.MediaPlayer;

/**
 * @Description 处理歌词工具类
 * @author LIu Mingyao
 * @date 2019年4月3日下午3:22:18
 */
public class LyricShowUtil {

	// 必须设置为volatile才能改变线程状态
	public volatile boolean isStop = false;

	// enum PlayStatus {}

	public void readyLyric(TextField lrcText, KuwoPojo nowMusic, MediaPlayer player) {
		// 得到每段歌词组成的列表
		List<KuwoLyric> lyric = KuwoMusic.obj.getLyric(nowMusic.getLabel());
		System.out.println("歌词列表：" + lyric);
		lrcText.setText("**********wait**********");
		for (KuwoLyric kuwoLyric : lyric) {
			String time = kuwoLyric.getTime();

			while (true) {
				if (this.isStop) {
					System.out.println("**********结束歌词显示线程**********");
					return;
				} ;

				Double temp=Double.valueOf(time).doubleValue()*1000- player.getCurrentTime().toMillis();//单位是毫秒ms
				// 设置歌词显示精度
				if ( temp< 0.1) {
					System.out.println("======显示歌词" + Double.valueOf(time).doubleValue() + " "
							+ player.getCurrentTime().toSeconds());
					System.out
							.println(lrcText + "  " + kuwoLyric + "  " + kuwoLyric.getLineLyric());
					lrcText.setText(kuwoLyric.getLineLyric());
					break;
				}else {
					try {
						TimeUnit.MILLISECONDS.sleep(temp.longValue());
					} catch (InterruptedException e) {
						System.out.println("歌词显示线程readyLyric出错");
						e.printStackTrace();
					}
				}
			}
		}

	}

	public void showLyricInfo(TextField lrcText, KuwoPojo nowMusic, MediaPlayer player) {

		// 之前可能中断过线程，因此每次调用需要重新设值
		this.isStop = false;
		new Thread(() -> {
			readyLyric(lrcText, nowMusic, player);
		}, "歌词展示线程").start();
	}
}
