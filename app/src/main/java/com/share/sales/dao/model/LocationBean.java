package com.share.sales.dao.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationBean extends BaseBean {
	public static final int COUNTRY = 0;
	public static final int ADMINAREA = 1;
	public static final int CITY = 2;

	@Expose
	@SerializedName("codeName")
	private String nameCode;
	@Expose
	@SerializedName("localName")
	private String nameLocal;
	@Expose
	@SerializedName("type")
	private int type;
	@Expose
	@SerializedName("parent")
	private LocationBean parent;
	
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public LocationBean getParent() {
		return parent;
	}
	public void setParent(LocationBean parent) {
		this.parent = parent;
	}

}
