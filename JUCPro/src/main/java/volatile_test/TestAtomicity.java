package volatile_test;

import java.util.concurrent.atomic.AtomicInteger;

public class TestAtomicity {	
	// volatile不保证原子性
//	private static volatile int num = 0;
	// 不加锁的情况下，使用原子类来保证原子性
	private static volatile AtomicInteger num = new AtomicInteger();
	
	public static void add() {
//		num++;
		num.getAndIncrement();	// 底层是CAS保证的原子性
	}
	
	public static void main(String[] args) {
		
		for(int i=0; i<20; i++) {
			new Thread(()->{
				for(int j=0; j<1000; j++) {
					add();
				}
			}).start();
		}
		
		while(Thread.activeCount() > 2) {	// main、gc
			// 当前线程让出使用权
			Thread.yield();
		}
		
		System.out.println(num);
	}
}
