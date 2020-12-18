package com.iansloat.scalperbot.entities.stores;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iansloat.scalperbot.ScalperBot;
import com.iansloat.scalperbot.entities.products.BestbuyCart;
import com.iansloat.scalperbot.entities.products.BestbuyProduct;
import com.iansloat.scalperbot.entities.products.Cart;
import com.iansloat.scalperbot.entities.products.Product;
import com.iansloat.scalperbot.tools.Request;
import com.iansloat.scalperbot.tools.RequestBuilder;
import com.iansloat.scalperbot.tools.RequestMethod;

public class BestbuyWrapper implements Store {

	private static final Logger logger = LoggerFactory.getLogger(BestbuyWrapper.class);
	private FirefoxDriver driver = null;
	private BestbuyCart cart = null;

	@Override
	public List<Product> searchForProducts(String search) {
		logger.info("Performing search for product \"" + search + "\"...");
		try {
			Document doc = Jsoup.connect("https://www.bestbuy.com/site/searchpage.jsp?st=" + search.replace(' ', '+'))
					.get();
			Elements e = doc.getElementsByClass("sku-header");
			if (e.size() > 0) {
				List<Product> products = new ArrayList<>();
				List<Future<?>> loadTasks = new ArrayList<>();
				ExecutorService taskPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
				for (Element r : e) {
					loadTasks.add(taskPool.submit(() -> {
						Elements n = r.getElementsByTag("a");
						if (n.size() > 0)
							products.add(new BestbuyProduct("https://www.bestbuy.com" + n.get(0).attr("href")));
					}));
				}
				for (Future<?> task : loadTasks) {
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
				} else
					return null;
			} else
				return null;
		} catch (IOException e2) {
			logger.error("Could not load page data for URL https://www.bestbuy.com/site/searchpage.jsp?st="
					+ search.replace(' ', '+'));
			return null;
		}
	}

	@Override
	public String getStoreID() {
		return "bestbuy";
	}

	@Override
	public FirefoxDriver getBrowserInstance() {
		if(driver == null) {
			boolean success = initBrowserInstance();
			if(!success)
				return null;
		}
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

	@Override
	public boolean initBrowserInstance() {
		if(driver == null) {
			logger.info("Starting new best buy wrapper engine...");
			driver = ScalperBot.getNewBrowserInstance();
			if (driver != null) {
				driver.get("https://www.bestbuy.com");
				logger.info("done.");
			} else {
				logger.error("Could not start wrapper engine");
				return false;
			}
		}
		return true;
	}

	@Override
	public Cart getCart() {
		if(cart == null)
			cart = new BestbuyCart(this);
		return cart;
	}

	@Override
	public boolean addProductToCart(Product product) {
		if(driver == null) {
			initBrowserInstance();
			if(driver == null)
				return false;
		}
		try {
			String productPage = product.getSourceURL();
			String userAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
			List<NameValuePair> queryList = URLEncodedUtils.parse(new URI(productPage), Charset.forName("UTF-8"));
			Map<String, String> queryMap = new HashMap<>();
			for (NameValuePair query : queryList) {
				queryMap.put(query.getName(), query.getValue());
			}
			JSONObject bodyData = new JSONObject();
			JSONArray items = new JSONArray();
			JSONObject item = new JSONObject();
			item.put("skuId", queryMap.get("skuId"));
			items.put(item);
			bodyData.put("items", items);
			for (Cookie c : driver.manage().getCookies()) {
				c.validate();
			}
			Set<Cookie> cookies = driver.manage().getCookies();
			String cookieString = "";
			for (Cookie c : cookies) {
				if (!cookieString.equals(""))
					cookieString += "; ";
				cookieString += c.getName() + '=' + c.getValue();
			}
			RequestBuilder request = new RequestBuilder(driver);
			request.setMethod(RequestMethod.POST);
			request.setHost("https://www.bestbuy.com/cart/api/v1/addToCart");
			request.addHeader("Host", "www.bestbuy.com");
			request.addHeader("User-Agent", userAgent);
			request.addHeader("Accept", "application/json");
			request.addHeader("Accept-Language", "en-US,en;q=0.5");
			request.addHeader("Accept-Encoding", "gzip, deflate, br");
			request.addHeader("Referer", productPage);
			request.addHeader("Content-Type", "application/json; charset=UTF-8");
			request.addHeader("Content-Length", "" + bodyData.toString().length());
			request.addHeader("Origin", "https://www.bestbuy.com");
			request.addHeader("Connection", "keep-alive");
			request.addHeader("Cookie", cookieString);
			request.addHeader("TE", "Trailers");
			request.setBody(bodyData.toString().replace("\"", "\\\""));
			Request req = request.build();
			req.execute();
			req.getResponse();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		return false;
	}

}
