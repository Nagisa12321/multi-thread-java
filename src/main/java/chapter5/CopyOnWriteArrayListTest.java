package chapter5;

import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/17 17:13
 */
public class CopyOnWriteArrayListTest {
	public static void main(String[] args) throws Exception {
		CopyOnWriteArrayList<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
		Vector<Integer> vector = new Vector<>();

		// 写入10000次
		Thread copyOnWriteListWriter = new Thread(() -> {
			for (int i = 0; i < 10000; i++)
				copyOnWriteArrayList.add(i);
		});
		// 读10000次
		Thread copyOnWriteListReader = new Thread(() -> {
			for (int i = 0; i < 100000000; i++) {
				Integer integer = copyOnWriteArrayList.get(0);
			}
		});

		// 写入10000次
		Thread vectorWriter = new Thread(() -> {
			for (int i = 0; i < 10000; i++)
				vector.add(i);
		});
		// 读10000次
		Thread vectorReader = new Thread(() -> {
			for (int i = 0; i < 100000000; i++) {
				Integer integer = vector.get(0);
			}
		});
		long start, end;
		start = System.currentTimeMillis();
		copyOnWriteListWriter.start();
		copyOnWriteListReader.start();
		copyOnWriteListReader.join();
		copyOnWriteListWriter.join();
		end = System.currentTimeMillis();
		System.out.println("copyOnWriteList: " + (end - start) + "ms. ");

		start = System.currentTimeMillis();
		vectorWriter.start();
		vectorReader.start();
		vectorReader.join();
		vectorWriter.join();
		end = System.currentTimeMillis();
		System.out.println("vector: " + (end - start) + "ms. ");

	}
}
