package commonly_used_util;

import java.util.concurrent.CountDownLatch;

public class TestCountDownLatch {
	public static void main(String[] args) throws InterruptedException {
		
		// 设置线程总数为6
		CountDownLatch latch = new CountDownLatch(6);
		
		for (int i = 0; i < 6; i++) {
			new Thread(()->{
				
				System.out.println(Thread.currentThread().getName() + " go out>>>");
				latch.countDown();		// 每启动一个线程，计数-1
				
			}, String.valueOf(i)).start();
		}
		
//		latch.await();	// 让当前线程等待计数器归零，然后才执行后续操作
		
		System.out.println("close door>>>");
	}
}
