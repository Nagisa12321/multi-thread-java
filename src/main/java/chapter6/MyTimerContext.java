package chapter6;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/22 10:59
 */
public class MyTimerContext {
	static class MyTimer {

		private BlockingQueue<TimerNode> blockingQueue;

		public MyTimer() {
			blockingQueue = new LinkedBlockingQueue<>();

			new Thread(new MyWorker(blockingQueue)).start();
		}

		public void schedule(Runnable runnable, int delay, int period) {
			try {
				blockingQueue.put(new TimerNode(runnable, delay, period));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void schedule(Runnable runnable, int delay) {
			try {
				blockingQueue.put(new TimerNode(runnable, delay));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		static class MyWorker implements Runnable {

			private final BlockingQueue<TimerNode> blockingQueue;

			public MyWorker(BlockingQueue<TimerNode> blockingQueue) {
				this.blockingQueue = blockingQueue;
			}

			@Override
			public void run() {
				while (true) {
					try {
						TimerNode timerNode = blockingQueue.take();
						if (timerNode.firstRunning) {
							if (System.currentTimeMillis() - timerNode.lastSchedTime > timerNode.delay) {
								timerNode.firstRunning = false;
								timerNode.runnable.run();
								timerNode.lastSchedTime = System.currentTimeMillis();
							} else {
								blockingQueue.put(timerNode);
								continue;
							}
						}
						if (timerNode.period != -1) {
							if (System.currentTimeMillis() - timerNode.lastSchedTime > timerNode.period) {
								timerNode.runnable.run();
								timerNode.lastSchedTime = System.currentTimeMillis();
							}

							blockingQueue.put(timerNode);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		static class TimerNode {
			private long lastSchedTime;
			private boolean firstRunning;
			private final Runnable runnable;
			private final int delay;
			private final int period;

			public TimerNode(Runnable runnable, int delay) {
				this(runnable, delay, -1);
			}

			public TimerNode(Runnable runnable, int delay, int period) {
				this.lastSchedTime = System.currentTimeMillis();
				this.firstRunning = true;
				this.runnable = runnable;
				this.delay = delay;
				this.period = period;
			}

		}
	}


	public static void main(String[] args) {
		MyTimer myTimer = new MyTimer();

		myTimer.schedule(new Runnable() {
			@Override
			public void run() {
				System.out.println("hello ~~~");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, 1000, 1000);

		myTimer.schedule(new Runnable() {
			@Override
			public void run() {
				System.out.println("world ~~~");
			}
		}, 1000, 2000);
	}
}
