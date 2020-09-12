package application.music.pojo.kuwo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 在搜索之后,返回的结果列表中的音乐所在的li标签包含的信息
 * @author LIu Mingyao
 * @date 2019年3月26日下午2:18:54
 */
@Setter
@Getter
@AllArgsConstructor@ToString
public class KuwoLiLabel {

	private String mId;// 音乐id
	private String mLocal;// 音乐在当前页面的位置，即第几首歌
	private String newPlayUrl;// 新的在线播放地址
	private String newMusicName;// 新的在线播放地址对应的歌曲名称
	private String albumUrl;// 专辑地址
	private String albumName;// 所在专辑
	private String singerInfoUrl;// 歌手信息列表
	private String singerName;// 歌手名字
	private String oldPlayUrl;// 老的在线播放地址,请求会重定向到新的地址 状态码是302
	private String oldTitle;// 老的在线播放地址所对应的的title
	private String mvUrl;// mv在线播放地址
	private String mvName;
//	private String downloadUrl;// 歌曲下载地址

}
