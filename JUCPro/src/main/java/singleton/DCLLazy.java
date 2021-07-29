package singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

// 这种方式采用双锁机制，安全且在多线程情况下能保持高性能
public class DCLLazy {
	
	// 添加标志位
	private static boolean key = false;
	
	private DCLLazy() {
		// 添加第三重锁
		synchronized (DCLLazy.class) {
			if(key == false) {
				key = true;
			}else {
				throw new RuntimeException("不要使用反射破坏异常");
			}
		}
	}
	
	private volatile static DCLLazy instance;
	
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
                     * 就有可能出现指令重排问题: 
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
	
	public static void main(String[] args) throws Exception {
//		DCLLazy instance1 = DCLLazy.getInstance();
		
		// 反射
		Constructor<DCLLazy> constructor = DCLLazy.class.getDeclaredConstructor(null);
		// 让私有构造器可见
		constructor.setAccessible(true);
		
		// 获取到标志位
		Field field = DCLLazy.class.getDeclaredField("key");
		field.setAccessible(true);
		
		DCLLazy instance1 = constructor.newInstance();
		// 创建第一个实例后再把标志位改回去
		field.set(instance1, false);
		
		DCLLazy instance2 = constructor.newInstance();
		
		System.out.println(instance1);
		System.out.println(instance2);
	}
}












