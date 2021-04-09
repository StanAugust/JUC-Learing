package blocking_queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TestBlockingQueue {

	public static void main(String[] args) {
		test1();
	}

	/**
	 * @Description: 抛出异常
	 */
	public static void test1() {
		ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(3);

		System.out.println(blockingQueue.add(1));
		System.out.println(blockingQueue.add(2));
		System.out.println(blockingQueue.add(3));

		// 抛出异常：java.lang.IllegalStateException: Queue full
//		System.out.println(blockingQueue.add(4));

		System.out.println(blockingQueue.remove());
		System.out.println(blockingQueue.remove());
		System.out.println(blockingQueue.remove());

		// 如果多移除一个或查看空队列
		// 均会造成 java.util.NoSuchElementException 抛出异常
//		System.out.println(blockingQueue.remove());
//		System.out.println(blockingQueue.element());
	}

	/**
	 * @Description: 不抛出异常，有返回值
	 */
	public static void test2() {
		ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(3);

		System.out.println(blockingQueue.offer(1));
		System.out.println(blockingQueue.offer(2));
		System.out.println(blockingQueue.offer(3));
		// 添加 一个不能添加的元素 使用offer只会返回false 不会抛出异常
		System.out.println(blockingQueue.offer(4));

		System.out.println(blockingQueue.poll());
		System.out.println(blockingQueue.poll());
		System.out.println(blockingQueue.poll());
		// 弹出 如果没有元素 只会返回null 不会抛出异常
		System.out.println(blockingQueue.poll());
	}
	
	/**
	 * @Description: 等待 一直阻塞
	 */
	public static void test3() {
		ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(3);
		
		try {
			blockingQueue.put(1);
			blockingQueue.put(2);
			blockingQueue.put(3);
			// 如果队列已经满了， 再进去一个元素  这种情况会一直等待这个队列 什么时候有了位置再进去，程序不会停止
//			blockingQueue.put(4);
			
			System.out.println(blockingQueue.take());
			System.out.println(blockingQueue.take());
			System.out.println(blockingQueue.take());
			// 如果再拿一个  这种情况也会等待，程序会一直运行 阻塞
			System.out.println(blockingQueue.take());			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Description: 等待 超时阻塞
	 * @throws InterruptedException 
	 */
	public static void test4() throws InterruptedException {
		ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(3);
		
		blockingQueue.offer(1);
		blockingQueue.offer(2);
		blockingQueue.offer(3);
		
//		TimeUnit.SECONDS.sleep(1);
//		blockingQueue.poll();
		// 如果队列已经满了， 再进去一个元素，等待指定时间后若还不满足条件就结束等待
//		System.out.println(blockingQueue.offer(4, 2, TimeUnit.SECONDS));
		
		System.out.println(blockingQueue.poll());
		System.out.println(blockingQueue.poll());
		System.out.println(blockingQueue.poll());
		// 同样地，超过指定时间还没有数据就停止等待
		System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));		
	}
}
