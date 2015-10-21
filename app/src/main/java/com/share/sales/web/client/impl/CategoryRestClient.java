package com.share.sales.web.client.impl;

import android.content.Context;

import com.share.sales.dao.model.CategoryBean;
import com.share.sales.web.client.AbstractRestClient;
import com.share.sales.web.client.RequestBean.RequestMethod;
import com.share.sales.web.client.ServiceCallListener;

public class CategoryRestClient extends AbstractRestClient<CategoryBean> {

	private static final String PATH = "category";
	private static final String PATH_LIST = PATH + "/list";
	
	public CategoryRestClient(Context context, ServiceCallListener l) {
		super(context, l);
	}

	public void list(CategoryBean bean, String tag) {
		send(bean, PATH_LIST, RequestMethod.GET, tag);
	}

}
