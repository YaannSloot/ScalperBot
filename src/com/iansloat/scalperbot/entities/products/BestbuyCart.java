package com.iansloat.scalperbot.entities.products;

import java.util.List;

import org.json.JSONObject;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.iansloat.scalperbot.entities.stores.BestbuyWrapper;

public class BestbuyCart implements Cart {

	private final BestbuyWrapper parent;
	JSONObject rawData;
	
	public BestbuyCart(BestbuyWrapper parent) {
		this.parent = parent;
	}
	
	@Override
	public JSONObject getRawData() {
		return rawData;
	}

	@Override
	public List<Product> getProducts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getProductCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateCart() {
		FirefoxDriver driver = parent.getBrowserInstance();
		if(driver != null) {
			driver.get("https://www.bestbuy.com/cart/json");
			rawData = new JSONObject(driver.findElementsById("json").get(0).getText());
		}
	}

}
