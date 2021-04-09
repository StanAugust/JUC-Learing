package lock_what;

import java.util.concurrent.TimeUnit;

public class Test1 {
	public static void main(String[] args) throws InterruptedException {
		Phone phone = new Phone();
		
		new Thread(()->{
			phone.send();
		}).start();
		
		TimeUnit.SECONDS.sleep(1);
		
		new Thread(()->{
			phone.call();
		}).start();
	}

}

class Phone{
	public synchronized void send() {
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("发短信");
	}
	
	public synchronized void call() {
		System.out.println("打电话");
	}
}
