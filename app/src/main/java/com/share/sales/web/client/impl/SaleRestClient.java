package com.share.sales.web.client.impl;

import android.content.Context;

import com.share.sales.dao.model.SalesBean;
import com.share.sales.web.client.AbstractRestClient;
import com.share.sales.web.client.RequestBean.RequestMethod;
import com.share.sales.web.client.ServiceCallListener;

public class SaleRestClient extends AbstractRestClient<SalesBean> {
	public static final String PATH = "sale";
	public static final String PATH_LIST = PATH + "/list";
	public static final String PATH_SAVE = PATH;
	public static final String PATH_DELETE = PATH;

	public SaleRestClient(Context context, ServiceCallListener l) {
		super(context, l);
	}

	public void save(SalesBean bean, String tag) {
		send(bean, PATH, RequestMethod.PUT, tag);
	}
	
	public void listAll(String tag) {
		send(null, PATH_LIST, RequestMethod.GET, tag);
	}
	
	public void getById(Long id, String tag) {
		send(null, PATH + "/" + id, RequestMethod.GET, tag);
	}
	
	public void delete(Long id, String tag) {
		send(null, PATH_DELETE, RequestMethod.DELETE, tag);
	}
}
