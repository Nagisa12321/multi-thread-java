package chapter3;

import java.util.function.Supplier;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/10 17:20
 */
public class ThreadLocalTest {

	private static class LocalThread extends Thread {
		private ThreadLocal<Obj> threadLocal = ThreadLocal.withInitial(Obj::getInstance);

		public void run() {
			System.out.println(threadLocal.get());
			System.out.println(threadLocal.get().i);
			threadLocal.get().i = 200;

		}
	}

	public static void main(String[] args) {
		new LocalThread().start();
		new LocalThread().start();


	}
}

class Obj {
	volatile int i;
	private static Obj obj = new Obj();

	public static Obj getInstance() {
		return obj;
	}

	private Obj() {

	}
}
