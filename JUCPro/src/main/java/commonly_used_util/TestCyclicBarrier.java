package commonly_used_util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TestCyclicBarrier {
	public static void main(String[] args) {
		// 主线程，开启七个线程后再执行此Runnable
		CyclicBarrier barrier = new CyclicBarrier(7, ()->{
			System.out.println("集齐七颗龙珠，召唤神龙>>>");
		});
		
		for (int i = 0; i < 7; i++) {
			final int tmp = i;
			new Thread(()->{
				System.out.println(Thread.currentThread().getName() + " 收集了第" + tmp + "颗");
				
				try {
					barrier.await();	// 加法计数，等待
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
			}).start();
		}
	}
}
