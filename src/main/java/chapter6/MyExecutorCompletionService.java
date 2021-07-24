package chapter6;

import java.util.concurrent.*;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/24 14:38
 */

class MyFutureTask<V> implements RunnableFuture<V> {
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
			done();
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

	protected void done() { }
}

public class MyExecutorCompletionService<V> implements CompletionService<V> {

	private final BlockingQueue<Future<V>> queue;
	private final ExecutorService executor;

	public MyExecutorCompletionService(ExecutorService executor) {
		queue = new LinkedBlockingQueue<>();
		this.executor = executor;
	}

	static class MyQueueingFuture<V> extends MyFutureTask<V> {
		private final BlockingQueue<Future<V>> queue;
		private final MyFutureTask<V> task;

		public MyQueueingFuture(MyFutureTask<V> futureTask, BlockingQueue<Future<V>> queue) {
			super(futureTask, null);
			this.task = futureTask;
			this.queue = queue;
		}

		@Override
		protected void done() {
			queue.add(task);
		}
	}
	@Override
	public Future<V> submit(Callable<V> task) {
		MyFutureTask<V> future = new MyQueueingFuture<>(new MyFutureTask<>(task), queue);
		executor.submit(future);
		return future;
	}

	@Override
	public Future<V> submit(Runnable task, V result) {
		MyFutureTask<V> future = new MyQueueingFuture<>(new MyFutureTask<>(task, null), queue);
		executor.submit(future);
		return future;
	}

	@Override
	public Future<V> take() throws InterruptedException {
		return queue.take();
	}

	@Override
	public Future<V> poll() {
		return queue.poll();
	}

	@Override
	public Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException {
		return queue.poll(timeout, unit);
	}
}
