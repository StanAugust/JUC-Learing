package lock_what;

import java.util.concurrent.TimeUnit;

public class Test2 {
	public static void main(String[] args) throws InterruptedException {
		Phone2 phone = new Phone2();
		Phone2 phone2 = new Phone2();
		
		new Thread(()->{
			phone.send();
		}).start();
		
		TimeUnit.SECONDS.sleep(1);
		
		new Thread(()->{
			phone2.call();
		}).start();
	}

}

class Phone2{
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
	
	public void hello() {
		System.out.println("hello");
	}
}
