package com.iansloat.scalperbot.entities.jobs;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import com.iansloat.scalperbot.entities.products.Product;
import com.iansloat.scalperbot.entities.stores.Store;

public interface Job {

	Store getStore();
	List<Product> getPendingProducts();
	Queue<Product> getCheckoutQueue();
	BlockingQueue<Product> getBlockingQueue();
	boolean addProduct(Product product);
	void startJob();
	void stopJob();
	void shutdownJob();
	void addCheckoutCallback(Runnable callback);
	void waitForComplete();
	
}
