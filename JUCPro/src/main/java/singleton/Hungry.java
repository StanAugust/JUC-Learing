package singleton;

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
