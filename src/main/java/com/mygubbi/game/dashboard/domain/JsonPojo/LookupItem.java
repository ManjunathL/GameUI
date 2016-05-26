package com.mygubbi.game.dashboard.domain.JsonPojo;

public class LookupItem {
	
	public String code;
	public String title;
	public String additionalType;

	
	public LookupItem() {
	}

	
	public LookupItem(String code, String title) {
		this.code = code;
		this.title = title;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LookupItem that = (LookupItem) o;

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

	public String getAdditionalType() {
		return additionalType;
	}

	public void setAdditionalType(String additionalType) {
		this.additionalType = additionalType;
	}
}
