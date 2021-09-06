package chapter6;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jtchen
 * @version 1.0
 * @date 2021/7/24 11:19
 */
public class PictureRender {
	static class Picture {
		private final int no;

		public Picture(int no) {
			this.no = no;
		}

		public void download() {
			try {
				System.out.println("now download the picture [" + no + "]");
				Thread.sleep(100);
				System.out.println("picture [" + no + "] download successfully!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void show() {
			try {
				System.out.println("now show the picture [" + no + "]");
				Thread.sleep(10);
				System.out.println("picture [" + no + "] show successfully!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static final int pictures = 10;

	private final ExecutorService executor;
	private final Picture[] pics;

	public PictureRender() {
		executor = Executors.newFixedThreadPool(20);
		pics = new Picture[pictures];
		for (int i = 0; i < pictures; i++) {
			pics[i] = new Picture(i);
		}
	}

	public long renderPicture_serial() {
		long start = System.currentTimeMillis();
		for (int i = 0; i < pictures; i++) {
			pics[i].download();
		}
		for (int i = 0; i < pictures; i++) {
			pics[i].show();
		}
		renderText();
		long end = System.currentTimeMillis();
		return end - start;
	}

	public long renderPicture_completionService() {
		long start = System.currentTimeMillis();
		CompletionService<Picture> completionService = new ExecutorCompletionService<>(executor);
		for (final Picture pic : pics) {
			completionService.submit(() -> {
				pic.download();
				return pic;
			});
		}

		renderText();

		try {
			for (int i = 0; i < pictures; i++) {
				Future<Picture> take = completionService.take();
				Picture picture = take.get();
				picture.show();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		return end - start;
	}

	public long renderPicture_mycompletionService() {
		long start = System.currentTimeMillis();
		MyExecutorCompletionService<Picture> completionService = new MyExecutorCompletionService<>(executor);
		for (final Picture pic : pics) {
			completionService.submit(() -> {
				pic.download();
				return pic;
			});
		}

		renderText();

		try {
			for (int i = 0; i < pictures; i++) {
				Future<Picture> take = completionService.take();
				Picture picture = take.get();
				picture.show();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		return end - start;
	}

	static class ShowWorker implements Runnable {
		final BlockingQueue<Picture> showQueue;
		final AtomicInteger succPic;
		final Picture poison;
		Thread t;


		public ShowWorker(Picture poison, BlockingQueue<Picture> showQueue, AtomicInteger succPic) {
			this.showQueue = showQueue;
			this.succPic = succPic;
			this.poison = poison;
		}

		@Override
		public void run() {
			t = Thread.currentThread();
			try {
				while (true) {
					Picture take = showQueue.take();
					if (take == poison) {
						System.out.println("!!!!!get the poison and exit");
						break;
					}
					take.show();
					succPic.addAndGet(1);
				}
			} catch (InterruptedException e) {
				System.out.println("be interrupted, and will exit..");
			}
		}

		public void stop() {
			showQueue.offer(poison);
		}
	}

	public long renderPicture_bq() {
		long start = System.currentTimeMillis();
		BlockingQueue<Picture> downloadQueue = new LinkedBlockingQueue<>();
		BlockingQueue<Picture> showQueue = new LinkedBlockingQueue<>();
		AtomicInteger succPic = new AtomicInteger(0);
		Picture poison = new Picture(12312321);
		try {
			for (int i = 0; i < pictures; i++)
				downloadQueue.put(pics[i]);
			List<ShowWorker> showWorkers = new LinkedList<>();
			for (int i = 0; i < 4; i++) {
				executor.submit(() -> {
					try {
						while (true) {
							if (downloadQueue.isEmpty()) {
								System.out.println("download worker exit.");
								return;
							}
							Picture take = downloadQueue.take();
							take.download();
							showQueue.put(take);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				ShowWorker showWorker = new ShowWorker(poison, showQueue, succPic);
				executor.submit(showWorker);
				showWorkers.add(showWorker);
			}
			while (succPic.get() != pictures) ;

			showQueue.offer(poison);
			showQueue.offer(poison);
			showQueue.offer(poison);
			showQueue.offer(poison);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		return end - start;
	}

	public void renderText() {
		try {
			System.out.println("now render the text");
			Thread.sleep(10);
			System.out.println("render the text successfully!");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		PictureRender pictureRender;
//		pictureRender = new PictureRender();
//		long picture_serial = pictureRender.renderPicture_serial();
//		pictureRender = new PictureRender();
//		long picture_mycompletionService = pictureRender.renderPicture_completionService();
//		pictureRender = new PictureRender();
//		long picture_completionService = pictureRender.renderPicture_completionService();
		pictureRender = new PictureRender();
		long renderPicture_bq = pictureRender.renderPicture_bq();
//		System.out.println("serial: " + picture_serial + "ms");
//		System.out.println("completionService: " + picture_completionService + "ms");
//		System.out.println("my completionService: " + picture_mycompletionService + "ms");
		System.out.println("bq: " + renderPicture_bq + "ms");


	}
}
