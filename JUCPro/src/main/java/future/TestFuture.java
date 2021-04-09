package future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TestFuture {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		Future<String> future = threadPool.submit(()->{
			try {
				TimeUnit.SECONDS.sleep(3);
				// 如果子线程执行异常时，其异常会被捕获，get方法就会中止等待并抛出异常，并不会无限制等待
//				if(true) {
//					throw new RuntimeException("test");
//				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "future result";
		});
		
		System.out.println(future.get());
		
		threadPool.shutdown();
	}
}
