package com.share.sales.web.client;

import com.share.sales.dao.model.BaseBean;

public class RequestBean<T extends BaseBean> {

	public enum RequestMethod {
		GET, POST, PUT, DELETE
	}

	private String url;
	private RequestMethod method;
	private T bean;
	private String tag;

	public RequestBean() {

	}

	public RequestBean(String url, RequestMethod method, T bean, String tag) {
		this.url = url;
		this.method = method;
		this.bean = bean;
		this.tag = tag;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

	public T getBean() {
		return bean;
	}

	public void setBean(T bean) {
		this.bean = bean;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
