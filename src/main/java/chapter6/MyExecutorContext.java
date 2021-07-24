package chapter6;

import java.util.concurrent.*;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/22 10:24
 */
public class MyExecutorContext {
	interface MyExecutor {
		void execute(Runnable runnable);

		MyFutureTask<?> submit(Runnable runnable);

		<V> MyFutureTask<V> submit(Callable<V> callable);
	}

	static class Executors {
		public static MyExecutor newFixedThreadPool(int threads) {
			return new MyExecutorImpl(threads);
		}
	}

	static class MyFutureTask<V> implements RunnableFuture<V> {
		private static final int NEW = 0;
		private static final int RUNNING = 1;
		private static final int COMPLETED = 2;
		private static final int CANCEL = 3;

		public volatile int state;
		private final Callable<V> callable;
		private V res;
		private Thread runThread;

		public MyFutureTask(Callable<V> callable) {
			this.callable = callable;
			state = NEW;
		}

		public MyFutureTask(Runnable runnable, V res) {
			this(java.util.concurrent.Executors.callable(runnable, res));
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

	static class MyExecutorImpl implements MyExecutor {

		private final BlockingQueue<Runnable> runnables;

		public MyExecutorImpl(int threads) {
			runnables = new LinkedBlockingQueue<>();
			for (int i = 0; i < threads; i++) {
				new Thread(new RunnableExecutor(runnables)).start();
			}
		}

		@Override
		public void execute(Runnable runnable) {
			try {
				runnables.put(runnable);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public MyFutureTask<?> submit(Runnable runnable) {
			MyFutureTask<Void> voidMyFutureTask = new MyFutureTask<>(runnable, null);
			execute(voidMyFutureTask);
			return voidMyFutureTask;
		}

		@Override
		public <V> MyFutureTask<V> submit(Callable<V> callable) {
			MyFutureTask<V> myFutureTask = new MyFutureTask<>(callable);
			execute(myFutureTask);
			return myFutureTask;
		}

		static class RunnableExecutor implements Runnable {

			private final BlockingQueue<Runnable> runnables;

			public RunnableExecutor(BlockingQueue<Runnable> runnables) {
				this.runnables = runnables;
			}

			@Override
			public void run() {
				while (true) {
					try {
						Runnable runnable = runnables.take();
						runnable.run();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	private static final MyExecutor executor = Executors.newFixedThreadPool(10);

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
		MyFutureTask<Integer> futureTask1 = executor.submit(c1);
		MyFutureTask<Integer> futureTask2 = executor.submit(c2);
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
