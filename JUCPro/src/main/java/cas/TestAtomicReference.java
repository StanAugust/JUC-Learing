package cas;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @ClassName: TestAtomicReference   
 * @Description: AtomicReference类实现自旋锁   
 * @author Stan
 * @date: 2021年4月9日
 */
public class TestAtomicReference {
	/*
	 * 自旋锁是假设在不久将来，当前的线程可以获得锁，因此虚拟机会让当前想要获取锁的线程做几个空循环(这也是称为自旋的原因)，
	 * 在经过若干次循环后，如果得到锁，就顺利进入临界区。如果还不能获得锁，那就会将线程在操作系统层面挂起，这种方式确实也是可以提升效率的。
	 * 但问题是当线程越来越多竞争很激烈时，占用CPU的时间变长会导致性能急剧下降，
	 * 因此Java虚拟机内部一般对于自旋锁有一定的次数限制，可能是50或者100次循环后就放弃，直接挂起线程，让出CPU资源。
	 */
	public static void main(String[] args) throws InterruptedException {
		SpinLock spinLock = new SpinLock();
		
		new Thread(()->{
			spinLock.lock();
			
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
			} finally {
				spinLock.unlock();
			}
		}, "T1").start();
		
		TimeUnit.SECONDS.sleep(1);
		
		new Thread(()->{
			spinLock.lock();
			
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
			} finally {
				spinLock.unlock();
			}
		}, "T2").start();
	}
}

class SpinLock {
	private AtomicReference<Thread> atomicReference = new AtomicReference<>();
	
	public void lock() {
		Thread currentThread = Thread.currentThread();
		System.out.println(System.currentTimeMillis() + " " + currentThread.getName() + " ===> mylock");
		
		while (!atomicReference.compareAndSet(null, currentThread)) {
		}
		System.out.println(System.currentTimeMillis() + " " + currentThread.getName() + " ===> spin done");
	}
	
	public void unlock() {
		Thread currentThread = Thread.currentThread();
		System.out.println(System.currentTimeMillis() + " " + currentThread.getName() + " ===> myUnlock");
		atomicReference.compareAndSet(currentThread, null);
	}
}