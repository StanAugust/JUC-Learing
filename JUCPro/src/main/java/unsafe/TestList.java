package unsafe;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestList {
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
}
