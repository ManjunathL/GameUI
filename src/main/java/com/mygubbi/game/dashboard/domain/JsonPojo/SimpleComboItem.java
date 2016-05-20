package com.mygubbi.game.dashboard.domain.JsonPojo;

public class SimpleComboItem {
	
	public String code;
	public String title;
	public String type;

	
	public SimpleComboItem() {
	}

	
	public SimpleComboItem(String code, String title) {
		this.code = code;
		this.title = title;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SimpleComboItem that = (SimpleComboItem) o;

		return code.equals(that.code);

	}

	@Override
	public int hashCode() {
		return code.hashCode();
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
