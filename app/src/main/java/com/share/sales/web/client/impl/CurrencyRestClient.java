package com.share.sales.web.client.impl;

import android.content.Context;

import com.share.sales.dao.model.CurrencyBean;
import com.share.sales.web.client.AbstractRestClient;
import com.share.sales.web.client.RequestBean.RequestMethod;
import com.share.sales.web.client.ServiceCallListener;

public class CurrencyRestClient extends AbstractRestClient<CurrencyBean> {
	private static final String PATH = "currency";
	private static final String PATH_LIST = PATH + "/list";

	public CurrencyRestClient(Context context, ServiceCallListener l) {
		super(context, l);
	}

	public void list(CurrencyBean bean, String tag) {
		send(bean, PATH_LIST, RequestMethod.GET, tag);
	}

}
