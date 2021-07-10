package chapter2;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/9 15:49
 */
public class LazyInitRace {
	private ExpensiveObject instance = null;

	// double check
	public ExpensiveObject getInstance() {
		if (instance == null) {
			synchronized (this) {
				if (instance == null)
					instance = new ExpensiveObject();
			}
		}
		return instance;
	}
}

class ExpensiveObject {}

class MultiGettingExpensiveObject {
	public static void main(String[] args) {
		LazyInitRace lazyInitRace = new LazyInitRace();

		for (int i = 0; i < 10; i++)
			new Thread(() -> {
				for (int j = 0; j < 20; j++) {
					System.out.println(Thread.currentThread().getName()
							+ lazyInitRace.getInstance());
				}
			}).start();
	}
}