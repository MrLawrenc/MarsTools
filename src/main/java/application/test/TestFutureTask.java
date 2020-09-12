package application.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import application.music.online.kuwo.KuwoMusic;
import application.music.online.kuwo.pojo.kuwo.KuwoLiLabel;
import application.music.online.kuwo.pojo.kuwo.KuwoPojo;
import application.utils.MarsException;
import lombok.ToString;

public class TestFutureTask {

	public static void main(String[] args) throws InterruptedException, ExecutionException {

//		CopyOnWriteArrayList<FutureTask<A>> s = new CopyOnWriteArrayList<FutureTask<A>>();
//		List<Integer> ages = new ArrayList<Integer>();
//		for (int i = 0; i < 10; i++) {
//			ages.add(i);
//		}
//		long start = new Date().getTime();
//		for (int i = 0; i < 10; i++) {
//			Integer age = ages.get(i);
//			FutureTask<A> futureTask = new FutureTask<A>(() -> {
//				A a = new A("网", age);
//				return a;
//			});
//			new Thread(futureTask).start();
//			s.add(futureTask);
//		}
//
//		for (int i = 0; i < 10; i++) {
//			FutureTask<A> futureTask = s.get(i);
//			if (!futureTask.isDone()) {
//				System.out.println(futureTask.get());
//			}
//		}
//		System.out.println(System.currentTimeMillis() - start);
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
			CountDownLatch cdl=new CountDownLatch(10);
			for (int i = 0; i < musicNum && i < 10; i++) {
				KuwoLiLabel label = labelList.get(i);

				// Task<KuwoPojo> task=new Task<KuwoPojo>() {
				// @Override
				// protected KuwoPojo call() throws Exception {
				// return KuwoMusic.obj.parseMusicInfo1(label);
				// }};

				FutureTask<KuwoPojo> task = new FutureTask<KuwoPojo>(() -> {
					KuwoPojo kuwoPojo = KuwoMusic.obj.parseMusicInfo1(label);
					cdl.countDown();
					return kuwoPojo;
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

			try {
				cdl.await(2,TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			List<KuwoPojo> C = new ArrayList<KuwoPojo>();
			for (int i = 0; i < musicNum && i < 10; i++) {
				KuwoPojo pojo = null;
				try {
					System.out.println(s.get(i));
					pojo = s.get(i).get();
					System.out.println(pojo);
				} catch (Exception e) {
					e.printStackTrace();
				}
				C.add(pojo);
			}
			System.out.println("\\\\\\\\\\\\" + C.size());//
			service.shutdown();
			s = null;
			long b = new Date().getTime();
			System.out.println("本次搜索共耗时 b - a=" + (b - a));
		
	}

}


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
