package chapter5;

import java.util.concurrent.CountDownLatch;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/20 17:42
 */
public class Latch {
	static class MyLatch {
		private int count;
		public MyLatch(int count) {
			this.count = count;
		}

		public synchronized void await() throws InterruptedException {
			while (count > 0)
				wait();
		}

		public synchronized void countDown() {
			count--;
			if (count == 0)
				notify();
		}
	}

	public static void main(String[] args) {
		CountDownLatch latch = new CountDownLatch(3);
		new Thread(() -> {
			try {
				latch.await();
				System.out.println("await 成功, 开始执行");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}).start();
		new Thread(() -> {
			try {
				for (int i = 0; i < 3; i++) {
					System.out.println("now count down... ");
					latch.countDown();
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}).start();
	}
}
