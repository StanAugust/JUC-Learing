package functional_interface;

import java.util.function.Function;

public class TestFunction {
	public static void main(String[] args) {
		// 只要是函数式接口，就可以用lamba表达式简化!
//		Function<String, Integer> function = new Function<String, Integer>(){
//
//			@Override
//			public Integer apply(String t) {
//				return Integer.valueOf(t);
//			}
//			
//		};

		Function<String, Integer> function = (str) -> {
			return Integer.valueOf(str);
		};

		System.out.println(function.apply("123"));
	}
}
