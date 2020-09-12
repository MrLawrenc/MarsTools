package application.music.online.kuwo.pojo.kuwo;

import application.music.online.kuwo.pojo.Music;
import javafx.scene.media.MediaPlayer;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description 酷我音乐实体类
 * @author LIu Mingyao
 * @date 2019年4月3日下午1:46:29
 */
public class KuwoPojo extends Music {

	@Setter
	@Getter
	private KuwoLiLabel label;// 包含label主要是用于请求播放页面1.根据响应的html里的lrclist来获取歌词信息 2.根据响应头的信息得到cookie的gid，将gid作为参数请求获取评论信息



	public KuwoPojo(String mid, String mname, String mp3Size, String auther_url, String artist_pic,
			String artist_pic240, String mp3PlayUrl, String aacPlayUrl, String wmaPlayUrl,
			 MediaPlayer player,KuwoLiLabel label) {
		super(mid, mname, mp3Size, auther_url, artist_pic, artist_pic240, mp3PlayUrl, aacPlayUrl,
				wmaPlayUrl,  player);
		this.label=label;
	}



	@Override
	public String toString() {
		return "KuwoPojo [label=" + label.hashCode() + ", getMid()=" + getMid() + ", getMname()=" + getMname()
				+ ", getMp3Size()=" + getMp3Size() + ", getAuther_url()=" + getAuther_url()
				+ ", getArtist_pic()=" + getArtist_pic() + ", getArtist_pic240()="
				+ getArtist_pic240() + ", getMp3PlayUrl()=" + getMp3PlayUrl() + ", getAacPlayUrl()="
				+ getAacPlayUrl() + ", getWmaPlayUrl()=" + getWmaPlayUrl() + ", getComment()="
				+ getComment() + ", getPlayer()=" + getPlayer() + ", toString()=" + super.toString()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}

}
