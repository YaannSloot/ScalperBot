package com.iansloat.scalperbot.tools;

public class Header {

	private String header;
	private String value;
	
	public Header(String header, String value) {
		this.header = header;
		this.value = value;
	}
	
	public String getHeader() {
		return header;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getJS() {
		return "xhttp.setRequestHeader(\"" + header + "\",\"" + value + "\");";
	}
	
	@Override
	public String toString() {
		return header + ": " + value;
	}
	
}
