package singleton;

public class OuterHolder {
	/*
	 * 和饿汉式的区别：
	 * 饿汉式只要该单例类被装载，那么instance就会被实例化
	 * 而这种模式是单例类被装载，但instance只有在显示调用getInstance()时才会被实例化
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
