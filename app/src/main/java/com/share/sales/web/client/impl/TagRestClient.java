package com.share.sales.web.client.impl;

import android.content.Context;

import com.share.sales.dao.model.TagBean;
import com.share.sales.web.client.AbstractRestClient;
import com.share.sales.web.client.RequestBean.RequestMethod;
import com.share.sales.web.client.ServiceCallListener;

public class TagRestClient extends AbstractRestClient<TagBean> {
	private static final String PATH = "tag";
	private static final String PATH_LIST = PATH + "/list";

	public TagRestClient(Context context, ServiceCallListener l) {
		super(context, l);
	}

	public void list(TagBean bean, String tag) {
		send(bean, PATH_LIST, RequestMethod.GET, tag);
	}
}
