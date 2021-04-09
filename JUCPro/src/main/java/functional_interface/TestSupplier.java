package functional_interface;

import java.util.Random;
import java.util.function.Supplier;

public class TestSupplier {
	public static void main(String[] args) {
		Supplier<Integer> supplier = ()->{return new Random().nextInt(5);};
		System.out.println(supplier.get());
	}
}
