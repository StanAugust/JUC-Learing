package lock;

import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TestReadWriteLock {
	
	public static void main(String[] args) {
		
//		MyCache cache = new MyCache();
		MyCacheLock cache = new MyCacheLock();
		
		// 开启5个写入线程
		for (int i = 0; i < 5; i++) {
			
			final int tmp = i;
			
			new Thread(()->{
				cache.put(String.valueOf(tmp), tmp);
			}, String.valueOf(i)).start();
		}
		// 开启5个读取线程
		for (int i = 0; i < 5; i++) {
			
			final int tmp = i;
			
			new Thread(()->{
				cache.get(String.valueOf(tmp));
			}, String.valueOf(i)).start();
		}
	}

}

class MyCacheLock {	
	private volatile HashMap<String, Object> map = new HashMap<>();
	// synchronize和lock也可以达到一样的效果
	// 但这次采用更细力度的锁来保证数据的可靠
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	public void put(String k, Object v) {
		readWriteLock.writeLock().lock();
		
		try {			
			System.out.println(Thread.currentThread().getName() + " 开始写>>");
			map.put(k, v);
			System.out.println(Thread.currentThread().getName() + " 写完成");
			
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	
	public void get(String k) {
		readWriteLock.readLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + " 开始读>>");
			map.get(k);
			System.out.println(Thread.currentThread().getName() + " 读完成");
			
		} finally {
			readWriteLock.readLock().unlock();
		}
	}	
}

class MyCache {	
	private volatile HashMap<String, Object> map = new HashMap<>();
	
	public void put(String k, Object v) {
		System.out.println(Thread.currentThread().getName() + " 开始写");
		map.put(k, v);
		System.out.println(Thread.currentThread().getName() + " 写完成");
	}
	
	public void get(String k) {
		System.out.println(Thread.currentThread().getName() + " 开始读");
		map.get(k);
		System.out.println(Thread.currentThread().getName() + " 读完成");
	}	
}
