package com.iansloat.scalperbot.tools;

public enum RequestMethod {

	GET("GET"),
	HEAD("HEAD"),
	POST("POST"),
	PUT("PUT"),
	DELETE("DELETE"),
	CONNECT("CONNECT"),
	OPTIONS("OPTIONS"),
	TRACE("TRACE"),
	PATCH("PATCH");
	
	private final String text;
	
	RequestMethod(final String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
}
