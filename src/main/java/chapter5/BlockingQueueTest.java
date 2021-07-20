package chapter5;

import java.util.concurrent.SynchronousQueue;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/17 17:30
 */
public class BlockingQueueTest {

	public static void main(String[] args) {
		SynchronousQueue<Integer> queue = new SynchronousQueue<>();
		Thread customer = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						int take = queue.take();
						System.out.println("[customer] take a product " + take);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		Thread producer = new Thread() {
			@Override
			public void run() {
				for (int i = 0; i < 100; i++) {
					try {
						queue.put(i);
						System.out.println("[producer] make a product " + i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};

		producer.start();
		customer.start();
	}
}
