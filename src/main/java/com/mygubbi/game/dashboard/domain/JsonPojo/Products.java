package com.mygubbi.game.dashboard.domain.JsonPojo;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


public class Products implements Serializable{

	private static final long serialVersionUID = 1011L;
		
		/*                 		*******************			@@@		Fields	@@@					********************					*/


	private String id;

	private String productId;

	private String name;

	private String desc;

	private String dimension;

	private String category;

	private String subcategory;

	private String categoryId;

	private String subcategoryId;

	private String tags;

	private String designer;

	private String curr;

	private String popularity;

	private String relevance;

	private String shortlisted;

	private String likes;

	private String createDt;

	private String pageId;

	private String styleName;

	private String styleId;

	private String priceRange;

	private String priceId;

	private String defaultPrice;

	private String defaultMaterial;

	private String defaultFinish;
	
	private Mf mf[];

	private List<String> images;

	private Components components[];
	
	private Accessories accessories[];



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getProductId() {
		return productId;
	}



	public void setProductId(String productId) {
		this.productId = productId;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getDesc() {
		return desc;
	}



	public void setDesc(String desc) {
		this.desc = desc;
	}



	public String getDimension() {
		return dimension;
	}



	public void setDimension(String dimension) {
		this.dimension = dimension;
	}



	public String getCategory() {
		return category;
	}



	public void setCategory(String category) {
		this.category = category;
	}



	public String getSubcategory() {
		return subcategory;
	}



	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}



	public String getCategoryId() {
		return categoryId;
	}



	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}



	public String getSubcategoryId() {
		return subcategoryId;
	}



	public void setSubcategoryId(String subcategoryId) {
		this.subcategoryId = subcategoryId;
	}



	public String getTags() {
		return tags;
	}



	public void setTags(String tags) {
		this.tags = tags;
	}



	public String getDesigner() {
		return designer;
	}



	public void setDesigner(String designer) {
		this.designer = designer;
	}



	public String getCurr() {
		return curr;
	}



	public void setCurr(String curr) {
		this.curr = curr;
	}



	public String getPopularity() {
		return popularity;
	}



	public void setPopularity(String popularity) {
		this.popularity = popularity;
	}



	public String getRelevance() {
		return relevance;
	}



	public void setRelevance(String relevance) {
		this.relevance = relevance;
	}



	public String getShortlisted() {
		return shortlisted;
	}



	public void setShortlisted(String shortlisted) {
		this.shortlisted = shortlisted;
	}



	public String getLikes() {
		return likes;
	}



	public void setLikes(String likes) {
		this.likes = likes;
	}



	public String getCreateDt() {
		return createDt;
	}



	public void setCreateDt(String createDt) {
		this.createDt = createDt;
	}



	public String getPageId() {
		return pageId;
	}



	public void setPageId(String pageId) {
		this.pageId = pageId;
	}



	public String getStyleName() {
		return styleName;
	}



	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}



	public String getStyleId() {
		return styleId;
	}



	public void setStyleId(String styleId) {
		this.styleId = styleId;
	}



	public String getPriceRange() {
		return priceRange;
	}



	public void setPriceRange(String priceRange) {
		this.priceRange = priceRange;
	}



	public String getPriceId() {
		return priceId;
	}



	public void setPriceId(String priceId) {
		this.priceId = priceId;
	}



	public String getDefaultPrice() {
		return defaultPrice;
	}



	public void setDefaultPrice(String defaultPrice) {
		this.defaultPrice = defaultPrice;
	}



	public String getDefaultMaterial() {
		return defaultMaterial;
	}



	public void setDefaultMaterial(String defaultMaterial) {
		this.defaultMaterial = defaultMaterial;
	}



	public String getDefaultFinish() {
		return defaultFinish;
	}



	public void setDefaultFinish(String defaultFinish) {
		this.defaultFinish = defaultFinish;
	}



	public Mf[] getMf() {
		return mf;
	}



	public void setMf(Mf[] mf) {
		this.mf = mf;
	}



	public List<String> getImages() {
		return images;
	}



	public void setImages(List<String> images) {
		this.images = images;
	}



	public Components[] getComponents() {
		return components;
	}



	public void setComponents(Components[] components) {
		this.components = components;
	}



	public Accessories[] getAccessories() {
		return accessories;
	}



	public void setAccessories(Accessories[] accessories) {
		this.accessories = accessories;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	@Override
	public String toString() {
		return "Products [id=" + id + ", productId=" + productId + ", name="
				+ name + ", desc=" + desc + ", dimension=" + dimension
				+ ", category=" + category + ", subcategory=" + subcategory
				+ ", categoryId=" + categoryId + ", subcategoryId="
				+ subcategoryId + ", tags=" + tags + ", designer=" + designer
				+ ", curr=" + curr + ", popularity=" + popularity
				+ ", relevance=" + relevance + ", shortlisted=" + shortlisted
				+ ", likes=" + likes + ", createDt=" + createDt + ", pageId="
				+ pageId + ", styleName=" + styleName + ", styleId=" + styleId
				+ ", priceRange=" + priceRange + ", priceId=" + priceId
				+ ", defaultPrice=" + defaultPrice + ", defaultMaterial="
				+ defaultMaterial + ", defaultFinish=" + defaultFinish
				+ ", mf=" + Arrays.toString(mf) + ", images=" + images
				+ ", components=" + Arrays.toString(components)
				+ ", accessories=" + Arrays.toString(accessories) + "]";
	}



	public Products(String id, String productId, String name, String desc,
			String dimension, String category, String subcategory,
			String categoryId, String subcategoryId, String tags,
			String designer, String curr, String popularity, String relevance,
			String shortlisted, String likes, String createDt, String pageId,
			String styleName, String styleId, String priceRange,
			String priceId, String defaultPrice, String defaultMaterial,
			String defaultFinish, Mf[] mf, List<String> images,
			Components[] components, Accessories[] accessories) {
		super();
		this.id = id;
		this.productId = productId;
		this.name = name;
		this.desc = desc;
		this.dimension = dimension;
		this.category = category;
		this.subcategory = subcategory;
		this.categoryId = categoryId;
		this.subcategoryId = subcategoryId;
		this.tags = tags;
		this.designer = designer;
		this.curr = curr;
		this.popularity = popularity;
		this.relevance = relevance;
		this.shortlisted = shortlisted;
		this.likes = likes;
		this.createDt = createDt;
		this.pageId = pageId;
		this.styleName = styleName;
		this.styleId = styleId;
		this.priceRange = priceRange;
		this.priceId = priceId;
		this.defaultPrice = defaultPrice;
		this.defaultMaterial = defaultMaterial;
		this.defaultFinish = defaultFinish;
		this.mf = mf;
		this.images = images;
		this.components = components;
		this.accessories = accessories;
	}



	public Products() {
		super();
	}

}