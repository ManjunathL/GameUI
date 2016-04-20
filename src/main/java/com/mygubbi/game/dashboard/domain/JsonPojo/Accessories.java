package com.mygubbi.game.dashboard.domain.JsonPojo;

public class Accessories {
	private String name;
	private String size;
	private String qty;
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Accessories [name=" + name + ", size=" + size + ", qty=" + qty
				+ "]";
	}


	public Accessories(String name, String size, String qty) {
		super();
		this.name = name;
		this.size = size;
		this.qty = qty;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}


	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}


	/**
	 * @return the qty
	 */
	public String getQty() {
		return qty;
	}


	/**
	 * @param qty the qty to set
	 */
	public void setQty(String qty) {
		this.qty = qty;
	}


	public Accessories() {
		// TODO Auto-generated constructor stub
	}

}
