package chapter3;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/10 16:34
 */
public class UnsafeState {
	private String[] states = new String[] {
			"Ak", "Al"
	};

	// 称为溢出发布
	public String[] getStates() {
		return states;
	}
}
