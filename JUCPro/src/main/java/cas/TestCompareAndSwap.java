package cas;

import java.util.concurrent.atomic.AtomicInteger;

public class TestCompareAndSwap {
	public static void main(String[] args) {
		AtomicInteger atomicInteger = new AtomicInteger(2021);
		
		// boolean compareAndSet(int expect, int update)
		// 期望值、更新值
        // 如果实际值 和 期望值相同，那么就更新; 否则不更新
		System.out.println(atomicInteger.compareAndSet(2021, 2020));
		System.out.println(atomicInteger.get());		
		
		// 自增操作，此时实际值2021
		atomicInteger.getAndIncrement();
		// 此时期望值和实际值不同，更新失败
		System.out.println(atomicInteger.compareAndSet(2020, 2021));
		// 返回实际值
		System.out.println(atomicInteger.get());
	}
}
