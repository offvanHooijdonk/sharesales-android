package com.share.sales.web.client.impl;

import android.content.Context;

import com.share.sales.dao.model.LocationBean;
import com.share.sales.web.client.AbstractRestClient;
import com.share.sales.web.client.RequestBean.RequestMethod;
import com.share.sales.web.client.ServiceCallListener;

public class LocationRestClient extends AbstractRestClient<LocationBean> {
	public static final String PATH = "location";

	public LocationRestClient(Context context, ServiceCallListener l) {
		super(context, l);
	}

	public void getOrSaveLocation(LocationBean bean, String tag) {
		send(bean, PATH, RequestMethod.PUT, tag);
	}
}
