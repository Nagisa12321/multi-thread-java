package chapter2;

import java.util.Vector;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/10 11:02
 */
public class SyncTest {
	public static void main(String[] args) throws InterruptedException {
		Sync sync = new Sync();

		new Thread(sync::sleep).start();

		Thread.sleep(10);
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println(sync.getVal());
			}
		}).start();
	}
}

class Sync {

	int val = 1;

	public void sleep() {
		synchronized (this) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public int getVal() {
		synchronized (this) {
			return val;
		}
	}
}
