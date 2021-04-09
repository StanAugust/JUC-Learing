package producer_consumer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockVer {
	
	public static void main(String[] args) {
		Data2 data = new Data2();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				try {
					data.increment();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"producer").start();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				try {
					data.decrement();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"consumer").start();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				try {
					data.increment();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"producer2").start();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				try {
					data.decrement();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"consumer2").start();
	}
}

class Data2{
	private int num = 0;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public void increment() throws InterruptedException {
		lock.lock();				// 加锁
		try {
			while(num != 0) {		// 判断等待
				condition.await();
			}
			num++;
			System.out.println(Thread.currentThread().getName() + "=>" + num);		
			
			condition.signalAll();	// 通知其他线程
		} finally {
			lock.unlock();			// 解锁
		}
	}
	
	public void decrement() throws InterruptedException {
		lock.lock();				
		try {
			while(num == 0) {		
				condition.await();
			}
			num--;
			System.out.println(Thread.currentThread().getName() + "=>" + num);
			
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
}
