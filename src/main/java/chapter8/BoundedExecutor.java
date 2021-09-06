package chapter8;

import java.util.concurrent.*;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/8/4 11:03
 */
public class BoundedExecutor {
	private final Executor exec;
	private final Semaphore semaphore;

	public BoundedExecutor(Executor exec, int bound) {
		this.exec = exec;
		this.semaphore = new Semaphore(bound);
	}

	public void submitTask(final Runnable command) throws InterruptedException {
		semaphore.acquire();
		try {
			exec.execute(() -> {
				try {
					command.run();
				} finally {
					semaphore.release();
				}
			});
		} catch (RejectedExecutionException e) {
			semaphore.release();
		}
	}

	public static void main(String[] args) {
		ThreadPoolExecutor executor =
				new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(10));
		BoundedExecutor boundedExecutor = new BoundedExecutor(executor, 10);
		try {
			for (int i = 0; i < 30; i++)
				boundedExecutor.submitTask(() -> {
					System.out.println("my running thread: " + Thread.currentThread());
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
