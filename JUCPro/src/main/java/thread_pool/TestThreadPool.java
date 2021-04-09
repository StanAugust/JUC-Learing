package thread_pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThreadPool {
	public static void main(String[] args) {
		// 线程池三大方法
//		ExecutorService threadPool = Executors.newSingleThreadExecutor();
//		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		try {
			for (int i = 0; i < 10; i++) {
				threadPool.execute(()->{
					System.out.println(Thread.currentThread().getName() + " execute");
				});
			}
			
		} finally {
			threadPool.shutdown();
		}
	}
}
