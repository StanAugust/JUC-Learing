package blocking_queue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class TestSynchronousQueue {
	public static void main(String[] args) {
		SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
		
		new Thread(()->{
			try {
				System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date()) +" "+Thread.currentThread().getName() + " put 1");
				synchronousQueue.put(1);
				System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date()) +" "+Thread.currentThread().getName() + " put 2");
				synchronousQueue.put(2);
				System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date()) +" "+Thread.currentThread().getName() + " put 3");
				synchronousQueue.put(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "T1").start();
		
		new Thread(()->{
			try {
				while(true) {
					TimeUnit.SECONDS.sleep(new Random().ints(1, 0, 5).findFirst().getAsInt());
					System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date()) +" "+ Thread.currentThread().getName() + " take " + synchronousQueue.take());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "T2").start();
		
		new Thread(()->{
			try {
				while(true) {
					TimeUnit.SECONDS.sleep(new Random().ints(1, 0, 5).findFirst().getAsInt());
					System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS").format(new Date()) +" "+ Thread.currentThread().getName() + " take " + synchronousQueue.take());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "T3").start();
	}
}
