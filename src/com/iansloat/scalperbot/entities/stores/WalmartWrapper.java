package com.iansloat.scalperbot.entities.stores;

import java.util.List;

import org.openqa.selenium.firefox.FirefoxDriver;

import com.iansloat.scalperbot.entities.products.Cart;
import com.iansloat.scalperbot.entities.products.Product;

public class WalmartWrapper implements Store {

	@Override
	public List<Product> searchForProducts(String search) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStoreID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FirefoxDriver getBrowserInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLoggedIn() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean initBrowserInstance() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Cart getCart() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean addProductToCart(Product product) {
		// TODO Auto-generated method stub
		return false;
	}

}
