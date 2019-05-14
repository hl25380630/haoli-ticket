package com.haoli.ticket.domain;

import java.util.List;

public class DamiInfo {
	
	private String chromeDriverPath;
	
	private String searchKey;
	
	private String detailItemKey;
	
	private List<String> priceDetailList;
	
	private List<String> sessionDetailList;

	public String getChromeDriverPath() {
		return chromeDriverPath;
	}

	public void setChromeDriverPath(String chromeDriverPath) {
		this.chromeDriverPath = chromeDriverPath;
	}

	public String getSearchKey() {
		return searchKey;
	}

	public void setSearchKey(String searchKey) {
		this.searchKey = searchKey;
	}

	public String getDetailItemKey() {
		return detailItemKey;
	}

	public void setDetailItemKey(String detailItemKey) {
		this.detailItemKey = detailItemKey;
	}

	public List<String> getPriceDetailList() {
		return priceDetailList;
	}

	public void setPriceDetailList(List<String> priceDetailList) {
		this.priceDetailList = priceDetailList;
	}

	public List<String> getSessionDetailList() {
		return sessionDetailList;
	}

	public void setSessionDetailList(List<String> sessionDetailList) {
		this.sessionDetailList = sessionDetailList;
	}
	
}
