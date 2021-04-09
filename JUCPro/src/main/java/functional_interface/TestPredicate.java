package functional_interface;

import java.util.function.Predicate;

public class TestPredicate {
	public static void main(String[] args) {
		Predicate<Integer> predicate = (i)->{return i>0;};
		System.out.println(predicate.test(-1));
	}
}
