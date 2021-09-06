/**
 * @author jtchen
 * @version 1.0
 * @date 2021/8/19 17:20
 */
public class A {
	public static void a() {
		System.out.println("a");
	}
}

class B extends A {
	public static void a() {
		System.out.println("b");
	}

	public static void main(String[] args) {
		a();
	}
}