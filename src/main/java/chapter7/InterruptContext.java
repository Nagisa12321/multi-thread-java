package chapter7;

import java.util.concurrent.*;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/25 15:36
 */
public class InterruptContext {
	private static final ScheduledExecutorService cancelExec = Executors.newScheduledThreadPool(10);

	public static void timeRunV1(Runnable r, long timeout, TimeUnit unit) {
		final Thread thread = Thread.currentThread();
		cancelExec.schedule(thread::interrupt, timeout, unit);
		r.run();
	}

	public static void timeRunV2(Runnable r, long timeout, TimeUnit unit) {
		class RethrowableTask implements Runnable {
			volatile Throwable t;
			@Override
			public void run() {
				try { r.run(); }
				catch (Throwable t) {
					this.t = t;
				}
			}

			void rethrow() throws Throwable {
				if (t != null)
					throw t;
			}
		}

		RethrowableTask task = new RethrowableTask();
		final Thread taskThread = new Thread(task);
		taskThread.start();
		cancelExec.schedule(new Runnable() {
			@Override
			public void run() {
				taskThread.interrupt();
			}
		}, timeout, unit);
		try {
			taskThread.join(unit.toMillis(timeout));
			task.rethrow();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	public static void timeRunV3(Runnable r, long timeout, TimeUnit unit) {
		Future<?> submit = cancelExec.submit(r);
		try {
			submit.get(timeout, unit);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.out.println("time out");
		} finally {
			submit.cancel(true);
		}
	}

	public static void main(String[] args) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						System.out.println("hello world");
						if (Thread.currentThread().isInterrupted())
							throw new InterruptedException();
					} catch (InterruptedException e) {
						System.out.println("Interrupted");
						break;
					}
				}
			}
		};
		timeRunV3(runnable, 3, TimeUnit.SECONDS);
	}
}
