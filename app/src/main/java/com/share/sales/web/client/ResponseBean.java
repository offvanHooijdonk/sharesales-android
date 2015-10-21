package com.share.sales.web.client;

import java.net.HttpURLConnection;

public class ResponseBean {

	private Integer code;
	private String body;
	private Exception exception;
	private String tag;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	public boolean isFailed() {
		return this.exception != null;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isValidResponseCode() {
		return this.code != null && this.code == HttpURLConnection.HTTP_OK;
	}
}
