package forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

public class TestForkJoin {
	
	private static final long SUM = 20_0000_0000;
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		test1();
		test2();
		test3();
	}
	
	/**
	 * 使用普通方法
	 */
	public static void test1() throws InterruptedException, ExecutionException {
		long start = System.currentTimeMillis();
		
		long sum = 0L;
		for (int i = 0; i < SUM; i++) {
			sum += i;
		}
		System.out.println(sum);
		
		long end = System.currentTimeMillis();
		System.out.println("耗费时间：" + (end-start));
		System.out.println("=======================");
	}
	
	/**
	 * 使用ForkJoin方法
	 */
	public static void test2() throws InterruptedException, ExecutionException {
		long start = System.currentTimeMillis();
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		MyForkJoinTask forkJoinTask = new MyForkJoinTask(0L, SUM);
		
		ForkJoinTask<Long> submit = forkJoinPool.submit(forkJoinTask);
		Long sum = submit.get();
		
		System.out.println(sum);
		
		long end = System.currentTimeMillis();
		System.out.println("耗费时间：" + (end-start));
		System.out.println("=======================");

	}
	
	/**
	 * 使用Stream流计算
	 */
	public static void test3() throws InterruptedException, ExecutionException {
		long start = System.currentTimeMillis();
		
		// reduce(identity, binary operator) : 提供一个跟Stream中数据同类型的初始值identity，通过累加器accumulator迭代计算Stream中的数据，得到一个跟Stream中数据相同类型的最终结果
		long sum = LongStream.range(0L, SUM).parallel().reduce(0, Long::sum);
		
		System.out.println(sum);
		
		long end = System.currentTimeMillis();
		System.out.println("耗费时间：" + (end-start));
		System.out.println("=======================");

	}
}
