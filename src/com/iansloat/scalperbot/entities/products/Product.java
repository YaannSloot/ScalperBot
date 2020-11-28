package com.iansloat.scalperbot.entities.products;

import com.iansloat.scalperbot.entities.stores.Store;

public interface Product {

	String getSourceURL();
	String getStoreID();
	String getProductName();
	String getProductImage();
	Store getStore();
	boolean isAvailable();
	boolean isValid();
	
}
