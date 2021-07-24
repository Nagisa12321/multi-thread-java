package chapter5;

import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.RecursiveTask;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/21 15:33
 */
public class Barrier {
	static class MergeSort {
		private final int[] arr;

		public MergeSort(int[] arr) {
			this.arr = arr;
		}

		public void sort() {
			sort(arr);
		}

		private void sort(int[] arr) {
			sort(arr, new int[arr.length], 0, arr.length - 1);
		}

		private void sort(int[] arr, int[] aux, int lo, int hi) {
			if (lo >= hi) return;
			int mid = lo + (hi - lo) / 2;
			sort(arr, aux, lo, mid);
			sort(arr, aux, mid + 1, hi);

			merge(arr, aux, lo, mid, hi);
		}

		private void merge(int[] arr, int[] aux, int lo, int mid, int hi) {

			// copy
			for (int i = lo; i <= hi; i++) {
				aux[i] = arr[i];
			}

			// merge
			int i = lo, j = mid + 1;
			for (int k = lo; k <= hi; k++) {
				if (i > mid) arr[k] = aux[j++];
				else if (j > hi) arr[k] = aux[i++];
				else if (less(aux, i, j)) arr[k] = aux[i++];
				else arr[k] = aux[j++];
			}
		}

		private boolean less(int[] arr, int i, int j) {
			return arr[i] <= arr[j];
		}

		@Override
		public String toString() {
			return Arrays.toString(arr);
		}
	}

	static class MultiMergeSort {
		private final int[] arr;

		public MultiMergeSort(int[] arr) {
			this.arr = arr;
		}

		public void sort() {
			sort(arr);
		}

		private void sort(int[] arr) {
			sort(arr, new int[arr.length], 0, arr.length - 1);
		}

		private void sort(int[] arr, int[] aux, int lo, int hi) {
			if (lo >= hi) return;
			int mid = lo + (hi - lo) / 2;

			// if the len < CUTOFF, then insertion.
			if (hi - lo + 1 <= 16) {
				InsertSort.insertSort(arr, lo, hi);
				return;
			}
			Thread t1 = new Thread(() -> {
				sort(arr, aux, lo, mid);
			});
			Thread t2 = new Thread(() -> {
				sort(arr, aux, mid + 1, hi);
			});
			t1.start();
			t2.start();
			try {
				t1.join();
				t2.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			merge(arr, aux, lo, mid, hi);
		}

		private void merge(int[] arr, int[] aux, int lo, int mid, int hi) {

			// copy
			for (int i = lo; i <= hi; i++) {
				aux[i] = arr[i];
			}

			// merge
			int i = lo, j = mid + 1;
			for (int k = lo; k <= hi; k++) {
				if (i > mid) arr[k] = aux[j++];
				else if (j > hi) arr[k] = aux[i++];
				else if (less(aux, i, j)) arr[k] = aux[i++];
				else arr[k] = aux[j++];
			}
		}

		private boolean less(int[] arr, int i, int j) {
			return arr[i] <= arr[j];
		}

		@Override
		public String toString() {
			return Arrays.toString(arr);
		}
	}

	static class InsertSort {
		// arr [1, 2, 3, 4] --> we say isSort(arr, 0, 3)
		private static boolean isSorted(int[] arr, int begin, int end) {
			for (int i = begin + 1; i <= end; i++) {
				if (!less(arr, i - 1, i)) return false;
			}
			return true;
		}

		public static boolean isSorted(int[] arr) {
			return isSorted(arr, 0, arr.length - 1);
		}

		// return true if [i] less then [j]
		private static boolean less(int[] arr, int i, int j) {
			return arr[i] < arr[j];
		}

		public static void insertSort(int[] arr) {
			insertSort(arr, 0, arr.length - 1);
		}

		public static void insertSort(int[] arr, int lo, int hi) {
			for (int i = lo + 1; i <= hi; i++) {
				insert(arr, lo, i);
			}
		}

		// 		l        h
		// arr [1, 4, 6, 3]
		public static void insert(int[] arr, int lo, int hi) {
			int i = hi;
			int key = arr[hi];
			while (i > lo && arr[i - 1] >= key) {
				arr[i] = arr[i - 1];
				i--;
			}
			arr[i] = key;
		}


		public static void main(String[] args) {
			int[] arr = {1, 4, 2, 8, 5, 7};
			insertSort(arr);
			System.out.println(Arrays.toString(arr));
		}
	}


	public static void main(String[] args) {
		int[] arr = new int[1000];
		for (int i = 0; i < 1000; i++) {
			arr[i] = (int) (1000 * Math.random());
		}

		long start = System.currentTimeMillis();
		MultiMergeSort mergeSort = new MultiMergeSort(arr);
		mergeSort.sort();
		System.out.println(mergeSort);
		long end = System.currentTimeMillis();

		System.out.println(end - start);
	}
}
