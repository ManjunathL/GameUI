package com.mygubbi.game.dashboard.domain.JsonPojo;

public class SimpleComboItem {
	
	public String code;
	public String title;
	
	
	public SimpleComboItem() {
	}

	
	public SimpleComboItem(String code, String title) {
		this.code = code;
		this.title = title;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
