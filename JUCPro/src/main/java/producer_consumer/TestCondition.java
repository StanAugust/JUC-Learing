package producer_consumer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestCondition {
	
	public static void main(String[] args) {
		Data3 data = new Data3();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				data.printA();
			}
		},"A").start();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				data.printB();;
			}
		},"B").start();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				data.printC();
			}
		},"C").start();
		
	}
}

/**
 * 指定调用顺序
 * A 执行完 调用B
 * B 执行完 调用C
 * C 执行完 调用A
 */
class Data3{
	private Lock lock = new ReentrantLock();
	private Condition condition1 = lock.newCondition();
	private Condition condition2 = lock.newCondition();
	private Condition condition3 = lock.newCondition();
	
	private int num = 1;
	
	public void printA() {
		lock.lock();				// 加锁
		try {
			while(num != 1) {		// 判断等待
				condition1.await();
			}
			System.out.println(Thread.currentThread().getName() + "=>" + num);		
			
			num++;
			condition2.signalAll();	// 唤醒指定的线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();			// 解锁
		}
	}
	
	public void printB() {
		lock.lock();				// 加锁
		try {
			while(num != 2) {		// 判断等待
				condition2.await();
			}
			System.out.println(Thread.currentThread().getName() + "=>" + num);		
			
			num++;
			condition3.signalAll();	// 唤醒指定的线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();			// 解锁
		}
	}
	
	public void printC() {
		lock.lock();				// 加锁
		try {
			while(num != 3) {		// 判断等待
				condition2.await();
			}
			System.out.println(Thread.currentThread().getName() + "=>" + num);		
			
			num = 1;
			condition1.signalAll();	// 唤醒指定的线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();			// 解锁
		}
	}
}