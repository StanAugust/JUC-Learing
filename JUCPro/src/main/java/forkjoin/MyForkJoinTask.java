package forkjoin;

import java.util.concurrent.RecursiveTask;

public class MyForkJoinTask extends RecursiveTask<Long>{
	
	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 3590905479475571273L;
	
	private long begin;
	private long end;
	// 临界值
	private long threshold = 1000000L;
	
	public MyForkJoinTask(long begin, long end) {
		this.begin = begin;
		this.end = end;
	}


	/*
	 * ForkJoinTask与一般任务的主要区别在于它需要实现compute方法，在这个方法里，
	 * 首先需要判断任务是否足够小，如果足够小就直接执行任务。如果不足够小，就必须分割成两个子任务
	 */
	@Override
	protected Long compute() {
		if(end-begin < threshold) {
			Long sum = 0L;
			for (long i = begin; i < end; i++) {
				sum += i;
			}
			return sum;
			
		}else {
			long middle = (begin + end) / 2;
			MyForkJoinTask task1 = new MyForkJoinTask(begin, middle);			
			MyForkJoinTask task2 = new MyForkJoinTask(middle, end);
			
			// 执行子任务
//			task1.fork();
//			task2.fork();
			
			// invokeAll会并行运行两个子任务:
			invokeAll(task1,task2);
			
			return task1.join() + task2.join();
		}
	}
	

}
