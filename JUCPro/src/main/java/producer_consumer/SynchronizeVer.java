package producer_consumer;

public class SynchronizeVer {
	
	public static void main(String[] args) {
		Data data = new Data();
		
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

class Data{
	private int num = 0;
	
	public synchronized void increment() throws InterruptedException {
		while(num != 0) {
			this.wait();
		}
		num++;
		System.out.println(Thread.currentThread().getName() + "=>" + num);
		this.notifyAll();
	}
	
	public synchronized void decrement() throws InterruptedException {
		while(num == 0) {
			this.wait();
		}
		num--;
		System.out.println(Thread.currentThread().getName() + "=>" + num);
		this.notifyAll();
	}
}