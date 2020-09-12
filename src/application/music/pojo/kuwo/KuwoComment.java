package application.music.pojo.kuwo;

import java.util.List;

import application.music.pojo.Comment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Description 音乐评论实体类,包含了评论对象（这是该音乐所有评论集合）
 * @author LIu Mingyao
 * @date 2019年3月26日下午4:23:14
 */
@Setter
@Getter@ToString
public class KuwoComment {
	private String mId;// 音乐id

	private String result;// 请求评论信息返回的状态码 ok--》成功
	private String totalPage;// 总页数
	private String pageSize;// 每页评论数
	private String currentPage;// 当前评论所在页数
	private List<Comment> rows;
	private String comment_tpye;// 评论类型 hot是热评
	private String total;// 热评总数
	private String t_total;// 音乐总评论数

}
