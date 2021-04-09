package lock_what;

import java.util.concurrent.TimeUnit;

public class Test3 {
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws InterruptedException {
		Phone3 phone = new Phone3();
		Phone3 phone2 = new Phone3();
		
		new Thread(()->{
			phone.send();
		}).start();
		
		TimeUnit.SECONDS.sleep(1);
		
		new Thread(()->{
			phone2.call();
//			phone2.hello();
		}).start();
	}

}

class Phone3{
	public static synchronized void send() {
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("发短信");
	}
	
	public static synchronized void call() {
		System.out.println("打电话");
	}
	
	public void hello() {
		System.out.println("hello");
	}
}
