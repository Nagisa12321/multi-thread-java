package chapter7;

import java.io.IOException;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/26 15:56
 */
public class ShutDownHook {
	public static void main(String[] args) {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("hello world");
			}
		});
	}
}
