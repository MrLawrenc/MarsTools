package application.test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import application.music.kuwo.KuwoMusic;
import application.music.pojo.kuwo.KuwoComment;
import application.music.pojo.kuwo.KuwoLiLabel;

public class Test {
	public static void main(String[] args) throws Exception {
		testHttp();
		while (true) {
			TimeUnit.MILLISECONDS.sleep(5000);
			System.out.println("sssssssssss");
		}
	}

	public static void testHttp() {
		KuwoMusic kuwoMusic = KuwoMusic.obj;
		//
		String html = kuwoMusic.searchMusic("童话镇");
		List<KuwoLiLabel> labelList = kuwoMusic.parseLiLabelList(html);
		
		
		System.out.println(kuwoMusic.parseMusicInfo1(labelList.get(0)));
//		KuwoComment kuwoComment = kuwoMusic.commentInfo(labelList.get(0), true);
//		System.out.println(kuwoMusic.getLyric(labelList.get(0)));
	}

}


