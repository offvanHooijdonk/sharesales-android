package com.share.sales.web.client;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.share.sales.R;
import com.share.sales.dao.model.BaseBean;
import com.share.sales.web.client.RequestBean.RequestMethod;

public abstract class AbstractRestClient<T extends BaseBean> implements ServiceCallListener {

	protected Context ctx;
	protected List<ServiceCallListener> listeners = new ArrayList<ServiceCallListener>();
	private ServiceCallTask<T> task = null;
	
	protected AbstractRestClient(Context context, ServiceCallListener l) {
		this.ctx = context;
		if (l != null) {
			listeners.add(l);
		}
	}

	protected void executeBeanRequest(RequestBean<T> requestBean) {
		if (WebHelper.checkConnectionAvailable(ctx)) {
			task = new ServiceCallTask<T>(this);
			task.execute(requestBean);
		} else {
			Toast.makeText(ctx, "Internet connection seems not found!", Toast.LENGTH_LONG).show();
		}
	}
	
	protected void send(T bean, String path, RequestMethod method, String tag) {
		RequestBean<T> requestBean = new RequestBean<T>(getBaseUrl() + path, method, bean, tag);
		
		executeBeanRequest(requestBean);
	}
	
	// TODO move to a Preference class
	protected String getBaseUrl() {
		int restBaseUrlIndex = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this.ctx).getString("restApiBaseUrl", "0"));
		String restBaseUrl = ctx.getResources().getStringArray(R.array.restUrls)[restBaseUrlIndex];
		return restBaseUrl;
	}
	
	public void addListener(ServiceCallListener l) {
		listeners.add(l);
	}
	
	public void cancel() {
		if (task != null) {
			task.cancel(true);
		}
	}

	@Override
	public void handleResponse(ResponseBean responseBean) {
		for (ServiceCallListener l : listeners) {
			l.handleResponse(responseBean);
		}
	}

}
