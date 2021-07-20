package chapter5;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/20 17:26
 */
public class MySemaphore {
	private int mutex;

	public MySemaphore(int mutex) {
		this.mutex = mutex;
	}

	public void acquire() { acquire(1); }

	public synchronized void acquire(int mutex) {
		try {
			this.mutex -= mutex;
			if (this.mutex < 0)
				wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void release() { release(1); }

	public synchronized void release(int mutex) {
		this.mutex += mutex;
		if (this.mutex <= 0)
			notifyAll();
	}

	private static int a;

	public static void main(String[] args) {
		MySemaphore s1 = new MySemaphore(1);
		MySemaphore s2 = new MySemaphore(0);
		Runnable r1 = () -> {
			for(int i = 0; i < 200; i++) {
				s1.acquire();
				System.out.println(Thread.currentThread() + ": " + a++);
				s2.release();
			}
		};
		Runnable r2 = () -> {
			for(int i = 0; i < 200; i++) {
				s2.acquire();
				System.out.println(Thread.currentThread() + ": " + a++);
				s1.release();
			}
		};

		new Thread(r1).start();
		new Thread(r2).start();
	}
}
