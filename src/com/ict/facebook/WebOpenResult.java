package com.ict.facebook;

public class WebOpenResult {
	public boolean isMongoGet=false;
	public String html = "";
	public WebOpenResult(boolean mongoGet, String html) {
		super();
		this.isMongoGet = mongoGet;
		this.html = html;
	}
	
}
