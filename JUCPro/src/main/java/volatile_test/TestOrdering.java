package volatile_test;

import java.util.concurrent.TimeUnit;

public class TestOrdering {
	
	private static volatile boolean init = false;
	
	private static void loadConfig() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("load config done>>>");
	}
	
	private static void doSomethingWithConfig() {
		System.out.println("do something>>>");
	}
	
	public static void main(String[] args) {
		
		
		new Thread(()->{
			loadConfig();
			init = true;
		}).start();
		
		
		new Thread(()->{
			while(!init) {	
			}
			
			doSomethingWithConfig();			
		}).start();
		
	}
}

class Config {
	
}
