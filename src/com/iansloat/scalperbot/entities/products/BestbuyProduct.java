package com.iansloat.scalperbot.entities.products;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iansloat.scalperbot.entities.stores.Store;

public class BestbuyProduct implements Product {

	private static final Logger logger = LoggerFactory.getLogger(BestbuyProduct.class);
	private final String sourceURL;
	private String nameCache = "";
	private String imageCache = "";

	public BestbuyProduct(String URL) {
		sourceURL = URL;
		isValid();
	}

	@Override
	public String getSourceURL() {
		return sourceURL;
	}

	@Override
	public String getStoreID() {
		return "bestbuy";
	}

	@Override
	public String getProductName() {
		String name = "";
		if (nameCache.equals("")) {
			try {
				logger.info("Product name not cached. Retrieving info...");
				Document doc = Jsoup.connect(sourceURL).get();
				logger.info("Data retrieved.");
				FileUtils.writeStringToFile(new File("output.txt"), doc.toString(), "UTF-8");
				Elements titles = doc.getElementsByClass("sku-title");
				if (titles.size() > 0) {
					titles = titles.get(0).getElementsByClass("heading-5");
					if (titles.size() > 0) {
						name = titles.get(0).text();
						nameCache = name;
					}
				}
			} catch (IOException e) {
				logger.error("Something went wrong when retrieving product info.");
				name = nameCache;
			}
		} else
			name = nameCache;
		if (name.equals(""))
			logger.error("Product name could not be retrieved");
		return name;
	}

	@Override
	public Store getStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAvailable() {
		try {
			logger.info('{' + getProductName() + "} - Checking availability...");
			Document doc = Jsoup.connect(sourceURL).get();
			Elements cartButton = doc.getElementsByClass("add-to-cart-button");
			if (cartButton.size() > 0) {
				if (cartButton.get(0).text().toLowerCase().equals("add to cart"))
					return true;
				else
					return false;
			} else
				return false;
		} catch (IOException e) {
			logger.error("Could not check availability");
			return false;
		}
	}

	@Override
	public boolean isValid() {
		try {
			if (Jsoup.connect(sourceURL).execute().statusCode() == 200) {
				if (getProductName().equals(""))
					return false;
				else {
					return true;
				}
			} else
				return false;
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public String getProductImage() {
		if (imageCache.equals("")) {
			try {
				logger.info('{' + getProductName() + "} - Product image url not cached. Retrieving image url...");
				Document doc = Jsoup.connect(sourceURL).get();
				Elements img = doc.getElementsByClass("primary-image");
				if (img.size() > 0) {
					String url = img.get(0).attr("src");
					imageCache = url;
					return url;
				} else
					return "";
			} catch (IOException e) {
				logger.error('{' + getProductName() + "} - Could not get product image");
				return "";
			}
		} else
			return imageCache;
	}

}
