package com.iansloat.scalperbot.tools;

import java.util.stream.Collectors;

import org.json.JSONObject;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.firefox.FirefoxDriver;

public class Request {

	private FirefoxDriver driver;
	private String script;
	private JSONObject response;
	
	public Request(FirefoxDriver driver, String script) {
		this.driver = driver;
		this.script = script;
	}
	
	public void execute() {
		String rawResponse = (String) ((JavascriptExecutor) driver).executeScript(script);
		JSONObject jsonResponse = new JSONObject(rawResponse);
		rawResponse = jsonResponse.getString("header");
		String headerJson = "";
		for(String line : rawResponse.lines().collect(Collectors.toList())) {
			if(!headerJson.equals(""))
				headerJson += ',';
			line = line.replaceFirst(": ", "\":\"");
			line = '"' + line + '"';
			headerJson += line;
		}
		jsonResponse.put("header", new JSONObject("{" + headerJson + "}"));
		response = jsonResponse;
	}
	
	public String getResponse() {
		return response.toString();
	}
	
}
