package com.share.sales.dao.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseBean {
	public static final Long VOID_ID = null;
	
	public BaseBean() {
		this.id = VOID_ID;
	}
	
	@Expose
	@SerializedName("id")
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
