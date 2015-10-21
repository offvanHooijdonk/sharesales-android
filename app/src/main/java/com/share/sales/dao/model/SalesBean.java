package com.share.sales.dao.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SalesBean extends BaseBean {
	@Expose
	@SerializedName("name")
	private String name;
	@Expose
	@SerializedName("description")
	private String description;
	@Expose
	@SerializedName("startDate")
	private Long startDate;
	@Expose
	@SerializedName("endDate")
	private Long endDate;
	@Expose
	@SerializedName("priceOrigin")
	private float priceOrigin;
	@Expose
	@SerializedName("priceCurrent")
	private float priceCurrent;
	@Expose
	@SerializedName("perCent")
	private int perCent;
	@Expose
	@SerializedName("currency")
	private CurrencyBean currency;
	@Expose
	@SerializedName("amount")
	private int amount;
	/*
	 * @Expose
	 * 
	 * @SerializedName("pictures")
	 */
	private List<String> pictures;
	@Expose
	@SerializedName("city")
	private LocationBean city;
	@Expose
	@SerializedName("address")
	private String address;
	/*
	 * @Expose
	 * 
	 * @SerializedName("store")
	 */
	private StoreBean store;
	@Expose
	@SerializedName("latitude")
	private Double latitude;
	@Expose
	@SerializedName("longitude")
	private Double longitude;
	@Expose
	@SerializedName("tagsString")
	private String tagsString;
	@Expose
	@SerializedName("category")
	private CategoryBean category;
	@Expose
	@SerializedName("status")
	private int status;
	@Expose
	@SerializedName("active")
	private boolean active;
	@Expose
	@SerializedName("createdDate")
	private long createdDate;
	private List<TagBean> tags = new ArrayList<TagBean>();

	public long getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
	}

	public List<TagBean> getTags() {
		return tags;
	}

	public void setTags(List<TagBean> tags) {
		this.tags = tags;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public float getPriceOrigin() {
		return priceOrigin;
	}

	public void setPriceOrigin(float priceOrigin) {
		this.priceOrigin = priceOrigin;
	}

	public float getPriceCurrent() {
		return priceCurrent;
	}

	public void setPriceCurrent(float priceCurrent) {
		this.priceCurrent = priceCurrent;
	}

	public int getPerCent() {
		return perCent;
	}

	public void setPerCent(int perCent) {
		this.perCent = perCent;
	}

	public CurrencyBean getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyBean currency) {
		this.currency = currency;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
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

	public StoreBean getStore() {
		return store;
	}

	public void setStore(StoreBean store) {
		this.store = store;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getTagsString() {
		return tagsString;
	}

	public void setTagsString(String tagsString) {
		this.tagsString = tagsString;
	}

	public CategoryBean getCategory() {
		return category;
	}

	public void setCategory(CategoryBean category) {
		this.category = category;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<String> getPictures() {
		return pictures;
	}

	public void setPictures(List<String> pictures) {
		this.pictures = pictures;
	}

}
