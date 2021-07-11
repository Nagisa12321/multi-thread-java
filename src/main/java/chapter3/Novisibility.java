package chapter3;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/10 15:12
 */
public class Novisibility {
	private static boolean ready;
	private static int number;

	private static class ReaderThread extends Thread {
		public void run() {
			while (!ready)
				Thread.yield();
			System.out.println(number);
		}
	}

	public static void main(String[] args) {
		new ReaderThread().start();

		// 编译器可能会对下面的指令进行重排
		// 也称为指令流水
		// 在main线程中感知不到指令重排
		// 在其他线程中能看到指令的重排
		number = 32;
		ready = true;
	}
}
