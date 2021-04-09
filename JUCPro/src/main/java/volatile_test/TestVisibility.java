package volatile_test;

import java.util.concurrent.TimeUnit;

public class TestVisibility {
//	static boolean flag = true;
	static volatile boolean flag = true;
	
	public static void main(String[] args) {
		new Thread(()->{
			while(flag) {
				
			}
		}).start();
		
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		flag = false;
		System.out.println(flag);
	}
}
