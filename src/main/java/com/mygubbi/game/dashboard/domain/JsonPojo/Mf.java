package com.mygubbi.game.dashboard.domain.JsonPojo;

public class Mf {
	
	private String basePrice;
	private String material;
	private String finish;
	
	
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Mf [basePrice=" + basePrice + ", material=" + material
				+ ", finish=" + finish + ", images=";
	}

	public Mf(String basePrice, String material, String finish) {
		super();
		this.basePrice = basePrice;
		this.material = material;
		this.finish = finish;
	}

	/**
	 * @return the basePrice
	 */
	public String getBasePrice() {
		return basePrice;
	}

	/**
	 * @param basePrice the basePrice to set
	 */
	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	/**
	 * @return the material
	 */
	public String getMaterial() {
		return material;
	}

	/**
	 * @param material the material to set
	 */
	public void setMaterial(String material) {
		this.material = material;
	}

	/**
	 * @return the finish
	 */
	public String getFinish() {
		return finish;
	}

	/**
	 * @param finish the finish to set
	 */
	public void setFinish(String finish) {
		this.finish = finish;
	}

	public Mf() {
	}

}
