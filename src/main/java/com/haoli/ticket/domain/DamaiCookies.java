package com.haoli.ticket.domain;

import java.util.List;

import org.openqa.selenium.Cookie;

public class DamaiCookies {

	private List<Cookie> cookieList;

	
	public DamaiCookies(List<Cookie> cookieList) {
		this.cookieList = cookieList;
	}
	
	public List<Cookie> getCookieList() {
		return cookieList;
	}

	public void setCookieList(List<Cookie> cookieList) {
		this.cookieList = cookieList;
	}
	
}
