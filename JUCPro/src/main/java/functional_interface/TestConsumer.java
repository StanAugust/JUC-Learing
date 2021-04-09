package functional_interface;

import java.util.function.Consumer;

public class TestConsumer {
	public static void main(String[] args) {
		Consumer<String> consumer = (str)->{System.out.println("input: " + str);};
		consumer.accept("hello");
	}
}
