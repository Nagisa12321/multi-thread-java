package chapter8;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/27 17:24
 */
public class ThreadPoolExecutorContext {
	public static void main(String[] args) {

		ThreadPoolExecutor executor =
				new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		System.out.println("main's running thread: " + Thread.currentThread());
		for (int i = 0; i < 30; i++)
			executor.submit(() -> {
				System.out.println("my running thread: " + Thread.currentThread());
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
	}
}
