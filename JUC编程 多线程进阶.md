

# JUC并发编程



## 1. 什么是JUC

JUC 就是 `java.util.cocurrent`，专门用于多线程的开发

![image-20210406095233437](https://gitee.com/StanAugust/picbed/raw/master/img/20210406095240.png)

---



## 2. 基础知识

### 2.1 进程和线程

> 进程是操作系统中的应用程序、是资源分配的基本单位;
>
> 线程是用来执行具体的任务和功能，是CPU调度和分派的最小单位

#### 2.1.1 进程

​	就是一个正在运行中的程序，数据+代码+pcb

​	一个进程至少包含一个线程

​	Java默认有几个线程？**2个：main线程、GC线程**

#### 2.1.2 线程

​	比如开了一个进程Typora，写字，等待几分钟会进行自动保存（由线程负责）

​	对于Java而言：由Thread、Runnable、Callable进行开启线程

​	**Java真的可以开启线程吗？并不！**

​	Java并没有权限去开启线程、操作硬件，它通过一个native方法，底层调用了C++

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210325175138.png"  />



### 2.2 并发和并行

#### 2.2.1 并发

​	多个线程操作同一个资源

​	CPU只有一核，通过快速交替来模拟出多条线程

​	并发编程的本质：充分利用CPU的资源！

#### 2.2.2 并行

​	多个人一起行走

​	CPU多核，多个线程可以同时执行。可以使用线程池



### 2.3 线程的状态

> 操作系统层面定义了5种状态
>
> Java层面是6种状态

``` java
 public enum State {
     // 初始状态,线程被构建，但是还没有调用 start 方法;
     NEW,

     // 运行,JAVA把操作系统中的就绪（ready）和运行（running）两种状态统一称为“运行中”;
     RUNNABLE,

     // 阻塞
     // 1.等待阻塞：执行了Thread.sleep()/wait()/join() 等方法, JVM会把当前线程设置为等待状态，当这些方法执行完毕之后,该线程从等待状态进入到阻塞状态,重新抢占锁后进行线程恢复;
     // 2.同步阻塞：运行的线程在获取对象的同步锁时，若该同步锁被其他线程锁占用了，那么JVM会把当前的线程放入到锁池中;
     // 3.其他阻塞：发出了I/O请求时,JVM会把当前线程设置为阻塞状态,当I/O处理完毕则线程恢复;
     BLOCKED,

     // 等待,永远地等待
     WAITING,

     // 超时等待,超时以后自动返回
     TIMED_WAITING,

     // 终止
     TERMINATED;
 }
```



### 2.4 wait 和 sleep 的区别

+ **来自不同的类**

  wait => Object

  sleep => Thread

  ​	一般情况企业中使用休眠是JUC中的：`TimeUnit.SECONDS.sleep()`

+ **关于锁的释放**

  wait 会释放锁；

  sleep 睡觉了，不会释放锁；

+ **不同的使用范围**

  wait 必须在同步代码块中；

  sleep 可以在任何地方睡；

+ **是否需要捕获异常**

  wait不需要捕获异常；

  sleep必须要捕获异常；

---



## 3. Lock

### 3.1 传统的 synchronize

```java
public class TestSynchronize {    
	public static void main(String[] args) {
		// 多线程操作
        // 并发：多线程操作同一个资源类，把资源类丢入线程
		Ticket ticket = new Ticket();
		
		new Thread(()->{ for(int i=0; i<15; i++) ticket.sale();}, "A") .start();
        new Thread(()->{ for(int i=0; i<15; i++) ticket.sale();}, "B") .start();
        new Thread(()->{ for(int i=0; i<15; i++) ticket.sale();}, "C") .start();
	}
}

// 线程就是一个单独的资源类，没有任何的附属操作
// oop 资源类: 属性+方法
class Ticket{
	private int num = 30;
	
	public synchronized void sale() {
		if(num > 0) {
			System.out.println(Thread.currentThread().getName() + "卖出了第 " + num-- + " 张票");
		}
	}
}
```



### 3.2  juc.locks.Lock

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210326173616.png" style="zoom:80%;" />

可重入锁：可以延续使用的锁（拿到外面的锁，就可以拿到里面的锁）

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210326173917.png" style="zoom:90%;" />

> 公平锁：顾名思义，十分公平，必须先来后到
>
> 非公平锁：不一定公平，允许插队（默认）

```java
public class TestLock {
	
	public static void main(String[] args) {
		Ticket2 ticket = new Ticket2();
		
		new Thread(()->{ for(int i=0; i<15; i++) ticket.sale();}, "A") .start();
        new Thread(()->{ for(int i=0; i<15; i++) ticket.sale();}, "B") .start();
        new Thread(()->{ for(int i=0; i<15; i++) ticket.sale();}, "C") .start();
	}
}

// lock三部曲
// 1、Lock lock=new ReentrantLock();
// 2、lock.lock() 加锁
// 3、finally=> 解锁：lock.unlock();
class Ticket2{
	
	private int num = 30;
	
	private Lock lock = new ReentrantLock();
	
	public void sale() {
		lock.lock();
		try {
			if (num > 0) {
				System.out.println(Thread.currentThread().getName() + "卖出了第 " + num + " 张票, 剩余：" + --num);
			} 
		} finally {			
			lock.unlock();
		}
	}
	
}
```



### 3.3 synchronize 和 Lock 的区别

+ synchronize是内置的Java关键字；Lock是一个java类
+ synchronize无法判断获取锁的状态；Lock是可以的
+ synchronize会自动获得/释放锁；Lock必须手动，**所以可能会遇到死锁！**
+ synchronize 线程1(获得锁->阻塞)、线程2(等待)；Lock就不一定会一直等下去，**它会有一个trylock去尝试获取锁，不会造成长久的等待**
+ synchronize是可重入锁，不可以中断的，非公平的；Lock，可重入的、可以判断锁、可以设置是否公平

---



## 4. 生产者和消费者

### 4.1 synchronize版本

```java
public class SynchronizeVer {
	// 生产一个 消费一个
	public static void main(String[] args) {
		Data data = new Data();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				try {
					data.increment();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"producer").start();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				try {
					data.decrement();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"consumer").start();
		
	}
}
// 资源类
class Data{
	private int num = 0;
	
	public synchronized void increment() throws InterruptedException {
		if(num != 0) {	//等待
			this.wait();
		}
		num++;
		System.out.println(Thread.currentThread().getName() + "=>" + num);
		this.notifyAll();	//通知其他线程
	}
	
	public synchronized void decrement() throws InterruptedException {
		if(num == 0) {	//等待
			this.wait();
		}
		num--;
		System.out.println(Thread.currentThread().getName() + "=>" + num);
		this.notifyAll();	//通知其他线程
	}
}
```



### 4.2 虚假唤醒

> 什么是虚假唤醒？
>
> 当一个条件满足时，很多线程都被唤醒了，但是只有其中部分是有用的唤醒，其它的唤醒都是无用功。
>
> 上述代码，如果一旦有多对线程，就会出现虚假唤醒：当消费者拿完商品之后执行notifyAll()，所有生产者都被唤醒进行生产，商品就超出了容量

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210326182852.png" style="float:left;" />

==解决方法：等待总在循环中==，if改为while即可

![](https://gitee.com/StanAugust/picbed/raw/master/img/20210326184503.png)

```java
public synchronized void increment() throws InterruptedException {
		while(num != 0) {
			this.wait();
		}
		num++;
		System.out.println(Thread.currentThread().getName() + "=>" + num);
		this.notifyAll();
	}
	
	public synchronized void decrement() throws InterruptedException {
		while(num == 0) {
			this.wait();
		}
		num--;
		System.out.println(Thread.currentThread().getName() + "=>" + num);
		this.notifyAll();
	}
```



### 4.3 Lock版本

![](https://gitee.com/StanAugust/picbed/raw/master/img/20210326185252.png)

```java
public class LockVer {	
	public static void main(String[] args) {
		Data2 data = new Data2();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				try {
					data.increment();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"producer").start();
		
		new Thread(()->{
			for (int i = 0; i < 10; i++) {
				try {
					data.decrement();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		},"consumer").start();
	}
}

class Data2{
	private int num = 0;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	
	public void increment() throws InterruptedException {
		lock.lock();				// 加锁
		try {
			while(num != 0) {		// 判断等待
				condition.await();
			}
			num++;
			System.out.println(Thread.currentThread().getName() + "=>" + num);		
			
			condition.signalAll();	// 通知其他线程
		} finally {
			lock.unlock();			// 解锁
		}
	}
	
	public void decrement() throws InterruptedException {
		lock.lock();				
		try {
			while(num == 0) {		
				condition.await();
			}
			num--;
			System.out.println(Thread.currentThread().getName() + "=>" + num);
			
			condition.signalAll();
		} finally {
			lock.unlock();
		}
	}
}
```



### 4.4 Condition的优势

> **可以精准的通知和唤醒线程！**

```java
public class TestCondition {
	
	public static void main(String[] args) {
		Data3 data = new Data3();
		
		new Thread(()->{for (int i = 0; i < 10; i++) data.printA();},"A").start();
		new Thread(()->{for (int i = 0; i < 10; i++) data.printA();},"B").start();
        new Thread(()->{for (int i = 0; i < 10; i++) data.printA();},"C").start();	
	}
}

/**
 * 指定调用顺序
 * A 执行完 调用B
 * B 执行完 调用C
 * C 执行完 调用A
 */
class Data3{
	private Lock lock = new ReentrantLock();
	private Condition condition1 = lock.newCondition();
	private Condition condition2 = lock.newCondition();
	private Condition condition3 = lock.newCondition();
	
	private int num = 1;
	
	public void printA() {
		lock.lock();				// 加锁
		try {
			while(num != 1) {		// 判断等待
				condition1.await();
			}
			System.out.println(Thread.currentThread().getName() + "=>" + num);		
			
			num++;
			condition2.signalAll();	// 唤醒指定的线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();			// 解锁
		}
	}
	
	public void printB() {
		lock.lock();				// 加锁
		try {
			while(num != 2) {		// 判断等待
				condition2.await();
			}
			System.out.println(Thread.currentThread().getName() + "=>" + num);		
			
			num++;
			condition3.signalAll();	// 唤醒指定的线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();			// 解锁
		}
	}
	
	public void printC() {
		lock.lock();				// 加锁
		try {
			while(num != 3) {		// 判断等待
				condition2.await();
			}
			System.out.println(Thread.currentThread().getName() + "=>" + num);		
			
			num = 1;
			condition1.signalAll();	// 唤醒指定的线程
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			lock.unlock();			// 解锁
		}
	}
}
```

---



## 5. 8锁现象

> 如何判断锁的是谁！锁到底锁的是谁？
>
> 如果一个线程已经在执行中，可能执行时间较长，此时有新的线程进来，那么这个新线程是马上执行还是等待执行？

锁会锁住：new的对象、static class

8锁现象，提出关于锁的8个问题来深刻理解锁



**问题1：**两个同步方法，先执行哪个？

```java
public class Test1 {
	public static void main(String[] args) throws InterruptedException {
		Phone phone = new Phone();
		
		new Thread(()->{ phone.send(); }).start();
		TimeUnit.SECONDS.sleep(1);	
		new Thread(()->{ phone.call(); }).start();
	}

}

class Phone{
	public synchronized void send() {
		System.out.println("发短信");
	}
	
	public synchronized void call() {
		System.out.println("打电话");
	}
}
```

**问题2：**如果让其中一个同步方法进行了延迟，先执行哪个？

```java
public class Test1 {
	public static void main(String[] args) throws InterruptedException {
		Phone phone = new Phone();
		
		new Thread(()->{ phone.send(); }).start();
		TimeUnit.SECONDS.sleep(1);	
		new Thread(()->{ phone.call(); }).start();
	}

}

class Phone{
	public synchronized void send() {
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("发短信");
	}
	
	public synchronized void call() {
		System.out.println("打电话");
	}
}
```

结果：都是先输出send()，然后再call()

**原因：**这并不是因为顺序执行，而是因为**被synchronize修饰的方法，锁住的对象是方法的调用者！**这两个方法实际用的是同一个锁，谁先拿到谁先执行，另外一个只能等待



**问题3：**加入一个普通方法

```java
public class Test2 {
	public static void main(String[] args) throws InterruptedException {
		Phone2 phone = new Phone2();
	
		new Thread(()->{ phone.send(); }).start();
		TimeUnit.SECONDS.sleep(1);
		new Thread(()->{ phone.hello(); }).start();
	}

}

class Phone2{
	public synchronized void send() {
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("发短信");
	}
	
	public synchronized void call() {
		System.out.println("打电话");
	}
	
	public void hello() {
		System.out.println("hello");
	}
}
```

结果：1秒后先输出hello()，然后4秒后再输出send()

**原因：**因为hello()是一个普通方法，不受synchronize锁的影响，不用等待锁的释放



**问题4：**增加一个对象，各自调用一个同步方法，执行顺序？

```java
public class Test2 {
	public static void main(String[] args) throws InterruptedException {
		Phone2 phone = new Phone2();
		Phone2 phone2 = new Phone2();
		
		new Thread(()->{ phone.send(); }).start();
		TimeUnit.SECONDS.sleep(1);		
		new Thread(()->{ phone2.call(); }).start();
	}
}
```

结果：1秒后先输出call()，然后4秒后再输出send()

**原因：**因为有了两个对象，是两把不同的锁，所以也没有出现等待的情况



**问题5：**将两个同步方法加上static变成静态方法，执行顺序？

```java
public class Test3 {
	public static void main(String[] args) throws InterruptedException {
		Phone3 phone = new Phone3();
		
		new Thread(()->{ phone.send(); }).start();
		TimeUnit.SECONDS.sleep(1);		
		new Thread(()->{ phone.call(); }).start();
	}

}

class Phone3{
	public static synchronized void send() {
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("发短信");
	}
	
	public static synchronized void call() {
		System.out.println("打电话");
	}
}
```

**问题6：**条件同上，由一个对象变为两个对象

```java
public class Test3 {
	public static void main(String[] args) throws InterruptedException {
		Phone3 phone = new Phone3();
		Phone3 phone2 = new Phone3();

		new Thread(()->{ phone.send(); }).start();
		TimeUnit.SECONDS.sleep(1);		
		new Thread(()->{ phone2.call(); }).start();
	}
}
```

结果：都是先输出send()，然后再call()

**原因：**static静态方法对于整个类Class来说只有一份，尽管有不同的对象，但使用的都是同一份方法。如果static方法使用synchronized锁定，那么这个synchronized会锁住整个对象！不管多少个对象，对于static的锁都只有一把锁，谁先拿到这个锁就先执行，其他的进程都需要等待！



**问题7：**使用一个静态同步方法、一个同步方法、一个对象，执行顺序？

```java
public class Test4 {
	public static void main(String[] args) throws InterruptedException {
		Phone4 phone = new Phone4();
		
		new Thread(()->{ phone.send(); }).start();
		TimeUnit.SECONDS.sleep(1);		
		new Thread(()->{ phone.call(); }).start();
	}
}

class Phone4{
	public static synchronized void send() {
		try {
			TimeUnit.SECONDS.sleep(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("发短信");
	}
	
	public synchronized void call() {
		System.out.println("打电话");
	}
}
```

结果：1秒后先输出call()，然后4秒后再输出send()

**原因：**因为一个锁的是Class类模板，一个锁的是对象的调用者，所以不存在等待



**问题8：**使用一个静态同步方法、一个同步方法、两个对象，执行顺序？

```java
public class Test4 {
	public static void main(String[] args) throws InterruptedException {
		Phone4 phone = new Phone4();
		Phone4 phone2 = new Phone4();
        
		new Thread(()->{ phone.send(); }).start();
		TimeUnit.SECONDS.sleep(1);		
		new Thread(()->{ phone2.call(); }).start();
	}
}
```

结果：同上

**原因：**两把锁锁的不是同一个东西

---



## 6. 集合不安全

> 不安全的集合在并发情况下均会导致 `java.util.ConcurrentModificationException ` 并发修改异常！

### 6.1 List不安全

```java
public static void main(String[] args) {		
	/**
     * 解决方案
     * 1. List<String> list = new Vector<>();
     * 2. List<String> list = Collections.synchronizedList(new ArrayList<>());
     * 3. List<String> list = new CopyOnWriteArrayList<>();
     */
    List<String> list = new CopyOnWriteArrayList<>();

    for (int i = 0; i < 10; i++) {
        new Thread(()->{
            list.add(UUID.randomUUID().toString().substring(0, 5));
            System.out.println(list);
        }, String.valueOf(i)) .start();
    }
}
```

> CopyOnWriteArrayList：写入时复制 COW ，是计算机程序设计领域的一种优化策略

核心思想是，如果有多个调用者同时要求相同的资源（如内存或者是磁盘上的数据存储），他们会共同获取相同的指针指向相同的资源，直到某个调用者试图修改资源内容时，系统才会真正复制一份专用副本（private copy）给该调用者，而其他调用者所见到的最初的资源仍然保持不变。

这过程对其他的调用者都是透明的。此做法主要的优点是如果调用者没有修改资源，就不会有副本（private copy）被创建，因此多个调用者**只是读操作时可以共享同一份资源**。

但如果读的时候有多个线程正在向CopyOnWriteArrayList添加数据，读还是会读到旧的数据，因为写的时候不会锁住旧的CopyOnWriteArrayList。



>CopyOnWriteArrayList相比Vector的优势？

Vector底层读写操作均由synchronize实现，效率相对低下（一般不使用）

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210329184840.png" style="float:left;" />

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210329184900.png" style="float:left;" />

CopyOnWriteArrayList底层由ReentrantLock实现

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210329185110.png" style="float:left;" />



### 6.2 Set不安全

```java
public static void main(String[] args) {
    // Set<String> set = new HashSet<>();
    /**
     * 解决方案
     * 1. Set<String> set = Collections.synchronizedSet(new HashSet<>());
     * 2. Set<String> set = new CopyOnWriteArraySet<>();
     */
    Set<String> set = new CopyOnWriteArraySet<>();

    for (int i = 1; i <= 10; i++) {
        new Thread(() -> {
            set.add(UUID.randomUUID().toString().substring(0,5));
            System.out.println(set);
        },String.valueOf(i)).start();
    }
}
```

> HashSet底层是什么？

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210329185623.png" style="float:left;"/>

底层就是一个HashMap，HashSet就是使用了HashMap key不能重复的原理

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210329185948.png" style="float:left;" />



### 6.3 Map不安全

```java
public static void main(String[] args) {
    // map 是这样用的吗？  不是，工作中不使用这个
    // 默认等价什么？ new HashMap<>(16,0.75);
    // HashMap<<String, String> map = new HashMap<>();
    /**
     * 解决方案
     * 1. Map<String, String> map = Collections.synchronizedMap(new HashMap<>());
     *  Map<String, String> map = new ConcurrentHashMap<>();
     */
    Map<String, String> map = new ConcurrentHashMap<>();
    for (int i = 0; i < 10; i++) {
        new Thread(()->{
            map.put(Thread.currentThread().getName(), UUID.randomUUID().toString().substring(0,5));
            System.out.println(map);
        },String.valueOf(i)).start();
    }
}
```

---



## 7. Callable

**与Runnable的区别：**

1. 可以有返回值
2. 可以抛出异常
3. 执行方法不同 run()/call()



> Thread只实现了Runnable，那么Callable如何放进Thread里呢？

```java
public class TestCallable {	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
//		new Thread(new Runnable()).start();
//		new Thread(new FutureTask<V>()).start();	// 因为 FutureTask implements Runnable，所以这句等价于上句
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
		TimeUnit.SECONDS.sleep(5);
		return 1024;
	}	
}
```

注意点：

1. 代码中新建了两个线程去执行task，但call() 只执行了一次

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210330180950.png" style="float:left;" />

因为一个FutureTask对象只会执行一次，所以task只会在线程A或线程B中run一次，看谁先run

![](https://gitee.com/StanAugust/picbed/raw/master/img/20210330181401.png)

2. 调用get() 可能会被阻塞



---



## 8. 常用的辅助类

### 8.1 CountDownLatch

CountDownLatch可视为一个减法计数器，对于计数器归零之后再进行后面的操作

一个线程（或者多个），等待另外n个线程全部执行完毕后再操作

```java
public class TestCountDownLatch {
	public static void main(String[] args) throws InterruptedException {		
		// 设置线程总数为6
		CountDownLatch latch = new CountDownLatch(6);
		
		for (int i = 0; i < 6; i++) {
			new Thread(()->{				
				System.out.println(Thread.currentThread().getName() + " go out>>>");
				latch.countDown();		// 每启动一个线程，计数-1				
			}, String.valueOf(i)).start();
		}
		
		latch.await();	// 让当前线程等待计数器归零，然后才执行后续操作
		
		System.out.println("close door>>>");
	}
}
```

执行结果：

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210330183114.png" style="float:left;" />

如果没有await() 方法的执行结果：

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210330183046.png" style="float:left;" />



### 8.2 CyclicBarrier

CyclicBarrier可视为一个加法计数器，可以实现n个线程相互等待，全部达到某一条件之后再进行后续操作

CyclicBarrier也可以达到CountDownLatch的效果，而且有可复用的特点

```java
public class TestCyclicBarrier {
	public static void main(String[] args) {
		// 主线程，开启七个线程后再执行此Runnable
		CyclicBarrier barrier = new CyclicBarrier(7, ()->{
			System.out.println("集齐七颗龙珠，召唤神龙>>>");
		});
		
		for (int i = 0; i < 7; i++) {
			final int tmp = i;
			new Thread(()->{
				System.out.println(Thread.currentThread().getName()+" 收集了第"+tmp+ "颗");
				
				try {
					barrier.await();	// 加法计数，等待其它线程执行完毕
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					e.printStackTrace();
				}
				
			}).start();
		}
	}
}
```

执行结果：

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210330184542.png" style="float:left;" />

如果没达到指定条件，会一直阻塞：

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210330184602.png" style="float:left;" />



### 8.3 Semaphore

Semaphore的计数是可加可减的

可用于限定某些资源最多有n个线程可以访问

```java
public class TestSemaphore {
	public static void main(String[] args) {
		// 模拟抢车位
		Semaphore semaphore = new Semaphore(3);
		
		for (int i = 0; i < 6; i++) {
			new Thread(()->{
				try {
					semaphore.acquire();  // 获取，如果资源已经使用完了，就等待资源释放后再进行使用
					System.out.println(Thread.currentThread().getName() + "抢到车位");
					TimeUnit.SECONDS.sleep(2);
					System.out.println(Thread.currentThread().getName() + "离开车位");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {	
					semaphore.release();	// 释放，会将当前的信号量释放+1，然后唤醒等待的线程
				}
			}, String.valueOf(i)).start();
		}
	}
}
```

执行结果：

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210330185215.png" style="float:left;" />

> ==信号量和互斥锁：==
>
> 1. 信号量用在**多线程多任务同步**的，一个线程完成了某一个动作就通过信号量告诉别的线程，别的线程再进行某些动作。
>
>    信号量不一定是锁定某一个资源，而是侧重**流程**上的概念，**用于调度线程，服务于多个线程之间的逻辑执行顺序**
>
> 2. 互斥锁是用在多线程多任务互斥的，一个线程占用了某一个资源，那么别的线程就无法访问，直到这个线程unlock，其他的线程才开始可以利用这个资源。
>
>    线程互斥量则是侧重“锁住某一资源”的概念，服务于共享资源
>
> 3. 互斥锁是信号量的一种特殊情况（n=1时），也就是说，完全可以用后者替代前者。
>
>    **但是，因为mutex较为简单，且效率高，所以在必须保证资源独占的情况下，还是采用这种设计**。

---



## 9. 读写锁

> 多线程的读写会造成数据不可靠的问题，这个问题可以用synchronize这种重量锁和轻量锁lock去解决
>
> 但这次采用更细粒度的锁：`ReadWriteLock`读写锁来保证数据安全

![image-20210331180056899](https://gitee.com/StanAugust/picbed/raw/master/img/20210331180102.png)

```java
public class TestReadWriteLock {	
	public static void main(String[] args) {		
		MyCacheLock cache = new MyCacheLock();
		
		// 开启5个写入线程
		for (int i = 0; i < 5; i++) {			
			final int tmp = i;			
			new Thread(()->{
				cache.put(String.valueOf(tmp), tmp);
			}, String.valueOf(i)).start();
		}
		
		// 开启5个读取线程
		for (int i = 0; i < 5; i++) {			
			final int tmp = i;			
			new Thread(()->{
				cache.get(String.valueOf(tmp));
			}, String.valueOf(i)).start();
		}
	}
}
// 加锁版本
class MyCacheLock {	
	private volatile HashMap<String, Object> map = new HashMap<>();
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
    // 写锁（独占锁）
	public void put(String k, Object v) {
		readWriteLock.writeLock().lock();
		
		try {			
			System.out.println(Thread.currentThread().getName() + " 开始写");
			map.put(k, v);
			System.out.println(Thread.currentThread().getName() + " 写完成");
			
		} finally {
			readWriteLock.writeLock().unlock();
		}
	}
	// 读锁（共享锁）
	public void get(String k) {
		readWriteLock.readLock().lock();
		try {
			System.out.println(Thread.currentThread().getName() + " 开始读");
			map.get(k);
			System.out.println(Thread.currentThread().getName() + " 读完成");
			
		} finally {
			readWriteLock.readLock().unlock();
		}
	}	
}
```

如果不加锁，无法保证数据安全

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210331174754.png" alt="image-20210331174750331" style="float:left" />

加上读写锁之后，运行结果：

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210331175516.png" alt="image-20210331175457459" style="float:left" />



---



## 10. 阻塞队列

### 10.1 BlockingQueue

> 添加了等待（阻塞）功能的队列，主要用于多生产者-多消费者队列

![image-20210401182826932](https://gitee.com/StanAugust/picbed/raw/master/img/20210401182827.png)

![image-20210401183230654](https://gitee.com/StanAugust/picbed/raw/master/img/20210401183230.png)

![image-20210402153005952](https://gitee.com/StanAugust/picbed/raw/master/img/20210402153006.png)





**四组重要api：**以不同的方式处理某种不能立即满足的操作

|              | 抛出异常 | 不会抛出异常，有返回值 | 阻塞，等待 |           超时等待           |
| :----------: | :------: | :--------------------: | :--------: | :--------------------------: |
|     添加     |  add(e)  |        offer(e)        |   put(e)   | offer(e , timeout, timeunit) |
|     移除     |  remove  |          poll          |    take    |   poll(timeout, timeunit)    |
| 判断队首元素 |  elemen  |          peek          |     /      |              /               |

```java
public class TestBlockingQueue {
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
			// 如果队列已经满了,再进去一个元素,会一直等待这个队列 什么时候有了位置再进去，程序不会停止
//			blockingQueue.put(4);
			
			System.out.println(blockingQueue.take());
			System.out.println(blockingQueue.take());
			System.out.println(blockingQueue.take());
			// 如果再拿一个,这种情况也会等待，程序会一直运行 阻塞
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
```



### 10.2 SynchronousQueue

> 一种特殊的无容量的BlockingQueue，必须由多个线程配合着顺序存取

![image-20210401171621057](https://gitee.com/StanAugust/picbed/raw/master/img/20210401171621.png)

```java
public class TestSynchronousQueue {
	public static void main(String[] args) {
		SynchronousQueue<Integer> synchronousQueue = new SynchronousQueue<>();
		// 一个线程专门放
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
		
        // 另外的线程专门取
		new Thread(()->{
			try {
				while(true) {
                    // 随机休眠几秒之后再去取
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
```

执行结果：

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210401175416.png" alt="image-20210401175416686" style="float:left" />

---



## 11. 线程池

>池化技术：事先准备好一些资源放在一个特定的“池子”中，以备不时之需以及重复使用

![image-20210402160847596](https://gitee.com/StanAugust/picbed/raw/master/img/20210402160847.png)

> **线程池的好处：**
>
> 1. 由于减少了每个任务调用的开销，线程池通常在执行大量异步任务时提供更好的性能; 
> 2. 方便管理



### 11.1 3大方法

```java
//工具类 Executors 三大方法；
ExecutorService threadPool = Executors.newSingleThreadExecutor();		// 单个线程的线程池
ExecutorService threadPool2 = Executors.newFixedThreadPool(5); 			// 固定大小
ExecutorService threadPool3 = Executors.newCachedThreadPool(); 			// 可伸缩的
```



### 11.2 7大参数

![image-20210402163946783](https://gitee.com/StanAugust/picbed/raw/master/img/20210402163946.png)	

分析源码，本质：3种方法都是通过 `ThreadPoolExecutor` 来开启线程池



> 那么`ThreadPoolExecutor`如何创建线程池？

```java
public ThreadPoolExecutor(int corePoolSize,						// 核心线程池大小
                          int maximumPoolSize,					// 最大的线程池大小
                          long keepAliveTime,					// 超时了没有调用就会释放
                          TimeUnit unit,						// 超时单位
                          BlockingQueue<Runnable> workQueue,	// 阻塞队列
                          ThreadFactory threadFactory,			// 创建线程的线程工厂（一般不动）
                          RejectedExecutionHandler handler		// 拒绝策略
                         ) {
    if (corePoolSize < 0 ||
        maximumPoolSize <= 0 ||
        maximumPoolSize < corePoolSize ||
        keepAliveTime < 0)
        throw new IllegalArgumentException();
    if (workQueue == null || threadFactory == null || handler == null)
        throw new NullPointerException();
    this.acc = System.getSecurityManager() == null ?
        null :
    AccessController.getContext();
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    this.workQueue = workQueue;
    this.keepAliveTime = unit.toNanos(keepAliveTime);
    this.threadFactory = threadFactory;
    this.handler = handler;
}
```



> 阿里java开发手册中提到：

![image-20210402164622509](https://gitee.com/StanAugust/picbed/raw/master/img/20210402164622.png)	



### 11.3 4种拒绝策略

![image-20210402171123861](https://gitee.com/StanAugust/picbed/raw/master/img/20210402171123.png)	

---



## 12. 函数式接口

> 函数式接口就是只有一个抽象方法的接口

![image-20210402195619214](https://gitee.com/StanAugust/picbed/raw/master/img/20210402195619.png)	

### 12.1 Function函数型接口

![image-20210402200146450](https://gitee.com/StanAugust/picbed/raw/master/img/20210402200146.png)	

```java
public class TestFunction {
	public static void main(String[] args) {
//		Function<String, Integer> function = new Function<String, Integer>(){
//
//			@Override
//			public Integer apply(String t) {
//				return Integer.valueOf(t);
//			}
//			
//		};
        // 只要是函数式接口，就可以用lamba表达式简化!
		Function<String, Integer> function = (str) -> { return Integer.valueOf(str); };
		System.out.println(function.apply("123"));
	}
}
```



### 12.2 Predicate判定型接口

![image-20210402201155949](https://gitee.com/StanAugust/picbed/raw/master/img/20210402201156.png)	

```java
public class TestPredicate {
	public static void main(String[] args) {
		Predicate<Integer> predicate = (i)->{ return i>0; };
		System.out.println(predicate.test(5));
        System.out.println(predicate.test(-1));
	}
}
```



### 12.3 Supplier供给型接口

![image-20210402201658993](https://gitee.com/StanAugust/picbed/raw/master/img/20210402201659.png)	

```java
public class TestSupplier {
	public static void main(String[] args) {
		Supplier<Integer> supplier = ()->{ return new Random().nextInt(5); };
		System.out.println(supplier.get());
	}
}
```



### 12.4 Consumer消费型接口

![image-20210402202141794](https://gitee.com/StanAugust/picbed/raw/master/img/20210402202141.png)	

```java
public class TestConsumer {
	public static void main(String[] args) {
		Consumer<String> consumer = (str)->{ System.out.println("input: " + str); };
		consumer.accept("hello");
	}
}
```



---



## 13. Stream流式处理

> 什么叫流式处理？
>
> 流(Stream) ：一个元素序列，支持聚合操作（类似SQL语句一样的操作，比如filter、find、sort等）。
>
> 流式处理就是将要处理的元素集合看做一种流，流在管道中进行传输，并且可以在管道的中间节点上进行处理，最后由最终操作(terminal operation)得到前面处理的结果

```java
public class TestStream {
	public static void main(String[] args) {
		/**
		 * 题目要求： 用一行代码实现
		 * 1. Id 必须是偶数
		 * 2. 年龄必须大于23
		 * 3. 用户名转为大写
		 * 4. 用户名倒序
		 * 5. 只能输出一个用户
		 */
		User u1 = new User(1, "a", 23);
        User u2 = new User(2, "b", 23);
        User u3 = new User(3, "c", 23);
        User u4 = new User(6, "d", 24);
        User u5 = new User(4, "e", 25);
        // 存储 交给集合
        List<User> list = Arrays.asList(u1,u2,u3,u4,u5);
        
        // 计算 交给流
        // 均为函数式接口
        list.stream().filter( (user)->{ return user.getId()%2 == 0; } )
        			 .filter( (user)->{ return user.getAge() > 23; } )
        			 .map( (user)->{ return user.getName().toUpperCase(); } )
        			 .sorted( (user1, user2)->{ return user2.compareTo(user1); })
        			 .limit(1)
        			 .forEach((user)->{System.out.println(user);});
	}
}
```

---



## 14. Fork/Join

> 什么是fork/join？
>
> fork/join是一种“分而治之”的思想，通过把一个**大任务**递归分解成多个小任务，**并行**执行，最后合并多个结果得到最终结果

![image-20210406155159452](https://gitee.com/StanAugust/picbed/raw/master/img/20210406155159.png)

> jdk1.7开始，提供了fork/join框架用于执行并行任务，采用的就是fork/join的思想

![image-20210406150635379](https://gitee.com/StanAugust/picbed/raw/master/img/20210406154123.png)

### 14.1 ForkJoinTask

要使用fork/join框架，就需要创建一个 `ForkJoinTask` ，该类提供了在任务中执行 `fork` 和  `join` 的机制

![image-20210406160345561](https://gitee.com/StanAugust/picbed/raw/master/img/20210406160345.png)![image-20210406160914364](https://gitee.com/StanAugust/picbed/raw/master/img/20210406160914.png)

通常情况下，实现任务时不需要直接继承ForkJoinTask类，而是继承它的子类：

![image-20210406161748139](https://gitee.com/StanAugust/picbed/raw/master/img/20210406161748.png)	



### 14.2 ForkJoinPool

`ForkJoinTask`需要通过`ForkJoinPool`来执行

`ForkJoinPool`不同于其它`ExecutorService`的地方就在于 “**工作窃取**” 算法：

​	任务分割出的子任务会添加到不同的工作线程所维护的**双端队列**中，进入队列的头部。当一个工作线程的队列里暂时没有任务时，它会随机从其他工作线程的队列的尾部获取一个任务

![image-20210406114340369](https://gitee.com/StanAugust/picbed/raw/master/img/20210406114340.png)



### 14.3 ForkJoinWorkerThread

`ForkJoinPool` 由 `ForkJoinTask` 数组和 `ForkJoinWorkerThread` 数组组成，`ForkJoinTask` 数组负责存放程序提交给 `ForkJoinPool` 的任务，而`ForkJoinWorkerThread` 数组负责执行这些任务。

![image-20210406163939419](https://gitee.com/StanAugust/picbed/raw/master/img/20210406163939.png)



### 14.4 ForkJoin的使用

1. 通过`ForkJoinPool`来执行
2. 计算类要继承 `ForkJoinTask`
3. 计算任务` execute(ForkJoinTask<?> task) / submit`



> ForkJoin的计算类

```java
public class MyForkJoinTask extends RecursiveTask<Long>{
	
	private long begin;
	private long end;
	// 临界值
	private long threshold = 1000000L;
	
	public MyForkJoinTask(long begin, long end) {
		this.begin = begin;
		this.end = end;
	}
    
	/*
	 * ForkJoinTask与一般任务的主要区别在于它需要实现compute方法，在这个方法里:
	 * 首先需要判断任务是否足够小，如果足够小就直接执行任务;如果不足够小，就必须分割成两个子任务
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
```

> 测试类

```java
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
		
        // 提交计算任务
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
		
		long sum = LongStream.range(0L, SUM).parallel().reduce(0, Long::sum);
		
		System.out.println(sum);
		
		long end = System.currentTimeMillis();
		System.out.println("耗费时间：" + (end-start));
		System.out.println("=======================");
	}
}
```

> 执行结果

![image-20210406172411107](https://gitee.com/StanAugust/picbed/raw/master/img/20210406172411.png)	

---



## 15. 异步编程

### 15.1 Future

jdk1.5 引入了`Future`模式，`Future`代表的是异步执行的结果，即当异步执行结束以后，返回的结果会保存在`Future`中

![image-20210406180600244](https://gitee.com/StanAugust/picbed/raw/master/img/20210406180600.png)

`Future`接口只提供了以下5个方法：

```java
public interface Future<V> {
 	// 取消任务的执行,参数指定是否立即中断任务执行
    boolean cancel(boolean mayInterruptIfRunning);
 	// 检查任务是否被取消
    boolean isCancelled(); 	
    // 检查任务是否完成
    boolean isDone();	
    // 等待任务执行结束，然后获得V类型的结果。
    V get() throws InterruptedException, ExecutionException; 	
    // 同上面的get功能一样，多了设置超时时间。
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

一般情况下，结合Callable和Future一起使用，通过它们执行异步任务可以获取执行结果：

```java
public class TestFuture {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		Future<String> future = threadPool.submit(()->{
			try {
				TimeUnit.SECONDS.sleep(3);
                // 如果子线程执行异常时，其异常会被捕获，get方法就会中止等待并抛出异常，并不会无限制等待
//				if(true) {
//					throw new RuntimeException("test");
//				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return "future result";
		});
		System.out.println(future.get());	// 3秒后再输出结果		
        
		threadPool.shutdown();
	}
}
```



### 15.2 CompletableFuture

> Future模式存在着一些缺点：
>
> 1. Future虽然可以实现获取异步执行结果的需求，但是它没有提供Callback的机制，我们无法得知Future什么时候完成
>
>    要么使用阻塞，在future.get()的地方等待future返回的结果，这时又变成同步操作
>
>    要么使用isDone() 轮询判断Future是否完成，这样会耗费CPU的资源
>
> 2. 很难直接表述多个Future结果之间的依赖性



所以在jdk1.8中引入了 `CompletableFuture` 类，增加了异步回调、流式处理、多个`Future`组合处理的能力，使Java在处理多任务的协同工作时更加顺畅便利

![image-20210407101106332](https://gitee.com/StanAugust/picbed/raw/master/img/20210407101113.png)

#### 15.2.1 创建异步任务

> supplyAsync /  runAsync

![image-20210407104917778](https://gitee.com/StanAugust/picbed/raw/master/img/20210407104917.png)	

可以指定`Executor`来执行异步任务，如果不指定，则默认使用`ForkJoinPool.commonPool()`/`ThreadPerTaskExecutor()`。以下所有方法都类同

![image-20210407105441950](https://gitee.com/StanAugust/picbed/raw/master/img/20210407105442.png)	

#### 15.2.2 异步回调(串行执行)

> thenApply/ thenAccept/ thenRun

![image-20210407132449643](https://gitee.com/StanAugust/picbed/raw/master/img/20210407132449.png)

不带`Async`的方法是由触发该任务的线程继续执行该任务，

带`Async`的方法是由触发该任务的线程将任务提交到线程池，执行任务的线程跟触发任务的线程不一定是同一个。

以下所有方法都类同

> exceptionally

![image-20210407152732095](https://gitee.com/StanAugust/picbed/raw/master/img/20210407152732.png)	



> whenComplete/ handle

![image-20210407155956430](https://gitee.com/StanAugust/picbed/raw/master/img/20210407155956.png)	

![image-20210407155636409](https://gitee.com/StanAugust/picbed/raw/master/img/20210407155636.png)	

#### 15.2.3 组合处理(并行执行)

> thenCombine / thenAcceptBoth / runAfterBoth

结合两个CompletableFuture，只有两个都正常执行结束后才会执行某个任务  

两个任务中只要有一个执行异常，则将该异常信息作为指定任务的执行结果

```java
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
```

> applyToEither / acceptEither / runAfterEither

结合两个CompletableFuture，只要其中一个执行完了就会执行某个任务

```java
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
            TimeUnit.SECONDS.sleep(2);
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
```

> allOf/ anyOf

结合多个CompletableFuture

```java
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
```

---



## 16. JMM

### 16.1 物理内存模型

![image-20210408103706837](https://gitee.com/StanAugust/picbed/raw/master/img/20210408103706.png)

物理内存的第一个问题是：硬件处理效率

> CPU和缓存一致性

由于向内存读写数据的过程跟CPU执行指令的速度比起来要慢的多，因此在 CPU 和内存之间增加了高速缓存Cache。

**缓存一致性问题: **

​	但在多处理器系统中，每个CPU都有自己的Cache，却只共享同一主内存。当多个CPU的运算任务都涉及同一块主内存区域时，将可能导致各自的缓存数据不一致。

缓存一致性协议：

​	为了解决缓存一致性问题，需要各个处理器访问缓存时都遵循一些协议，在读写时要根据协议来进行操作。

​	比如Intel 的MESI协议，其核心思想：当CPU写数据时，如果发现操作的变量是共享变量，即在其他CPU中也存在该变量的副本，会发出信号通知其他CPU将该变量的缓存行置为无效状态，因此当其他CPU需要读取这个变量时，发现自己缓存中缓存该变量的缓存行是无效的，那么它就会从内存重新读取。

> 处理器优化和指令重排

除了Cache以外，为了使得CPU内部的运算单元尽量被充分利用，CPU可能会对输入代码进行“乱序执行”优化。

同时很多编程语言的编译器也会有类似的优化，比如 Java 虚拟机的即时编译器（JIT）也会做指令重排。

处理器优化和指令重排，在单核系统下不会出现问题，但在多核环境下，如果不同核的计算任务之间存在数据依赖，而且对相关数据读写没做任何防护措施，那么会导致各种问题。



### 16.2 并发编程的3个概念

并发编程有3个人们抽象定义出的问题，这个抽象的底层问题就是前面提到的缓存一致性问题、处理器优化问题和指令重排问题。

1. 可见性：当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够**立即**看得到修改的值
2. 原子性：在一个操作中，CPU 不可以在中途暂停然后再调度，即**不被中断**操作，要不执行完成，要不就不执行
3. 有序性：程序执行的顺序按照代码的先后顺序执行

要想并发程序正确地执行，必须要保证这所有3个特性。



### 16.3 Java内存模型

> 内存模型：为了保证共享内存的正确性（可见性、有序性、原子性），而定义的共享内存系统中多线程程序读写操作行为的规范
>
> 是一个概念，也是一个约定！



Java 内存模型（Java Memory Model，JMM）就是一种**屏蔽了各种硬件和操作系统的访问差异**的，保证了 Java 程序在**各种平台下对内存的访问都能保证效果一致**的机制及规范。



#### 16.3.1 JMM内存划分

JMM中分为 **主内存、工作内存**

​	所有的变量都存储在主内存中；

​	工作内存中保留了该线程使用到的变量的主内存的副本，线程对变量的所有操作都必须在工作内存中进行，不能直接读写主内存中的变量；

​	不同的线程间也无法直接访问对方工作内存中的变量（ThreadLocal），线程间值交互均通过主内存完成；

#### 16.3.2 JMM内存操作的问题

为了较好的执行性能，类似于物理内存模型面临的问题，JMM 也存在以下两个问题：

1.  工作内存数据一致性 
2. 指令重排序优化

#### 16.3.4 内存交互操作

JMM 定义了 8 个操作来完成主内存和工作内存之间的交互操作。JVM 实现时必须保证下面介绍的每种操作都是 **原子的**：

+ read（读取）：作用于主内存变量，它把一个变量的值从主内存传输到线程的工作内存中，以便随后的load动作使用；
+ load（载入）：作用于工作内存的变量，它把read操作从主存中变量放入工作内存中；
+ use（使用）：作用于工作内存中的变量，它把工作内存中的变量传输给执行引擎，每当虚拟机遇到一个需要使用到变量的值，就会使用到这个指令；
+ assign（赋值）：作用于工作内存中的变量，它把一个从执行引擎中接受到的值放入工作内存的变量副本中；
+ store（存储）：作用于工作内存中的变量，它把一个从工作内存中一个变量的值传送到主内存中，以便后续的write使用；
+ write（写入）：作用于主内存中的变量，它把store操作从工作内存中得到的变量的值放入主内存的变量中；
+ lock（锁定）：作用于主内存的变量，把一个变量标识为线程独占状态；
+ unlock（解锁）：作用于主内存的变量，它把一个处于锁定状态的变量释放出来，释放后的变量才可以被其他线程锁定；

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210407181334.png" alt="image-20210407181334648" style="zoom:80%;" />



**JMM对这8种操作给了相应的规定**：

1. read和load、store和write 必须成对出现

2. 不允许线程丢弃最近的assign操作，即变量在工作内存中改变了之后必须把变化同步到主内存中

3. 不允许一个线程无原因地（无assign操作）把数据从工作内存同步到主内存中。

4. 新变量只能在主内存诞生

5. 一个变量同一时刻只能被一条线程独占lock，但lock操作可以被同一条线程重复执行多次，多次lock要进行相同次数的unlock才能解锁

6. 如果一个变量事先没有被lock，就不允许对它进行unlock操作，也不允许去unlock一个被其他线程锁住的变量。

7. lock操作会去清空工作内存中此变量的值，在执行引擎使用该变量前，需要重新执行load或assign操作以初始化变量的值，

   即线程加锁前，**必须读取**主存中的**最新值**到工作内存中

8. 一个变量unlock之前必须把此变量同步回主内存中，也就是执行store、write，

   即线程解锁前，必须把共享变量**立刻**刷回主存

#### 16.3.5 JMM实现

前面提到，并发编程必须全部解决3个问题，在JMM中如何实现：

1. 原子性

   JMM只保证了**基本数据类型**的变量的**读取和赋值**是原子性操作；

   更大范围操作的原子性通过`synchronized`和`Lock`来实现（保证任一时刻只有一个线程执行该代码块）。

2. 可见性：

   JMM 是通过 **"变量修改后将新值同步回主内存**， **变量读取前从主内存刷新变量值"** 这种方式来实现的。

   实现方式：

   + `volatile`
   + `synchronized`和`Lock`
   + `final`（一旦初始化完成，final 变量的值立刻回写到主内存且不可再变，那么其他线程无须同步就能正确看见 final 字段的值）

3. 有序性：

   线程内：遵守 `as-if-serial` 属性，给程序一个顺序执行的假象，经过重排的执行结果要与顺序执行的结果保持一致

   线程间：对于同步方法、同步块以及`volatile`字段的操作仍维持相对有序

   ​	实现方式有所区别：

   +  `volatile` 关键字会禁止指令重排序

   - `synchronized` 关键字通过互斥保证同一时刻只允许一条线程操作。



可以发现`synchronized` 关键字可以同时满足以上三种特性，但是它比较影响性能，虽然编译器提供了很多锁优化技术，也还是不建议过度使用

---



## 17. Volatile

`volatile` 是 JVM 提供的 **最轻量级的同步机制**

它的特性：

1. 保证可见性
2. 不保证原子性
3. 禁止指令重排



### 17.1 保证可见性

> 普通共享变量不保证可见性

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210407182206.png" alt="image-20210407182206145" style="zoom:80%;" />

![image-20210407184129283](https://gitee.com/StanAugust/picbed/raw/master/img/20210407184129.png)

> 使用Volatile修饰

![image-20210408133259586](https://gitee.com/StanAugust/picbed/raw/master/img/20210408133259.png)

使用 `volatile` 关键字之后：

1. 会强制将线程B修改的值**立即**写入主存；
2. 当线程B进行修改时，会其他线程的工作内存中缓存变量flag的缓存行无效（反映到硬件层的话，就是CPU的L1或者L2缓存中对应的缓存行无效）；
3. 由于线程A的工作内存中缓存变量flag的缓存行无效，所以线程A再次读取变量flag的值时会去主存读取最新值。



### 17.2 不保证原子性

<img src="https://gitee.com/StanAugust/picbed/raw/master/img/20210408141330.png" alt="image-20210408141330232" style="zoom:150%;" />



如果不加 `synchronized` 或 `Lock`，怎样保证原子性？

答：使用原子类修饰

![image-20210408151403954](https://gitee.com/StanAugust/picbed/raw/master/img/20210408151404.png)

![image-20210408153405334](https://gitee.com/StanAugust/picbed/raw/master/img/20210408153405.png)



### 17.3 禁止指令重排

`volatile`关键字禁止指令重排序有两层意思：

1. 程序执行到volatile变量的读/写操作时，在其前面操作的更改肯定已经全部进行，且结果已经对后面的操作可见；在其后面的操作肯定还没有进行；
2. 在进行指令优化时，不能将 volatile 变量之前的语句放在对 volatile 变量的读写操作之后，也不能把 volatile 变量后面的语句放到其前面执行

```java
int x = 2;        //语句1
int y = 0;        //语句2
volatile boolean flag = true;  //语句3
int x = 4;         //语句4
int y = -1;       //语句5
```

由于 flag 变量为 volatile，那么在进行指令重排序的过程的时候，不会将语句3放到1、2前面，也不会将语句3放到4、5后面。

但是语句1和2的顺序、语句4和5的顺序是不作任何保证的。

并且能保证执行到语句3时，语句1和2必定是执行完毕了的，且1和2的执行结果对语句3、4、5是可见的。



### 17.4 volatile的原理和实现机制

加入volatile关键字后，会在编译期生成字节码时，多出一个lock前缀指令，这相当于一个**内存屏障**

JMM把内存屏障分为4类（Load表示读，Store表示写）：

![image-20210408171357114](https://gitee.com/StanAugust/picbed/raw/master/img/20210408171357.png)



> 面试官：那么你知道在哪里用这个内存屏障用得最多呢？**单例模式**

---



## 18. 玩转单例模式

### 18.1 饿汉式

```java
public class Hungry {	
	// 优点：没有加锁，执行效率会提高
	// 缺点：类加载时就初始化，浪费内存
	private Hungry() {		
	}

	private final static Hungry HUNGRY = new Hungry();
	
	public static Hungry getInstance() {
		return HUNGRY;
	}
}
```



### 18.2 懒汉式

#### 18.2.1 普通懒汉式（不建议）

```java
public class Lazy {	
	// 优点：第一次调用才初始化，避免内存浪费(lazy initialize)
	// 缺点：必须加锁 synchronized 才能保证单例，但加锁会影响效率。
	private Lazy() {		
	}
	
	private static Lazy instance;
	
	public static synchronized Lazy getInstance() {
		if(instance == null) {
			instance = new Lazy();
		}
		return instance;
	}
}
```

#### 18.2.2 DCL懒汉式

```java
public class DCLLazy {	
	// 这种方式采用双锁机制，安全且在多线程情况下能保持高性能
	private DCLLazy() {		
	}
	
	private volatile static DCLLazy instance;	// 防止指令重排
	
	public static DCLLazy getInstance() {
		if(instance == null) {
			synchronized (DCLLazy.class) {
				if(instance == null) {
					instance = new DCLLazy();	// 不是一个原子性操作
					/*
					 * 实例化的底层操作：
					 * 
                     * 1. 分配内存空间
                     * 2. 执行构造方法，初始化对象
                     * 3. 把这个对象指向这个空间
                     *
                     * 这有可能出现指令重排问题: 
                     * 比如线程A执行的顺序是1 3 2，在执行到3时，来了线程B，因为已经执行了3，
                     * 所以B判断instance！=null，直接return，那么实际上返回的就是一个空的instance（还未初始化）
                     * 
                     * 我们就可以添加volatile保证指令重排问题
					 */
				}
			}
		}
		return instance;
	}
}
```

#### 18.2.3 静态内部类

```java
public class OuterHolder {
	/*
	 * 和饿汉式的区别：
	 * 饿汉式只要该单例类被装载，那么instance就会被实例化
	 * 而这种模式是单例类被装载，但instance只有在显示调用getInstance()时才会被实例化(lazy loading)
	 */
	private OuterHolder() {		
	}
	
	public static OuterHolder getInstance() {
		return InnerClass.INSTANCE;
	}
	
	private static class InnerClass{
		private static final OuterHolder INSTANCE = new OuterHolder();
	}
}
```



==**反射可以破坏单例模式，以上代码并不安全！**==

以DCL为例：

>  反射可以强行使用私有构造器，轻松多次实例化

![image-20210408194545863](https://gitee.com/StanAugust/picbed/raw/master/img/20210408194545.png)	

> 可在构造器中可添加第三重判断

![image-20210408195218987](https://gitee.com/StanAugust/picbed/raw/master/img/20210408195219.png)	

> 但如果实例均为反射创建，那么第三重锁失效

![image-20210408195455419](https://gitee.com/StanAugust/picbed/raw/master/img/20210408195455.png)	

因为`newInstance()`只是返回新建实例，并不会赋值给单例类中的`instance`，这样第三重锁中一直判断的instance为空

> 所以优化第三重锁，使用额外标志位

![image-20210729174748608](https://gitee.com/StanAugust/picbed/raw/master/img/image-20210729174748608.png)	

![image-20210729175056696](https://gitee.com/StanAugust/picbed/raw/master/img/image-20210729175056696.png)

> 但是额外的标志位也不保证绝对的安全

![image-20210408201216341](https://gitee.com/StanAugust/picbed/raw/master/img/20210408201216.png)	



#### 18.2.4 枚举

查看`newInstance()`源码发现：

![image-20210408202059336](https://gitee.com/StanAugust/picbed/raw/master/img/20210408202059.png)	

```java
// 枚举方式的单例模式
public enum EnumSingle {
	INSTANCE;
	public EnumSingle getInstance() {
		return INSTANCE;
	}
}
```

> 再次试图使用反射来破坏单例

![image-20210408204740293](https://gitee.com/StanAugust/picbed/raw/master/img/20210408204740.png)	

结论：**枚举是实现单例模式的最佳方法**。它更简洁，自动支持序列化机制，绝对防止多次实例化

---



## 19. 深入理解CAS

### 19.1 悲观锁与乐观锁

+ 悲观锁(Pessimistic Lock)：每次去拿数据的时候都认为会有并发冲突，所以每次拿数据都会上锁，这样别人想拿这个数据就会block直到它拿到锁。

+ 乐观锁(Optimistic Lock)：每次不加锁而是假设没有冲突而去完成某项操作，在更新值时再检查是否有冲突，如果因为冲突失败就重试，直到成功为止。

所以悲观锁适合写操作非常多的场景，乐观锁适合读操作非常多的场景



在JDK 5之前Java是靠`synchronized`关键字保证同步的，这会导致有锁：

1. 在多线程竞争下，加锁、释放锁会导致比较多的上下文切换和调度延时，引起性能问题;
2. 一个线程持有锁会导致其它所有需要此锁的线程挂起;
3. 如果一个优先级高的线程等待一个优先级低的线程释放锁会导致优先级倒置，引起性能风险。

`synchronized`是一种独占锁，独占锁是一种悲观锁

而另一个更加有效的方式就是乐观锁，这实际上是无锁编程，用到的机制就是CAS



### 19.2 什么是CAS

CAS, Compare And Swap，比较并交换，其核心思想

```java
执行函数：CAS(V,E,N)
 
    包含3个参数:
    V表示需要读写的内存位置
    E表示进行比较的预期原值
    N表示打算写入的新值
```

只有内存位置`V`的值与预期原值`E`相匹配，处理器才会将该位置值更新为新值` N`；否则会不断重试更新（自旋）

CAS是一种系统原语，是由若干条指令组成的，用于完成某个功能的一个过程，并且原语的**执行必须是连续的**，在执行过程中不允许被中断。也就是说CAS是一条CPU的原子指令，不会造成所谓的数据不一致问题。



整个juc都是建立在CAS之上的，因此相对于synchronized阻塞算法，juc在性能上有了很大的提升

Java中CAS操作的执行依赖于Unsafe类的方法



### 19.3 Unsafe

Unsafe类存在于 `sun.misc` 包中，其中所有方法都是**native修饰**的，方法内部操作可以像C的指针一样直接操作内存,也就是说Unsafe类中的方法都直接调用操作系统底层资源执行相应任务

Unsafe类的主要功能：

> 直接操作内存

```java
// 分配内存指定大小的内存
public native long allocateMemory(long bytes);
 
// 根据给定的内存地址address设置重新分配指定大小的内存
public native long reallocateMemory(long address, long bytes); 
 
// 用于释放allocateMemory和reallocateMemory申请的内存 
public native void freeMemory(long address); 
 
// 将指定对象的给定offset偏移量内存块中的所有字节设置为固定值
public native void setMemory(Object o, long offset, long bytes, byte value);//设置给定内存地址的值public native void putAddress(long address, long x);

// 获取指定内存地址的值
public native long getAddress(long address);
 
// 设置或获取指定内存的byte值 
// 其他基本数据类型(long,char,float,double,short等)的操作与putByte及getByte相同
public native byte getByte(long address); 
public native void putByte(long address, byte x);
  
//操作系统的内存页大小 
public native int pageSize();
```

> 操作对象

```java
// 提供实例对象新途径：
// 传入一个对象的class并创建该实例对象，但不会调用构造方法
public native Object allocateInstance(Class cls) throws InstantiationException;
 
 
// 类和实例对象以及变量的操作，就不贴出来了： 
// 传入Field f，获取字段f在实例对象中的偏移量
// 获得给定对象偏移量上的int值，所谓的偏移量可以简单理解为指针指向该变量的内存地址，
// 通过偏移量便可得到该对象的变量
// 通过偏移量可以设置给定对象上偏移量的int值
// 获得给定对象偏移量上的引用类型的值
//通过偏移量可以设置给定对象偏移量上的引用类型的值
 
// 虽然在Unsafe类中存在getUnsafe()方法，但该方法只提供给高级的Bootstrap类加载器使用，普通用户调用将抛出异常，可以用反射获取“theUnsafe”变量
```

> CAS操作相关

```java
// 第一个参数o为给定对象，offset为对象内存的偏移量，通过这个偏移量迅速定位字段并设置或获取该字段的值，
// expected表示期望值，x表示要设置的值，下面3个方法都通过CAS原子指令执行操作。
public final native boolean compareAndSwapObject(Object o, long offset,Object expected, Object x);                               
public final native boolean compareAndSwapInt(Object o, long offset,int expected,int x);
public final native boolean compareAndSwapLong(Object o, long offset,long expected,long x);
```

> 线程挂起与恢复

```java
// 线程调用该方法，线程将一直阻塞直到超时，或者是中断条件出现。  
public native void park(boolean isAbsolute, long time);  
 
// 终止挂起的线程，恢复正常.java.util.concurrent包中挂起操作都是在LockSupport类实现的，其底层正是使用这两个方法，  
public native void unpark(Object thread);
```



### 19.4 基于CAS的原子类

前面提到如果不加 `synchronized` 或 `Lock`，还可以使用原子类来保证原子性

以 `AtomicInteger` 类为例进行分析：

> demo

![image-20210409172911410](https://gitee.com/StanAugust/picbed/raw/master/img/20210409172911.png)	

> 原子类中重要的成员变量：
>
> 1. 共享变量value
> 2. unsafe实例用于调用CAS操作
> 3. valueOffset用于获取共享变量的内存地址

![image-20210409165742173](https://gitee.com/StanAugust/picbed/raw/master/img/20210409165749.png)	

> 提供原子方法来调用CAS操作

![image-20210409173314124](https://gitee.com/StanAugust/picbed/raw/master/img/20210409173314.png)	

![image-20210409172132602](https://gitee.com/StanAugust/picbed/raw/master/img/20210409172132.png)	



### 19.5 CAS存在的问题

#### 19.5.1 自旋开销大

自旋CAS如果长时间不成功，会给CPU带来非常大的执行开销。如果JVM能支持处理器提供的pause指令那么效率会有一定的提升

#### 19.5.2 只能保证一个共享变量的原子操作

对多个共享变量操作时，循环CAS就无法保证操作的原子性。

这个时候就需要用到锁，或者使用`AtomicReference`类来保证引用**对象**之间的原子性（可以把            在一个对象里来进行CAS操作）

![image-20210409182823587](https://gitee.com/StanAugust/picbed/raw/master/img/20210409182823.png)	

#### 19.5.3 ABA问题

如果两个线程都要修改值A，一个线程较快，将值改成B后又改回成A，然后较慢的线程使用CAS进行检查时会发现A的值没有发生变化，但是实际上却变化了。

ABA问题的解决思路就是使用版本号，在变量前面追加上版本号，那么A－B－A 就会变成1A - 2B－3A。

使用 `AtomicStampedReference` 类可以解决ABA问题

![image-20210409185227111](https://gitee.com/StanAugust/picbed/raw/master/img/20210409185227.png)	

这个类的 `compareAndSet` 方法 

![image-20210409185506470](https://gitee.com/StanAugust/picbed/raw/master/img/20210409185506.png)	

> 使用AtomicStampedReference解决ABA问题

![image-20210409193049529](https://gitee.com/StanAugust/picbed/raw/master/img/20210409193049.png)	

