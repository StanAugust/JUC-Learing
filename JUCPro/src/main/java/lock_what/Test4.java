package lock_what;

import java.util.concurrent.TimeUnit;

public class Test4 {
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws InterruptedException {
		Phone4 phone = new Phone4();
		Phone4 phone2 = new Phone4();
		
		new Thread(()->{
			phone.send();
		}).start();
		
		TimeUnit.SECONDS.sleep(1);
		
		new Thread(()->{
			phone2.call();
		}).start();
	}

}

class Phone4{
	public static synchronized void send() {
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
