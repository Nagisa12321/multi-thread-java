package chapter6;

import java.util.concurrent.*;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/22 10:14
 */
public class ExecutorTest {
	private static final ExecutorService executor = Executors.newFixedThreadPool(10);
	public static void main(String[] args) {
		Callable<Integer> c1 = () -> {
			for (int i = 0; i < 10; i++) {
				System.out.println("c1 steal running");
				Thread.sleep(1000);
			}
			return 555;
		};
		Callable<Integer> c2 = () -> {
			for (int i = 0; i < 3; i++) {
				System.out.println("c2 steal running");
				Thread.sleep(1000);
			}
			return 333;
		};
		Future<Integer> futureTask1 = executor.submit(c1);
		Future<Integer> futureTask2 = executor.submit(c2);
		Runnable c1Waiter = () -> {
			try {
				System.out.println("get the answer " + futureTask1.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		};
		Runnable c2Waiter = () -> {
			try {
				System.out.println("get the answer " + futureTask2.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		};
		executor.execute(c1Waiter);
		executor.execute(c1Waiter);
		executor.execute(c2Waiter);
		executor.execute(c2Waiter);
	}
}
