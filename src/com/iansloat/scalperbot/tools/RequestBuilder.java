package com.iansloat.scalperbot.tools;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.firefox.FirefoxDriver;

public class RequestBuilder {

	private FirefoxDriver driver;
	private RequestMethod method;
	private String host;
	private String body;
	private QueryString queries;
	private boolean async;
	private List<Header> headers;

	public RequestBuilder(FirefoxDriver driver) {
		this.driver = driver;
		this.method = RequestMethod.GET;
		this.queries = new QueryString();
		this.async = false;
		this.headers = new LinkedList<>();
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

	public void addQuery(Query query) {
		queries.addQuery(query);
	}

	public void addQuery(String key, String value) {
		queries.addQuery(key, value);
	}

	public void addQueries(QueryString qstring) {
		qstring.getQueries().forEach(q -> queries.addQuery(q));
	}

	public void addHeader(Header header) {
		headers.add(header);
	}

	public void addHeader(String header, String value) {
		headers.add(new Header(header, value));
	}

	public void setAsync(boolean value) {
		async = value;
	}

	public void setBody(String data) {
		body = data;
	}

	public String getJS() {
		String js = "var xhttp = new XMLHttpRequest();xhttp.open(\"" + method + "\",\"" + host;
		if (queries.getQueries().size() > 0) {
			js += '?';
			js += queries;
		}
		js += "\"," + (async ? "true" : "false") + ");";
		for (Header h : headers) {
			js += h.getJS();
		}
		js += "xhttp.send(\"" + body
				+ "\");var response = {response: {code: xhttp.status, msg: xhttp.statusText}, header: xhttp.getAllResponseHeaders(), data: xhttp.responseText};return JSON.stringify(response);";
		return js;
	}

	public Request build() {
		return new Request(driver, getJS());
	}

}
