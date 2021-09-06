package chapter8;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/8/4 11:39
 */
public class TreeRecursiver {
	public void recursive(TreeNode node) {
		if (node == null)
			return;
		node.compute();
		recursive(node.left);
		recursive(node.right);
	}

	public void recursive_fast(TreeNode node) {
		ExecutorService executorService = Executors.newFixedThreadPool(30);
		recursive_fast_helper(node, executorService);
		executorService.shutdown();
	}
	public void recursive_fast_helper(TreeNode node, ExecutorService executor) {
		if (node == null)
			return;
		executor.submit(node::compute);
		recursive_fast_helper(node.left, executor);
		recursive_fast_helper(node.right, executor);
	}

	public static void main(String[] args) {
		TreeNode treeNode = TreeNode.randomTreeNode(30);
		TreeNode.showTree(treeNode);
		System.out.println();
		TreeRecursiver treeRecursiver = new TreeRecursiver();
		long start = System.currentTimeMillis();
		treeRecursiver.recursive(treeNode);
		long end = System.currentTimeMillis();
		long time1 = end - start;
		System.out.println();
		System.out.println();
		System.out.println();
		start = System.currentTimeMillis();
		treeRecursiver.recursive_fast(treeNode);
		end = System.currentTimeMillis();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("time1: " + time1 + "ms");
		System.out.println("time2: " + (end - start) + "ms");
	}
}

class TreeNode {
	int val;
	TreeNode left;
	TreeNode right;


	void compute() {
		System.out.println(Thread.currentThread() + " is computing treenode " + val);
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	static void showTree(TreeNode node) {
		if (node == null)
			return;
		System.out.print(node.val + " ");
		showTree(node.left);
		showTree(node.right);
	}


	static TreeNode randomTreeNode(int nodes) {
		if (nodes == 0)
			return null;
		TreeNode res = new TreeNode();
		int leftNodes = (int) (Math.random() * nodes);
		int rightNodes = nodes - leftNodes - 1;
		res.left = randomTreeNode(leftNodes);
		res.right = randomTreeNode(rightNodes);
		res.val = (int) (Math.random() * 100);
		return res;
	}
}