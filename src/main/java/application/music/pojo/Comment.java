package application.music.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 评论(一条)
 * @author LIu Mingyao
 * @date 2019年3月26日下午4:31:19
 */
@Setter
@Getter@ToString
public class Comment {
	private String id;
	private String u_pic;
	private String u_headframe;
	private String u_name;// 评论人
	private String time;// 发表时间
	private String u_hangerid;
	private String like_num;// 点赞数
	private String msg;// 评论内容
	private String u_id;

	private Comment reply;// 回复信息

	private String mId;// 音乐id

}
