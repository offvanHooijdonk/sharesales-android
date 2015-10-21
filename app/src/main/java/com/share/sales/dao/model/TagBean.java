package com.share.sales.dao.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TagBean extends BaseBean {
	@Expose
	@SerializedName("title")
	private String title;
	@Expose
	@SerializedName("titleCode")
	private String titleCode;
	@Expose
	@SerializedName("textColor")
	private String textColor;
	@Expose
	@SerializedName("backgroundColor")
	private String backgroundColor;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitleCode() {
		return titleCode;
	}
	public void setTitleCode(String titleCode) {
		this.titleCode = titleCode;
	}
	public String getTextColor() {
		return textColor;
	}
	public void setTextColor(String textColor) {
		this.textColor = textColor;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	@Override
	public boolean equals(Object o) {
		return this.title.equalsIgnoreCase(((TagBean) o).getTitle());
	}
}
