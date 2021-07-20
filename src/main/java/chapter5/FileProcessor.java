package chapter5;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/20 16:07
 */
public class FileProcessor {
	static class FileCrawler implements Runnable {
		private final BlockingQueue<File> fileBlockingQueue;
		private final String fileName;

		public FileCrawler(BlockingQueue<File> fileBlockingQueue, String fileName) {
			this.fileBlockingQueue = fileBlockingQueue;
			this.fileName = fileName;
		}

		@Override
		public void run() {
			try {
				File rootFile = new File(fileName);
				Queue<File> fileQueue = new LinkedList<>();
				fileQueue.offer(rootFile);

				while (!fileQueue.isEmpty()) {
					File cur = fileQueue.poll();
					System.out.println("[FileCrawler] craw the file " + cur.getName() + ".");
					if (cur.isFile())
						fileBlockingQueue.put(cur);
					else
						for (File file : cur.listFiles())
							fileQueue.offer(file);
				}

				System.out.println("[FileCrawler] craw the file done.");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static class FileIndexer implements Runnable {
		private final BlockingQueue<File> fileBlockingQueue;
		private volatile boolean running;

		public FileIndexer(BlockingQueue<File> fileBlockingQueue) {
			this.fileBlockingQueue = fileBlockingQueue;
			running = true;
		}

		@Override
		public void run() {

			while (running || !fileBlockingQueue.isEmpty()) {
				try {
					File file = fileBlockingQueue.take();
					System.out.println("[FileIndexer] deal with file " + file.getName());
					Thread.sleep(5);
					System.out.println("[FileIndexer] deal with file " + file.getName() + " done.");
				} catch (InterruptedException e) {
					System.out.println("indexer has been interrupted..");
				}
			}

		}

		public void stopRunning() {
			running = false;
		}
	}

	public static void main(String[] args) throws Exception {
		ArrayBlockingQueue<File> files = new ArrayBlockingQueue<>(3000);
		Thread crawler = new Thread(new FileCrawler(files, "D:\\theCode\\project\\Tetris"));
		FileIndexer fileIndexer = new FileIndexer(files);
		Thread indexer = new Thread(fileIndexer);
		Thread helper = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						indexer.interrupt();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		crawler.start();
		indexer.start();

		helper.setDaemon(true);
		crawler.join();
		fileIndexer.stopRunning();
		// helper.start();


	}
}
