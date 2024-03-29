package commonly_used_util;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class TestSemaphore {
	public static void main(String[] args) {
		// 模拟抢车位
		Semaphore semaphore = new Semaphore(3);
		
		for (int i = 0; i < 6; i++) {
			new Thread(()->{
				try {
					semaphore.acquire();	// 获取，如果资源已经使用完了，就等待资源释放后再进行使用
					System.out.println(Thread.currentThread().getName() + "抢到车位");
					TimeUnit.SECONDS.sleep(2);
					System.out.println(Thread.currentThread().getName() + "离开车位");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {	
					semaphore.release();	// 释放，会将当前的信号量释放+1，然后唤醒等待的线程
				}
			}, String.valueOf(i)).start();
		}
	}

}
