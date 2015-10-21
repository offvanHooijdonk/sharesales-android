package com.share.sales.dao.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class StoreBean extends BaseBean {

	@Expose
	@SerializedName("nameCode")
	private String nameCode;
	@Expose
	@SerializedName("nameLocal")
	private String nameLocal;
	@Expose
	@SerializedName("city")
	private LocationBean city;
	@Expose
	@SerializedName("address")
	
	private String address;
	public String getNameCode() {
		return nameCode;
	}
	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}
	public String getNameLocal() {
		return nameLocal;
	}
	public void setNameLocal(String nameLocal) {
		this.nameLocal = nameLocal;
	}
	public LocationBean getCity() {
		return city;
	}
	public void setCity(LocationBean city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

}
