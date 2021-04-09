package thread_pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestThreadPool2 {
	public static void main(String[] args) {
		// 7大参数和4种拒绝策略
		ExecutorService threadPool = new ThreadPoolExecutor(2,
															5,
															3,
															TimeUnit.SECONDS,
															new LinkedBlockingQueue<>(3),
															Executors.defaultThreadFactory(),
															new ThreadPoolExecutor.CallerRunsPolicy());
		
		try {
			for (int i = 0; i < 10; i++) {
				threadPool.execute(()->{
					System.out.println(Thread.currentThread().getName() + " execute");
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
			}
			
		} finally {
			threadPool.shutdown();
		}
	}
}
