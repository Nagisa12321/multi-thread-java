package chapter8;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/8/4 11:09
 */
public class ThreadFactoryContext {
	static class MyThread extends Thread {
		public MyThread(Runnable target) {
			super(target);
		}

		@Override
		public void run() {
			System.out.println(getName() + " is start to running. ");
			try {
				super.run();
			} finally {
				System.out.println(getName() + " ... end");
			}
		}
	}

	static class MyThreadFactory implements ThreadFactory {

		@Override
		public Thread newThread(Runnable r) {
			return new MyThread(r);
		}
	}

	public static void main(String[] args) {
		ThreadPoolExecutor executor =
				new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10), new MyThreadFactory());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()
		);
		for (int i = 0; i < 20; i++)
			executor.submit(() -> {
				System.out.println("my running thread: " + Thread.currentThread());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});

		executor.shutdown();
	}
}
