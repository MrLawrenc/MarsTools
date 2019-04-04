package application.test;

import java.util.List;

import application.music.kuwo.KuwoMusic;
import application.music.pojo.kuwo.KuwoComment;
import application.music.pojo.kuwo.KuwoLiLabel;

public class Test {
	public static void main(String[] args) throws Exception {
		testHttp();
	}

	public static void testHttp() {
		KuwoMusic kuwoMusic = KuwoMusic.obj;
		//
		String html = kuwoMusic.searchMusic("九张机");
		List<KuwoLiLabel> labelList = kuwoMusic.parseLiLabelList(html);
//		KuwoComment kuwoComment = kuwoMusic.commentInfo(labelList.get(0), true);
		System.out.println(kuwoMusic.getLyric(labelList.get(0)));
	}

}
