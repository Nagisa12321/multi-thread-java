package chapter5;

import java.util.List;
import java.util.concurrent.*;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/20 19:57
 */
public class Preloader {
	public static void main(String[] args) throws Exception {
		FutureTask<Integer> task = new FutureTask<>(() -> {
			for (int i = 0; i < 8; i++) {
				Thread.sleep(1000);
				System.out.println("steal running");
			}
			return 123;
		});

		Thread thread = new Thread(task);


		Runnable waiter = () -> {
			try {
				System.out.println("get the answer " + task.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		};

		thread.start();

		for (int i = 0; i < 5; i++) {
			new Thread(waiter).start();
		}

		Thread.sleep(3000);
		task.cancel(true);
	}
}

class MyFutureTask<V> implements RunnableFuture<V> {
	private static final int NEW = 0;
	private static final int RUNNING = 1;
	private static final int COMPLETED = 2;
	private static final int CANCEL = 3;

	public volatile int state;
	private final Callable<V> callable;
	private V res;
	private Thread runThread;

	public MyFutureTask(Callable<V> callable)  {
		this.callable = callable;
		state = NEW;
	}

	@Override
	public void run() {
		runThread = Thread.currentThread();
		state = RUNNING;
		try {
			res = callable.call();
		} catch (Exception e) {
			System.out.println("future task has been interrupted.");
			state = CANCEL;
			System.out.println("let the state CANCEL!");
			synchronized (this) {
				notifyAll();
			}
			return;
		}
		state = COMPLETED;
		synchronized (this) {
			notifyAll();
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		runThread.interrupt();
		return true;
	}

	@Override
	public boolean isCancelled() {
		return state == CANCEL;
	}

	@Override
	public boolean isDone() {
		int state = this.state;
		return state == CANCEL || state == COMPLETED;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		synchronized (this) {
			if (state == NEW || state == RUNNING)
				wait();

			if (state == CANCEL) {
				throw new ExecutionException("canceled!", new IllegalArgumentException());
			}
			return res;
		}
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		throw new UnsupportedClassVersionError();
	}
}