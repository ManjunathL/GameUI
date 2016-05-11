package com.mygubbi.game.dashboard.domain.JsonPojo;

public class ModuleNew 
{
	public String importedModule;
	public String mgModule;
	public String image;
	public int quantity;
	public double amount;
	
	public ModuleNew(String importedModule, String mgModule, String image, int quantity, double amount) {
		this.importedModule = importedModule;
		this.mgModule = mgModule;
		this.image = image;
		this.quantity = quantity;
		this.amount = amount;
	}

	public String getImportedModule() {
		return importedModule;
	}

	public void setImportedModule(String importedModule) {
		this.importedModule = importedModule;
	}

	public String getMgModule() {
		return mgModule;
	}

	public void setMgModule(String mgModule) {
		this.mgModule = mgModule;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	
	
}
