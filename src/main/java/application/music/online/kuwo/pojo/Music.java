package application.music.online.kuwo.pojo;

import javafx.scene.media.MediaPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 音乐信息
 * @author LIu Mingyao
 * @date 2019年3月26日下午8:33:19
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Music {
	
	public Music(String mid, String mname, String mp3Size, String auther_url, String artist_pic,
			String artist_pic240, String mp3PlayUrl, String aacPlayUrl, String wmaPlayUrl,
			MediaPlayer player) {
		super();
		this.mid = mid;
		this.mname = mname;
		this.mp3Size = mp3Size;
		this.auther_url = auther_url;
		this.artist_pic = artist_pic;
		this.artist_pic240 = artist_pic240;
		this.mp3PlayUrl = mp3PlayUrl;
		this.aacPlayUrl = aacPlayUrl;
		this.wmaPlayUrl = wmaPlayUrl;
		this.player = player;
	}
	private String mid;
	private String mname;
	private String mp3Size;
	private String auther_url;// 歌手详情页
	private String artist_pic;// 演唱者图片
	private String artist_pic240;// 备用图片

	// 三种不同格式的播放地址(即下载地址)
	private String mp3PlayUrl;
	private String aacPlayUrl;
	private String wmaPlayUrl;

	private Comment comment;// 评论
	private MediaPlayer player;// 播放器对象

}
