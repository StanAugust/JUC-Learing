package singleton;

public class Lazy {
	
	// 优点：第一次调用才初始化，避免内存浪费。
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
