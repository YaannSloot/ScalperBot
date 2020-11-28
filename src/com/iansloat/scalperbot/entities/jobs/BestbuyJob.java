package com.iansloat.scalperbot.entities.jobs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iansloat.scalperbot.entities.products.BestbuyProduct;
import com.iansloat.scalperbot.entities.products.Product;
import com.iansloat.scalperbot.entities.stores.BestbuyWrapper;
import com.iansloat.scalperbot.entities.stores.Store;

public class BestbuyJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(BestbuyJob.class);

	private BestbuyWrapper store;
	private List<Product> pendingProducts;
	private BlockingQueue<Product> stockwatchQueue;
	private BlockingQueue<Product> checkoutQueue;
	private ExecutorService taskExecutor;
	private List<Future<?>> currentTasks;
	private List<Runnable> checkoutCallbacks;
	private Thread checkoutThread;
	private boolean stopFlag;

	public BestbuyJob() {
		this.store = new BestbuyWrapper();
		this.pendingProducts = new ArrayList<>();
		this.stockwatchQueue = new LinkedBlockingQueue<>();
		this.checkoutQueue = new LinkedBlockingQueue<>();
		this.taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		this.currentTasks = new ArrayList<>();
		this.checkoutCallbacks = new ArrayList<>();
		this.stopFlag = true;
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public List<Product> getPendingProducts() {
		return pendingProducts;
	}

	@Override
	public boolean addProduct(Product product) {
		if (product instanceof BestbuyProduct) {
			logger.info("Checking product validity...");
			if (product.isValid()) {
				pendingProducts.add(product);
				return true;
			} else
				return false;
		} else
			return false;
	}

	@Override
	public void startJob() {
		if (pendingProducts.size() > 0) {
			if (currentTasks.size() > 0) {
				logger.error("Job is already running!");
			} else {
				if(checkoutThread != null) {
					checkoutThread.interrupt();
				}
				checkoutThread = new Thread(() -> {
					/*
					try {
						Product current = checkoutQueue.take();
						checkoutQueue.add(current);
						System.out.println("aaaaaaa");
						if(checkoutCallbacks.size() > 0) {
							for(Runnable callback : checkoutCallbacks) {
								callback.run();
							}
						}
					} catch (InterruptedException e) {
						logger.info("Checkout thread was stopped");
					}*/
				});
				checkoutThread.start();
				logger.info("Starting stockwatch tasks...");
				for (Product p : pendingProducts) {
					stockwatchQueue.add(p);
				}
				pendingProducts.clear();
				stopFlag = false;
				for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
					currentTasks.add(taskExecutor.submit(() -> {
						logger.info("Task is starting...");
						if (stockwatchQueue.size() > 0) {
							Product current = stockwatchQueue.poll();
							while (!stopFlag) {
								if (current.isAvailable()) {
									logger.info("{" + current.getProductName()
											+ "} Stock has been found. Placing product in checkout queue");
									stopFlag = true;
									checkoutQueue.add(current);
								} else {
									logger.info("{" + current.getProductName() + "} No stock found.");
									if (stockwatchQueue.size() > 0) {
										stockwatchQueue.add(current);
										current = stockwatchQueue.poll();
									}
								}
							}
						} else
							logger.info("No products available");
						logger.info("Task is exiting...");
					}));
				}
			}
		} else
			logger.error("No products available for job");
	}

	@Override
	public void stopJob() {
		if (currentTasks.size() > 0) {
			logger.info("Stopping current job...");
			checkoutThread.interrupt();
			stopFlag = true;
			waitForComplete();
			currentTasks.clear();
			stockwatchQueue.clear();
			logger.info("Done.");
		} else
			logger.error("Job is not running");
	}

	@Override
	public void shutdownJob() {
		logger.info("Shutting down any remaining job threads...");
		stopJob();
		logger.info("Shutting down job thread pool...");
		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(1, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addCheckoutCallback(Runnable callback) {
		checkoutCallbacks.add(callback);
	}

	@Override
	public Queue<Product> getCheckoutQueue() {
		return new LinkedList<>(checkoutQueue);
	}

	@Override
	public BlockingQueue<Product> getBlockingQueue() {
		return checkoutQueue;
	}

	@Override
	public void waitForComplete() {
		if (currentTasks.size() > 0) {
			for (Future<?> task : currentTasks) {
				try {
					task.get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
