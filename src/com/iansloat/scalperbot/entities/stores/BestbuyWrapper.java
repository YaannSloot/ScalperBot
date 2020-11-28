package com.iansloat.scalperbot.entities.stores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iansloat.scalperbot.ScalperBot;
import com.iansloat.scalperbot.entities.products.BestbuyProduct;
import com.iansloat.scalperbot.entities.products.Product;

public class BestbuyWrapper implements Store {

	private static final Logger logger = LoggerFactory.getLogger(BestbuyWrapper.class);
	private FirefoxDriver driver;

	public BestbuyWrapper() {
		logger.info("Starting new best buy wrapper engine...");
		driver = ScalperBot.getNewBrowserInstance();
		if (driver != null)
			logger.info("done.");
		else
			logger.error("Could not start wrapper engine");
	}

	@Override
	public List<Product> searchForProducts(String search) {
		logger.info("Performing search for product \"" + search + "\"...");
		try {
			Document doc = Jsoup.connect("https://www.bestbuy.com/site/searchpage.jsp?st=" + search.replace(' ', '+')).get();
			Elements e = doc.getElementsByClass("sku-header");
			if(e.size() > 0) {
				List<Product> products = new ArrayList<>();
				List<Future<?>> loadTasks = new ArrayList<>();
				ExecutorService taskPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				for(Element r : e) {
					loadTasks.add(taskPool.submit(() -> {
						Elements n = r.getElementsByTag("a");
						if(n.size() > 0)
							products.add(new BestbuyProduct("https://www.bestbuy.com" + n.get(0).attr("href")));
					}));
				}
				for(Future<?> task : loadTasks) {
					try {
						task.get();
					} catch (InterruptedException | ExecutionException e1) {
						e1.printStackTrace();
					}
				}
				taskPool.shutdown();
				if (products.size() > 0) {
					logger.info(products.size() + " products found.");
					return products;
				}
				else
					return null;
			} else
				return null;
		} catch (IOException e2) {
			logger.error("Could not load page data for URL https://www.bestbuy.com/site/searchpage.jsp?st=" + search.replace(' ', '+'));
			return null;
		}
	}

	@Override
	public String getStoreID() {
		return "bestbuy";
	}

	@Override
	public FirefoxDriver getBrowserInstance() {
		return driver;
	}

	@Override
	public boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void finalize() {
		driver.close();
	}

}
