package chapter6;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/22 10:54
 */
public class TimerTest {
	public static void main(String[] args) {
		Timer timer =  new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("hello !!!");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, 1000, 1000);

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("world !!!");
			}
		}, 1000, 2000);

	}
}
