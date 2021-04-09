package future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CompletableFutureAPI {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
//		createAsyncTasks();
//		thenActions();
//		testExceptionally();
//		whenCompleteAndHandle();
//		combine();
//		chooseOne();
//		testThenCompose();
		allOfAndAnyOf();
	}
	
	/**
	 * @Description: 创建异步任务
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void createAsyncTasks() throws InterruptedException, ExecutionException {
		// 有返回值
		CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(()->{
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "test supplyAsync";	
		});
		
		System.out.println(supplyAsync.get());
//		
//		// 无返回值
//		CompletableFuture<Void> runAsync = CompletableFuture.runAsync(()->{
//			try {
//				TimeUnit.SECONDS.sleep(3);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		});
//		
//		System.out.println(runAsync.get());
	}

	/**
	 * @Description: 异步回调  thenApply/ thenAccept/ thenRun
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void thenActions() throws InterruptedException, ExecutionException {
		CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(()->{
			try {
				TimeUnit.SECONDS.sleep(3);
				System.out.println(System.currentTimeMillis()+": "+Thread.currentThread().getName() + " supplyAsync");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return "test supplyAsync";	
		});
		
		// 某个任务执行完成之后，再执行的动作
		// 有返回值
		CompletableFuture thenApply = supplyAsync.thenApplyAsync((result)->{
			try {
				TimeUnit.SECONDS.sleep(2);
				System.out.println(System.currentTimeMillis()+": "+Thread.currentThread().getName() + " thenApplyAsync");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return result.concat(" then applyAsync");
			
		}).thenAcceptAsync((result)->{	// 跟thenApply一样接收上一个任务的返回值作为入参，但是无返回值		
			try {
				TimeUnit.SECONDS.sleep(2);
				System.out.println(System.currentTimeMillis()+": "+Thread.currentThread().getName() + result.concat(" then acceptAsync"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}).thenRun(()->{	// 跟thenAccept一样无返回值，但是也无入参，只是任务的顺序执行
			try {
				TimeUnit.SECONDS.sleep(2);
				System.out.println(System.currentTimeMillis()+": "+Thread.currentThread().getName() + " then Run");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		System.out.println(System.currentTimeMillis()+": "+thenApply.get());
	}

	/**
	 * @Description: 异步回调 exceptionaly  
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void testExceptionally() throws InterruptedException, ExecutionException {
		
		CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(()->{
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(true) {
				throw new RuntimeException("test exceptionally");
			}
			return "test supplyAsync";	
		});
		
		// 执行异常时，将抛出的异常作为入参传递给回调方法
		CompletableFuture<Void> future = supplyAsync.exceptionally((throwable)->{
			
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("into exceptionally");
			throwable.printStackTrace();
			
			return "throwable exception";
			
		}).thenAccept((result)->{	// 正常执行时执行的逻辑，如果执行异常则不调用此逻辑

			try {
				TimeUnit.SECONDS.sleep(2);
				System.out.println(System.currentTimeMillis()+": "+Thread.currentThread().getName() + result.concat(" then acceptAsync"));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		System.out.println(future.get());
	}

	/**
	 * @Description: 异步回调 whenComplete/ handle  
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void whenCompleteAndHandle() throws InterruptedException, ExecutionException {
		CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(()->{
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(false) {
				throw new RuntimeException("test exceptionally");
			}
			return "test supplyAsync";	
		}).whenComplete((t, u)->{
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(u != null) {
				System.out.println("执行出现异常：" + u);
			}else {
				System.out.println("执行无异常：" + t);				
			}
		})
//		.handle((t, u)->{							// 和whenComplete一样，多了返回值
//			try {
//				TimeUnit.SECONDS.sleep(2);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			
//			if(u != null) {
//				return "执行出现异常：" + u;
//			}else {
//				return "执行无异常：" + t;
//			}
//		})
		;

		System.out.println(supplyAsync.get());		
	}

	/**
	 * @Description: 结合两个CompletableFuture，两个都结束后才会执行某个任务  
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void combine() throws InterruptedException, ExecutionException {
		
		CompletableFuture<Double> cf = CompletableFuture.supplyAsync(()->{
            try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            return 1.2;
        });
        CompletableFuture<Double> cf2 = CompletableFuture.supplyAsync(()->{
            try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            return 3.2;
        });
        
        // cf和cf2的异步任务都执行完成后，会将其执行结果作为方法入参传递给cf3,且有返回值
        CompletableFuture<Double> cf3 = cf.thenCombine(cf2, (a,b)->{ return a+b;});
        
        // cf和cf2的异步任务都执行完成后，会将其执行结果作为方法入参传递给cf4,无返回值
        CompletableFuture<Void> cf4 = cf.thenAcceptBothAsync(cf2, (a,b)->{ System.out.println(a+b);});
        cf4.join();
        
        // cf4和cf3都执行完成后，执行cf5，无入参，无返回值
        CompletableFuture<Void> cf5 = cf4.runAfterBothAsync(cf3, ()->{
        	try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	System.out.println("all done");
        });  
	}

	/**
	 * @Description: 结合两个CompletableFuture，  只要其中一个执行完了就会执行某个任务
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void chooseOne() throws InterruptedException, ExecutionException {
		CompletableFuture<Double> cf = CompletableFuture.supplyAsync(()->{
            try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            return 1.2;
        });
        CompletableFuture<Double> cf2 = CompletableFuture.supplyAsync(()->{
            try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            return 3.2;
        });
        
        
        // cf和cf2的异步任务任一执行完成后，会将其执行结果作为方法入参传递给cf3,且有返回值
        CompletableFuture<Double> cf3 = cf.applyToEither(cf2, (result)->{ return result+1;});
        System.out.println(cf3.get());
        
        // cf和cf2的异步任务任一执行完成后，会将其执行结果作为方法入参传递给cf4,无返回值
        CompletableFuture<Void> cf4 = cf.acceptEither(cf2, (result)->{ System.out.println(result+1);});
        
        // cf4和cf3任一执行完成后，执行cf5，无入参，无返回值
        CompletableFuture<Void> cf5 = cf3.runAfterEither(cf4, ()->{System.out.println("all done");});
        
        cf5.join();
	}

	public static void testThenCompose() throws InterruptedException, ExecutionException {
		CompletableFuture<Double> cf = CompletableFuture.supplyAsync(()->{
            try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            System.out.println(Thread.currentThread().getName()+" 1.2");
            return 1.2;
        });
		
		
		CompletableFuture<Double> cf2 = cf.thenComposeAsync((result)->{
			
			return CompletableFuture.supplyAsync(()->{
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return result+1;
			});
		});
		
		System.out.println(cf2.get());
	}
	
	/**
	 * @Description: 结合多个  CompletableFuture
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public static void allOfAndAnyOf() throws InterruptedException, ExecutionException {
		CompletableFuture<Double> cf = CompletableFuture.supplyAsync(()->{
            try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            return 1.2;
        });
        CompletableFuture<Double> cf2 = CompletableFuture.supplyAsync(()->{
            try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            return 3.2;
        });
        CompletableFuture<Double> cf3 = CompletableFuture.supplyAsync(()->{
            try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            return 2.6;
        });
        
        // 多个任务都执行完成后才会执行
        // 如果都是正常执行，则get返回null; 有任一个任务执行异常，则返回的CompletableFuture执行get方法时会抛出异常
        CompletableFuture<Void> cf4 = CompletableFuture.allOf(cf,cf2,cf3)       		
        											   .whenComplete((u,t)->{
        												   if(t!=null) {
        													   System.out.println("异常："+ t);        													
        												   }else {
        													   System.out.println("正常："+ u);
        												   }
        											   });
        System.out.println(cf4.get());
        
        // 多个任务只要其中一个执行完成就会执行
        // 其get返回的是已经执行完成的任务的执行结果，如果该任务执行异常，则抛出异常
        System.out.println(CompletableFuture.anyOf(cf,cf2,cf3).get());
	}

}



















































