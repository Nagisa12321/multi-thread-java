package chapter8;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/27 17:16
 */
public class GetCPUS {
	public static void main(String[] args) {
		int cpus = Runtime.getRuntime().availableProcessors();
		System.out.println(cpus);
	}
}
