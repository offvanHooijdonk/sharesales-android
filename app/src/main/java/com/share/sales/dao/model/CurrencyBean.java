package com.share.sales.dao.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CurrencyBean extends BaseBean {
	@Expose
	@SerializedName("code")
	private String nameCode;
	@Expose
	@SerializedName("symbol")
	private String signCode;
	
	@Override
	public String toString() {
		return this.nameCode;
	}

	public String getNameCode() {
		return nameCode;
	}

	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}

	public String getSignCode() {
		return signCode;
	}

	public void setSignCode(String signCode) {
		this.signCode = signCode;
	}
	
}
