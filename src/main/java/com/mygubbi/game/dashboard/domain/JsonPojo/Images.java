package com.mygubbi.game.dashboard.domain.JsonPojo;

public class Images {
	
	private String images;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Images [images=" + images + "]";
	}

	public Images(String images) {
		super();
		this.images = images;
		
	}

	/**
	 * @return the basePrice
	 */
	public String getImages() {
		return images;
	}

	/**
	 * @param basePrice the basePrice to set
	 */
	public void setImages(String images) {
		this.images = images;
	}




	public Images() {
		// TODO Auto-generated constructor stub
	}

}
