package chapter2;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/9 15:57
 */
public class AtomicLongTest {
	public static void main(String[] args) {
		AtomicLong atomicLong = new AtomicLong(1L);
		atomicLong.incrementAndGet();
	}
}
