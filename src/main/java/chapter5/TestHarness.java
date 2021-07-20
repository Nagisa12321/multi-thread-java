package chapter5;

import java.util.concurrent.CountDownLatch;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/20 19:47
 */
public class TestHarness {
	public long timeTasks(int nThreads, final Runnable task) throws InterruptedException {
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(nThreads);

		for (int i = 0; i < nThreads; i++) {
			Thread t = new Thread(() -> {
				try {
					startGate.await();
					try {
						task.run();
					} finally {
						endGate.countDown();
					}
				} catch (InterruptedException ignored) {

				}
			});

			t.start();
		}


		long start = System.nanoTime();
		startGate.countDown();
		endGate.await();
		long end = System.nanoTime();
		return end - start;
	}

	public static void main(String[] args) {
		// ...
	}
}
