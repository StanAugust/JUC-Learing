package cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @ClassName: TestAtomicStampedReference   
 * @Description: AtomicStampedReference 解决ABA问题   
 * @author Stan
 * @date: 2021年4月9日
 */
public class TestAtomicStampedReference {
	
	private static AtomicStampedReference<Integer> asr = new AtomicStampedReference<Integer>(10, 1);
	
	public static void main(String[] args) {
		
		int stamp = asr.getStamp(); // 获得版本号
		
		new Thread(()->{
            System.out.println(Thread.currentThread().getName() + " get stamp：" + stamp);
			
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 修改操作时，版本号更新 + 1
			System.out.println(asr.compareAndSet(10, 20, stamp , asr.getStamp()+1));
			System.out.println(Thread.currentThread().getName() + " " + asr.getReference() + "," + asr.getStamp());
			// 重新把值改回去， 版本号更新 + 1
			System.out.println(asr.compareAndSet(20, 10, asr.getStamp() , asr.getStamp()+1));
			System.out.println(Thread.currentThread().getName() + " " + asr.getReference() + "," + asr.getStamp());

		}, "T1").start();
		
		new Thread(()->{
            System.out.println(Thread.currentThread().getName() + " get stamp：" + stamp);
			
            try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println(asr.compareAndSet(10, 30, stamp, asr.getStamp()+1));
			System.out.println(Thread.currentThread().getName() + " " + asr.getReference() + "," + asr.getStamp());
			
		}, "T2").start();
	}
}
