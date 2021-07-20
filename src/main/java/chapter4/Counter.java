package chapter4;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/11 16:25
 */
public final class Counter {
	private long value = 0;

	public synchronized long getValue() {
		return value;
	}

	public synchronized long increment() {
		if (value == Long.MAX_VALUE)
			throw new IllegalStateException();
		return ++value;
	}

	public static void main(String[] args) {
		Counter counter = new Counter();
		Set<Object> objects = Collections.synchronizedSet(new HashSet<>());

		for (int i = 0; i < 10; i++) {
			new Thread(() -> {
				for (int j = 0; j < 100; j++){
					if (!objects.add(counter.increment()))
						System.out.println("error");
				}
			}).start();
		}
	}
}


