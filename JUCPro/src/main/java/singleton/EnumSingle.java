package singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public enum EnumSingle {
	INSTANCE;
	
	public static EnumSingle getInstance() {
		return INSTANCE;
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		EnumSingle instance = EnumSingle.getInstance();
		EnumSingle instance2 = EnumSingle.getInstance();	
		System.out.println(instance == instance2);

		// 再次试图用反射来破坏单例
		Constructor<EnumSingle> constructor = EnumSingle.class.getDeclaredConstructor(String.class, int.class);
		constructor.setAccessible(true);
		
		EnumSingle instance3 = constructor.newInstance();
		
		System.out.println(instance3);
	}
}

