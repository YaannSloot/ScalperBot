package com.iansloat.scalperbot.entities.stores;

import java.util.List;

import org.openqa.selenium.firefox.FirefoxDriver;

import com.iansloat.scalperbot.entities.products.Product;

public interface Store {

	List<Product> searchForProducts(String search);
	boolean isLoggedIn();
	String getStoreID();
	FirefoxDriver getBrowserInstance();
	
}
