package com.iansloat.scalperbot.entities.products;

import java.util.List;

import org.json.JSONObject;

public interface Cart {
	
	JSONObject getRawData();
	List<Product> getProducts();
	int getProductCount();
	void updateCart();

}
