package com.iansloat.scalperbot;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.iansloat.scalperbot.entities.jobs.BestbuyJob;
import com.iansloat.scalperbot.entities.jobs.Job;
import com.iansloat.scalperbot.entities.stores.Store;
import com.iansloat.scalperbot.tools.Request;
import com.iansloat.scalperbot.tools.RequestBuilder;
import com.iansloat.scalperbot.tools.RequestMethod;

public class test {

	public static void main(String[] args) throws URISyntaxException {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				ScalperBot.driverCache.forEach(d -> d.quit());
			}
		});

		Job bestbuyJob = new BestbuyJob();

		System.out.println("Starting browser instance...");
		long before = System.currentTimeMillis();
		bestbuyJob.getStore().initBrowserInstance();
		System.out.println("Done. (" + (System.currentTimeMillis() - before) + " ms)");

		Store bestbuy = bestbuyJob.getStore();
		FirefoxDriver driver = bestbuy.getBrowserInstance();

		String userAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");

		String productPage = "https://www.bestbuy.com/site/msi-geforce-rtx-3080-ventus-3x-10g-oc-bv-gddr6x-pci-express-4-0-graphic-card-black-silver/6430175.p?skuId=6430175";
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

		// Expired cookie check
		System.out.println("Validating cookies...");
		before = System.currentTimeMillis();
		for (Cookie c : driver.manage().getCookies()) {
			c.validate();
		}
		System.out.println("Done. (" + (System.currentTimeMillis() - before) + " ms)");

		Set<Cookie> cookies = driver.manage().getCookies();

		String cookieString = "";
		for (Cookie c : cookies) {
			if (!cookieString.equals(""))
				cookieString += "; ";
			cookieString += c.getName() + '=' + c.getValue();
		}

		RequestBuilder request = new RequestBuilder(bestbuyJob.getStore().getBrowserInstance());
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
		System.out.println("Submitting cart request...");
		before = System.currentTimeMillis();
		req.execute();
		System.out.println("Done. (" + (System.currentTimeMillis() - before) + " ms)");
		System.out.println(req.getResponse());

	}

}
