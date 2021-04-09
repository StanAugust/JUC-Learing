package cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


/**
 * @ClassName: TestAtomicReference   
 * @Description: AtomicReference类维护多个共享变量  
 * @author Stan
 * @date: 2021年4月9日
 */
public class TestAtomicReference2 {
	public static void main(String[] args) {
		Data initData = new Data(0, 1);
		
		Data change1 = new Data(5, 8);
		Data change2 = new Data(3,2);
		
		// 设置对象初始值
		AtomicReference<Data> atomicReference = new AtomicReference<>(initData);
		
		// 设置两个线程去修改值
		new Thread(()->{
			atomicReference.compareAndSet(initData, change1);	
			System.out.println(Thread.currentThread().getName() + "  " + atomicReference.get());
			
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println(Thread.currentThread().getName() + "(2)  " + atomicReference.get());
			
		}, "T1").start();
		
		new Thread(()->{
			atomicReference.compareAndSet(change1, change2);		
			System.out.println(Thread.currentThread().getName() + "  " + atomicReference.get());
		}, "T2").start();		
	}
}

class Data {
	int a , b;
	
	public Data(int a, int b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public String toString() {
		return "a:" + a + ",b:" + b;
	}
}