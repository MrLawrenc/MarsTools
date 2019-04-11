package application.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import application.music.kuwo.KuwoMusic;
import application.music.pojo.kuwo.KuwoLiLabel;
import application.music.pojo.kuwo.KuwoPojo;
import application.utils.MarsException;
import javafx.concurrent.Task;
import lombok.ToString;

public class TestFutureTask {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		List<FutureTask<A>> s = new ArrayList<FutureTask<A>>();
		List<Integer> ages = new ArrayList<Integer>();
		for (int i = 0; i < 10; i++) {
			ages.add(i);
		}
		long start = new Date().getTime();
		for (int i = 0; i < 10; i++) {
			Integer age = ages.get(i);
			FutureTask<A> futureTask = new FutureTask<A>(() -> {
				A a = new A("网", age);
				return a;
			});
			new Thread(futureTask).start();
			s.add(futureTask);
		}

		for (int i = 0; i < 10; i++) {
			FutureTask<A> futureTask = s.get(i);
			System.out.println(s.get(i).get());
		}
		System.out.println(System.currentTimeMillis() - start);
		search();
	}

	public static void search() {


		// 搜索之前先清空之前内容

		System.out.println("正在搜索歌曲.......");

		String musciListHTML = KuwoMusic.obj.searchMusic("2");
		List<KuwoLiLabel> labelList = KuwoMusic.obj.parseLiLabelList(musciListHTML);
		if (labelList.size() == 0 && musciListHTML.contains("天翼飞")) throw new MarsException("请先联网");
		long a = new Date().getTime();

		// 保证集合线程安全
		List<FutureTask<KuwoPojo>> s = new ArrayList<FutureTask<KuwoPojo>>();

		int musicNum = labelList.size();
		ExecutorService service = Executors.newFixedThreadPool(10);
		// 最多只展示10首歌,开多线程爬虫可以使时间缩短至一个爬虫的时间（100多）.酷我一页结果默认是25首歌
		for (int i = 0; i < musicNum && i < 10; i++) {
			KuwoLiLabel label = labelList.get(i);
			
//			Task<KuwoPojo> task=new Task<KuwoPojo>() {
//				@Override
//				protected KuwoPojo call() throws Exception {
//					return KuwoMusic.obj.parseMusicInfo1(label);
//				}};
				
				

			FutureTask<KuwoPojo> task = new FutureTask<KuwoPojo>(() -> {
				return KuwoMusic.obj.parseMusicInfo1(label);
			});

			/**
			 * 使用execute()方法，在使用下面使用futureTask.get()的时候偶尔会报并发修改异常的错。麻烦知道的同学解释下^.^<br>
			 * submit偶尔也会，原因未知
			 * 查了资料说的是submit方法可以返回future对象，用于有返回值得情况，而execute只接受runable接口，返回值为void,奇怪的是我是偶尔会报错（报null和并发修改的错）<br>
			 * 不使用线程池，调用futuretask的get方法也会偶尔报空指针的错误
			 */
			service.submit(task);
			s.add(task);
		}

		List<KuwoPojo> C=new ArrayList<KuwoPojo>();
		for (int i = 0; i < musicNum && i < 10; i++) {
			KuwoPojo pojo =null;
			try {
				System.out.println(s.get(i));
				 pojo = s.get(i).get();
				System.out.println(pojo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			C.add(pojo);
		}
		System.out.println("\\\\\\\\\\\\"+C.size());//好像出现异常也不影响使用
		service.shutdown();
		s = null;
		long b = new Date().getTime();
		System.out.println("本次搜索共耗时 b - a=" + (b - a));
	}

}
//[KuwoPojo [label=KuwoLiLabel(mId=38841452, mLocal=1, newPlayUrl=http://www.kuwo.cn/yinyue/38841452/, newMusicName=2, albumUrl=http://www.kuwo.cn/album/5073387/, albumName=电子琴联奏 好曲音乐精选（一）, singerInfoUrl=http://www.kuwo.cn/mingxing/%E7%BA%AF%E9%9F%B3%E4%B9%90/, singerName=纯音乐, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_38841452, oldTitle=2试听, mvUrl=, mvName=), getMid()=38841452, getMname()=2, getMp3Size()=23.42 MB, getAuther_url()=http://www.kuwo.cn/mingxing/%E7%BA%AF%E9%9F%B3%E4%B9%90/, getArtist_pic()=http://img1.kuwo.cn/star/starheads/120/2/d216967dcc21e3acc8bb878c9ed1b7d_0.jpg, getArtist_pic240()=http://img1.kuwo.cn/star/starheads/120/10/74/3851039176.jpg, getMp3PlayUrl()=http://nn01.sycdn.kuwo.cn/d4cd5bdae8f318bdaf2d0ad147f3d324/5cada043/resource/n2/82/13/4037236395.mp3, getAacPlayUrl()=http://nn03.sycdn.kuwo.cn/c023bffcc6118a26f373f74296fdd02d/5cada043/resource/a2/24/25/2025194711.aac, getWmaPlayUrl()=http://nn01.sycdn.kuwo.cn/f0c12ad680910b17edf1e8b8953c681f/5cada043/resource/m3/4/54/4045611513.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@670b40af, toString()=Music(mid=38841452, mname=2, mp3Size=23.42 MB, auther_url=http://www.kuwo.cn/mingxing/%E7%BA%AF%E9%9F%B3%E4%B9%90/, artist_pic=http://img1.kuwo.cn/star/starheads/120/2/d216967dcc21e3acc8bb878c9ed1b7d_0.jpg, artist_pic240=http://img1.kuwo.cn/star/starheads/120/10/74/3851039176.jpg, mp3PlayUrl=http://nn01.sycdn.kuwo.cn/d4cd5bdae8f318bdaf2d0ad147f3d324/5cada043/resource/n2/82/13/4037236395.mp3, aacPlayUrl=http://nn03.sycdn.kuwo.cn/c023bffcc6118a26f373f74296fdd02d/5cada043/resource/a2/24/25/2025194711.aac, wmaPlayUrl=http://nn01.sycdn.kuwo.cn/f0c12ad680910b17edf1e8b8953c681f/5cada043/resource/m3/4/54/4045611513.wma, comment=null, player=javafx.scene.media.MediaPlayer@670b40af), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=1227074340], KuwoPojo [label=KuwoLiLabel(mId=6649854, mLocal=2, newPlayUrl=http://www.kuwo.cn/yinyue/6649854/, newMusicName=オーディオドラマ2, albumUrl=http://www.kuwo.cn/album/516848/, albumName=美妙天堂 偶像曲音乐集, singerInfoUrl=http://www.kuwo.cn/mingxing/%E6%97%A5%E6%9C%ACACG/, singerName=日本ACG, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_6649854, oldTitle=オーディオドラマ2试听, mvUrl=, mvName=), getMid()=6649854, getMname()=オーディオドラマ2, getMp3Size()=9.14 MB, getAuther_url()=http://www.kuwo.cn/mingxing/%E6%97%A5%E6%9C%ACACG/, getArtist_pic()=http://img1.kuwo.cn/star/starheads/120/1/a22f2eac0a9ef8ee2a4c8a24c37d097_0.jpg, getArtist_pic240()=http://img2.kuwo.cn/star/starheads/240/9/58/1999698343.jpg, getMp3PlayUrl()=http://other.web.rg01.sycdn.kuwo.cn/f00ebb6ca41af0c9eaefdec0af58c94b/5cada043/resource/n3/71/6/1437177714.mp3, getAacPlayUrl()=http://other.web.rg03.sycdn.kuwo.cn/fff6b6ca90f2af99bfc69d8d3db716fe/5cada043/resource/a3/88/29/3758347980.aac, getWmaPlayUrl()=http://other.web.rg01.sycdn.kuwo.cn/85766eca459e6bc8e6e8235b14a2c873/5cada043/resource/m2/28/68/3751371124.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@7b69c6ba, toString()=Music(mid=6649854, mname=オーディオドラマ2, mp3Size=9.14 MB, auther_url=http://www.kuwo.cn/mingxing/%E6%97%A5%E6%9C%ACACG/, artist_pic=http://img1.kuwo.cn/star/starheads/120/1/a22f2eac0a9ef8ee2a4c8a24c37d097_0.jpg, artist_pic240=http://img2.kuwo.cn/star/starheads/240/9/58/1999698343.jpg, mp3PlayUrl=http://other.web.rg01.sycdn.kuwo.cn/f00ebb6ca41af0c9eaefdec0af58c94b/5cada043/resource/n3/71/6/1437177714.mp3, aacPlayUrl=http://other.web.rg03.sycdn.kuwo.cn/fff6b6ca90f2af99bfc69d8d3db716fe/5cada043/resource/a3/88/29/3758347980.aac, wmaPlayUrl=http://other.web.rg01.sycdn.kuwo.cn/85766eca459e6bc8e6e8235b14a2c873/5cada043/resource/m2/28/68/3751371124.wma, comment=null, player=javafx.scene.media.MediaPlayer@7b69c6ba), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=1188753216], KuwoPojo [label=KuwoLiLabel(mId=9327381, mLocal=3, newPlayUrl=http://www.kuwo.cn/yinyue/9327381/, newMusicName=约会2|Date 2, albumUrl=http://www.kuwo.cn/album/1271470/, albumName=《你的名字。》动画电影原声带, singerInfoUrl=http://www.kuwo.cn/mingxing/RADWIMPS/, singerName=RADWIMPS, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_9327381, oldTitle=约会2|Date 2试听, mvUrl=, mvName=), getMid()=9327381, getMname()=约会2|Date 2, getMp3Size()=4.92 MB, getAuther_url()=http://www.kuwo.cn/mingxing/RADWIMPS/, getArtist_pic()=http://img1.kuwo.cn/star/starheads/120/0/354b6ba694677c19968975290d93751_0.jpg, getArtist_pic240()=http://img2.kuwo.cn/star/starheads/120/96/38/1045446069.jpg, getMp3PlayUrl()=http://other.web.ri01.sycdn.kuwo.cn/ce259c69a2e2705d566feb1817ae39d4/5cada043/resource/n3/70/41/3441940813.mp3, getAacPlayUrl()=http://other.web.ri03.sycdn.kuwo.cn/bdf21044a3288cde9e8fb7ac5190d4b7/5cada043/resource/a2/81/2/2033018200.aac, getWmaPlayUrl()=http://other.web.ri01.sycdn.kuwo.cn/bbbe7fcdb873092628486d11a922a4d0/5cada043/resource/m1/31/90/297186568.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@13c27452, toString()=Music(mid=9327381, mname=约会2|Date 2, mp3Size=4.92 MB, auther_url=http://www.kuwo.cn/mingxing/RADWIMPS/, artist_pic=http://img1.kuwo.cn/star/starheads/120/0/354b6ba694677c19968975290d93751_0.jpg, artist_pic240=http://img2.kuwo.cn/star/starheads/120/96/38/1045446069.jpg, mp3PlayUrl=http://other.web.ri01.sycdn.kuwo.cn/ce259c69a2e2705d566feb1817ae39d4/5cada043/resource/n3/70/41/3441940813.mp3, aacPlayUrl=http://other.web.ri03.sycdn.kuwo.cn/bdf21044a3288cde9e8fb7ac5190d4b7/5cada043/resource/a2/81/2/2033018200.aac, wmaPlayUrl=http://other.web.ri01.sycdn.kuwo.cn/bbbe7fcdb873092628486d11a922a4d0/5cada043/resource/m1/31/90/297186568.wma, comment=null, player=javafx.scene.media.MediaPlayer@13c27452), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=640363654], KuwoPojo [label=KuwoLiLabel(mId=67369152, mLocal=4, newPlayUrl=http://www.kuwo.cn/yinyue/67369152/, newMusicName=Гудини 2, albumUrl=http://www.kuwo.cn/album/9669305/, albumName=88.1, singerInfoUrl=http://www.kuwo.cn/mingxing/%D0%90%D0%BB%D0%BA%D0%BE%D0%B3%D0%BE%D0%BB%D1%8C+%D0%BF%D0%BE%D1%81%D0%BB%D0%B5+%D1%81%D0%BF%D0%BE%D1%80%D1%82%D0%B0/, singerName=Алкоголь после спорта, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_67369152, oldTitle=Гудини 2试听, mvUrl=, mvName=), getMid()=67369152, getMname()=Гудини 2, getMp3Size()=6.96 MB, getAuther_url()=http://www.kuwo.cn/mingxing/%D0%90%D0%BB%D0%BA%D0%BE%D0%B3%D0%BE%D0%BB%D1%8C+%D0%BF%D0%BE%D1%81%D0%BB%D0%B5+%D1%81%D0%BF%D0%BE%D1%80%D1%82%D0%B0%26L+iZReal/, getArtist_pic()=http://img4.kuwo.cn/star/starheads/120/9/ec9de6230ab244febac6196db620f90_0.jpg, getArtist_pic240()=, getMp3PlayUrl()=http://so.sycdn.kuwo.cn/88541ad5ecf1a36cff6d81c8add15461/5cada043/resource/n2/22/50/3302719057.mp3, getAacPlayUrl()=http://so.sycdn.kuwo.cn/7e43dafebc24d71b9f6d878571b5048d/5cada043/resource/a2/21/3/3523011901.aac, getWmaPlayUrl()=http://so.sycdn.kuwo.cn/e74e5ae2c0973b33866cf16b66a462cb/5cada043/resource/m1/17/19/2422390277.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@5ed828d, toString()=Music(mid=67369152, mname=Гудини 2, mp3Size=6.96 MB, auther_url=http://www.kuwo.cn/mingxing/%D0%90%D0%BB%D0%BA%D0%BE%D0%B3%D0%BE%D0%BB%D1%8C+%D0%BF%D0%BE%D1%81%D0%BB%D0%B5+%D1%81%D0%BF%D0%BE%D1%80%D1%82%D0%B0%26L+iZReal/, artist_pic=http://img4.kuwo.cn/star/starheads/120/9/ec9de6230ab244febac6196db620f90_0.jpg, artist_pic240=, mp3PlayUrl=http://so.sycdn.kuwo.cn/88541ad5ecf1a36cff6d81c8add15461/5cada043/resource/n2/22/50/3302719057.mp3, aacPlayUrl=http://so.sycdn.kuwo.cn/7e43dafebc24d71b9f6d878571b5048d/5cada043/resource/a2/21/3/3523011901.aac, wmaPlayUrl=http://so.sycdn.kuwo.cn/e74e5ae2c0973b33866cf16b66a462cb/5cada043/resource/m1/17/19/2422390277.wma, comment=null, player=javafx.scene.media.MediaPlayer@5ed828d), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=84739718], KuwoPojo [label=KuwoLiLabel(mId=64690069, mLocal=5, newPlayUrl=http://www.kuwo.cn/yinyue/64690069/, newMusicName=:   ‘ ’ - 2, albumUrl=http://www.kuwo.cn/album/9292864/, albumName=Vivaldi x Corelli, singerInfoUrl=http://www.kuwo.cn/mingxing/%E9%9F%A9%E5%9B%BD%E7%BE%A4%E6%98%9F/, singerName=韩国群星, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_64690069, oldTitle=:   ‘ ’ - 2试听, mvUrl=, mvName=), getMid()=64690069, getMname()=:   ‘ ’ - 2, getMp3Size()=5.57 MB, getAuther_url()=http://www.kuwo.cn/mingxing/%E9%9F%A9%E5%9B%BD%E7%BE%A4%E6%98%9F/, getArtist_pic()=http://img1.kuwo.cn/star/starheads/120/c/f6f96588641ce157992799027343c51_0.jpg, getArtist_pic240()=http://img2.kuwo.cn/star/starheads/120/8/17/4095675122.jpg, getMp3PlayUrl()=http://sn.sycdn.kuwo.cn/f9daf88c4b24ec4b2705314c87050f45/5cada043/resource/n3/94/87/1282084366.mp3, getAacPlayUrl()=http://sn.sycdn.kuwo.cn/170aac072552ecd0b5560749c66ba315/5cada043/resource/a1/77/93/423628235.aac, getWmaPlayUrl()=http://sn.sycdn.kuwo.cn/5355ab5bef414e4e5514ef62fac02d8e/5cada043/resource/m2/23/23/1891343250.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@1e7c7811, toString()=Music(mid=64690069, mname=:   ‘ ’ - 2, mp3Size=5.57 MB, auther_url=http://www.kuwo.cn/mingxing/%E9%9F%A9%E5%9B%BD%E7%BE%A4%E6%98%9F/, artist_pic=http://img1.kuwo.cn/star/starheads/120/c/f6f96588641ce157992799027343c51_0.jpg, artist_pic240=http://img2.kuwo.cn/star/starheads/120/8/17/4095675122.jpg, mp3PlayUrl=http://sn.sycdn.kuwo.cn/f9daf88c4b24ec4b2705314c87050f45/5cada043/resource/n3/94/87/1282084366.mp3, aacPlayUrl=http://sn.sycdn.kuwo.cn/170aac072552ecd0b5560749c66ba315/5cada043/resource/a1/77/93/423628235.aac, wmaPlayUrl=http://sn.sycdn.kuwo.cn/5355ab5bef414e4e5514ef62fac02d8e/5cada043/resource/m2/23/23/1891343250.wma, comment=null, player=javafx.scene.media.MediaPlayer@1e7c7811), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=2011986105], null, KuwoPojo [label=KuwoLiLabel(mId=57452402, mLocal=7, newPlayUrl=http://www.kuwo.cn/yinyue/57452402/, newMusicName=2, albumUrl=http://www.kuwo.cn/album/6047869/, albumName=Sebastian Bach Overture in the French Manner, Fantasia in C Minor, Concert in G Major and Tocata in F Sharp minor and Christian Bach Duet in A Minor, singerInfoUrl=http://www.kuwo.cn/mingxing/%E4%B8%AD%E5%94%B1%E7%BE%A4%E6%98%9F/, singerName=中唱群星, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_57452402, oldTitle=2试听, mvUrl=, mvName=), getMid()=57452402, getMname()=2, getMp3Size()=3.17 MB, getAuther_url()=http://www.kuwo.cn/mingxing/%E4%B8%AD%E5%94%B1%E7%BE%A4%E6%98%9F/, getArtist_pic()=http://img2.kuwo.cn/star/starheads/120/f/a70b4ecd896bae5b3df88f2a0a45700_0.jpg, getArtist_pic240()=http://img1.kuwo.cn/star/starheads/120/8/46/2000273287.jpg, getMp3PlayUrl()=http://sh.sycdn.kuwo.cn/906b3ea07e7828debda6df1aede0c874/5cada043/resource/n2/59/2/3437472018.mp3, getAacPlayUrl()=http://sh.sycdn.kuwo.cn/f9aa8f381145f86358f843c2dea27813/5cada043/resource/a3/58/20/362415824.aac, getWmaPlayUrl()=http://sh.sycdn.kuwo.cn/416951394ecf91b58108d1ed01de730f/5cada043/resource/m2/67/83/497420227.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@3aeaafa6, toString()=Music(mid=57452402, mname=2, mp3Size=3.17 MB, auther_url=http://www.kuwo.cn/mingxing/%E4%B8%AD%E5%94%B1%E7%BE%A4%E6%98%9F/, artist_pic=http://img2.kuwo.cn/star/starheads/120/f/a70b4ecd896bae5b3df88f2a0a45700_0.jpg, artist_pic240=http://img1.kuwo.cn/star/starheads/120/8/46/2000273287.jpg, mp3PlayUrl=http://sh.sycdn.kuwo.cn/906b3ea07e7828debda6df1aede0c874/5cada043/resource/n2/59/2/3437472018.mp3, aacPlayUrl=http://sh.sycdn.kuwo.cn/f9aa8f381145f86358f843c2dea27813/5cada043/resource/a3/58/20/362415824.aac, wmaPlayUrl=http://sh.sycdn.kuwo.cn/416951394ecf91b58108d1ed01de730f/5cada043/resource/m2/67/83/497420227.wma, comment=null, player=javafx.scene.media.MediaPlayer@3aeaafa6), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=1990451863], KuwoPojo [label=KuwoLiLabel(mId=48141521, mLocal=8, newPlayUrl=http://www.kuwo.cn/yinyue/48141521/, newMusicName=明明说好不哭, albumUrl=http://www.kuwo.cn/album/6151356/, albumName=5sing原创音乐精选, singerInfoUrl=http://www.kuwo.cn/mingxing/2/, singerName=2, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_48141521, oldTitle=明明说好不哭试听, mvUrl=, mvName=), getMid()=48141521, getMname()=明明说好不哭, getMp3Size()=2.98 MB, getAuther_url()=http://www.kuwo.cn/mingxing/2/, getArtist_pic()=http://img2.kuwo.cn/star/starheads/120/c/81e728d9d4c2f636f067f89cc14862c_0.jpg, getArtist_pic240()=http://img1.kuwo.cn/star/starheads/120/49/31/3237900137.jpg, getMp3PlayUrl()=http://ns01.sycdn.kuwo.cn/47f999bacfaa575d97eabec01bd5496d/5cada043/resource/n2/41/94/3289571938.mp3, getAacPlayUrl()=http://ns03.sycdn.kuwo.cn/de4f9b7e26e7239096fda7d103c5aa89/5cada043/resource/a2/51/56/2550067746.aac, getWmaPlayUrl()=http://ns01.sycdn.kuwo.cn/6a17affe1ecabceef699daae6ddbd7d9/5cada043/resource/m1/68/75/3177759590.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@ed9d034, toString()=Music(mid=48141521, mname=明明说好不哭, mp3Size=2.98 MB, auther_url=http://www.kuwo.cn/mingxing/2/, artist_pic=http://img2.kuwo.cn/star/starheads/120/c/81e728d9d4c2f636f067f89cc14862c_0.jpg, artist_pic240=http://img1.kuwo.cn/star/starheads/120/49/31/3237900137.jpg, mp3PlayUrl=http://ns01.sycdn.kuwo.cn/47f999bacfaa575d97eabec01bd5496d/5cada043/resource/n2/41/94/3289571938.mp3, aacPlayUrl=http://ns03.sycdn.kuwo.cn/de4f9b7e26e7239096fda7d103c5aa89/5cada043/resource/a2/51/56/2550067746.aac, wmaPlayUrl=http://ns01.sycdn.kuwo.cn/6a17affe1ecabceef699daae6ddbd7d9/5cada043/resource/m1/68/75/3177759590.wma, comment=null, player=javafx.scene.media.MediaPlayer@ed9d034), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=1629604310], KuwoPojo [label=KuwoLiLabel(mId=48042049, mLocal=9, newPlayUrl=http://www.kuwo.cn/yinyue/48042049/, newMusicName=2, albumUrl=http://www.kuwo.cn/album/176030/, albumName=August, singerInfoUrl=http://www.kuwo.cn/mingxing/Giuseppe+Ielasi/, singerName=Giuseppe Ielasi, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_48042049, oldTitle=2试听, mvUrl=, mvName=), getMid()=48042049, getMname()=2, getMp3Size()=18.04 MB, getAuther_url()=http://www.kuwo.cn/mingxing/Giuseppe+Ielasi/, getArtist_pic()=http://img2.kuwo.cn/star/starheads/120/2/392d555b462c9a3f3dfd576d5c40a86_0.jpg, getArtist_pic240()=http://img2.kuwo.cn/star/starheads/240/78/89/1751408527.jpg, getMp3PlayUrl()=http://ns01.sycdn.kuwo.cn/404c43ff76a946567a549ca4396ac8f5/5cada043/resource/n1/89/33/845619874.mp3, getAacPlayUrl()=http://ns03.sycdn.kuwo.cn/8866bfda5d53c02acb8a398e93606fef/5cada043/resource/a2/96/5/3447293568.aac, getWmaPlayUrl()=http://ns01.sycdn.kuwo.cn/41249ef1f0816b3c37721792491d1631/5cada043/resource/m2/0/18/3371814130.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@4eb7f003, toString()=Music(mid=48042049, mname=2, mp3Size=18.04 MB, auther_url=http://www.kuwo.cn/mingxing/Giuseppe+Ielasi/, artist_pic=http://img2.kuwo.cn/star/starheads/120/2/392d555b462c9a3f3dfd576d5c40a86_0.jpg, artist_pic240=http://img2.kuwo.cn/star/starheads/240/78/89/1751408527.jpg, mp3PlayUrl=http://ns01.sycdn.kuwo.cn/404c43ff76a946567a549ca4396ac8f5/5cada043/resource/n1/89/33/845619874.mp3, aacPlayUrl=http://ns03.sycdn.kuwo.cn/8866bfda5d53c02acb8a398e93606fef/5cada043/resource/a2/96/5/3447293568.aac, wmaPlayUrl=http://ns01.sycdn.kuwo.cn/41249ef1f0816b3c37721792491d1631/5cada043/resource/m2/0/18/3371814130.wma, comment=null, player=javafx.scene.media.MediaPlayer@4eb7f003), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=246399377], KuwoPojo [label=KuwoLiLabel(mId=48145400, mLocal=10, newPlayUrl=http://www.kuwo.cn/yinyue/48145400/, newMusicName=我想我不够好, albumUrl=http://www.kuwo.cn/album/6151356/, albumName=5sing原创音乐精选, singerInfoUrl=http://www.kuwo.cn/mingxing/2/, singerName=2, oldPlayUrl=http://player.kuwo.cn/MUSIC/MUSIC_48145400, oldTitle=我想我不够好试听, mvUrl=, mvName=), getMid()=48145400, getMname()=我想我不够好, getMp3Size()=2.56 MB, getAuther_url()=http://www.kuwo.cn/mingxing/2/, getArtist_pic()=http://img1.kuwo.cn/star/starheads/120/c/81e728d9d4c2f636f067f89cc14862c_0.jpg, getArtist_pic240()=http://img3.kuwo.cn/star/starheads/120/49/31/3237900137.jpg, getMp3PlayUrl()=http://no01.sycdn.kuwo.cn/379af2ed6af1d984327f1b22cbcb9e88/5cada043/resource/n2/9/30/2859051026.mp3, getAacPlayUrl()=http://no03.sycdn.kuwo.cn/93ed006879534a3edeed305b245192c6/5cada043/resource/a1/57/93/490371609.aac, getWmaPlayUrl()=http://no01.sycdn.kuwo.cn/5867de1c3a80952d9b699776da8b94cf/5cada043/resource/m1/80/55/2867017690.wma, getComment()=null, getPlayer()=javafx.scene.media.MediaPlayer@1060b431, toString()=Music(mid=48145400, mname=我想我不够好, mp3Size=2.56 MB, auther_url=http://www.kuwo.cn/mingxing/2/, artist_pic=http://img1.kuwo.cn/star/starheads/120/c/81e728d9d4c2f636f067f89cc14862c_0.jpg, artist_pic240=http://img3.kuwo.cn/star/starheads/120/49/31/3237900137.jpg, mp3PlayUrl=http://no01.sycdn.kuwo.cn/379af2ed6af1d984327f1b22cbcb9e88/5cada043/resource/n2/9/30/2859051026.mp3, aacPlayUrl=http://no03.sycdn.kuwo.cn/93ed006879534a3edeed305b245192c6/5cada043/resource/a1/57/93/490371609.aac, wmaPlayUrl=http://no01.sycdn.kuwo.cn/5867de1c3a80952d9b699776da8b94cf/5cada043/resource/m1/80/55/2867017690.wma, comment=null, player=javafx.scene.media.MediaPlayer@1060b431), getClass()=class application.music.pojo.kuwo.KuwoPojo, hashCode()=1629911510]]

@ToString
class A {
	String name;
	int age;

	public A(String name, int age) throws InterruptedException {
		this.name = name;
		TimeUnit.MILLISECONDS.sleep(250);
		this.age = age;
	};
}
