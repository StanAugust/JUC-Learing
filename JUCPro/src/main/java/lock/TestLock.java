package lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestLock {
	
	public static void main(String[] args) {
		Ticket2 ticket = new Ticket2();
		
		new Thread(()->{
			for(int i=0; i<15; i++) {
				ticket.sale();
			}
		}, "A") .start();
		
		new Thread(()->{
			for(int i=0; i<15; i++) {
				ticket.sale();
			}
		}, "B") .start();
		
		new Thread(()->{
			for(int i=0; i<15; i++) {
				ticket.sale();
			}
		}, "C") .start();
	}

}

class Ticket2{
	
	private int num = 30;
	
	private Lock lock = new ReentrantLock();
	
	public void sale() {
		lock.lock();
		
		try {
			if (num > 0) {
				System.out.println(Thread.currentThread().getName() + "卖出了第 " + num + " 张票, 剩余：" + --num);
			} 
		} finally {			
			lock.unlock();
		}
	}
	
}
