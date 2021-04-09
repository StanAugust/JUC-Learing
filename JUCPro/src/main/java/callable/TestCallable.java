package callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class TestCallable {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
//		new Thread(new Runnable()).start();
//		new Thread(new FutureTask<V>()).start();		// 因为 FutureTask implements Runnable，所以上句等价于这句
//		new Thread(new FutureTask<V>( Callable<V> callable) ).start();
		
		MyThread thread = new MyThread();
		
		// 适配类：FutureTask
		FutureTask<Integer> task = new FutureTask<>(thread);
		
		// 放入Thread
		new Thread(task, "A").start();
		new Thread(task, "B").start();
		
		// 获取返回值
		Integer integer = task.get();
		System.out.println(integer);
		
	}

}

class MyThread implements Callable<Integer>{

	@Override
	public Integer call() throws Exception {
		System.out.println(Thread.currentThread().getName() + " call>>>");
//		TimeUnit.SECONDS.sleep(5);
		return 1024;
	}
	
}