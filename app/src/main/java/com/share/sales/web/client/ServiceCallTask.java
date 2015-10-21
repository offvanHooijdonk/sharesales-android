package com.share.sales.web.client;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

import com.share.sales.dao.model.BaseBean;
import com.share.sales.helper.GsonHelper;
import com.share.sales.web.client.RequestBean.RequestMethod;

public class ServiceCallTask<T extends BaseBean> extends AsyncTask<RequestBean<T>, Void, ResponseBean> {
	private ServiceCallListener listener;

	public ServiceCallTask(ServiceCallListener l) {
		this.listener = l;
	}

	@Override
	protected ResponseBean doInBackground(RequestBean<T>... requests) {
		RequestBean<T> request = requests[0];
		ResponseBean response = new ResponseBean();
		response.setTag(request.getTag());

		HttpURLConnection conn = null;
		try {
			URL url = new URL(request.getUrl());
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000); /* milliseconds */
			conn.setConnectTimeout(15000); /* milliseconds */
			conn.setRequestMethod(request.getMethod().toString());
			conn.setDoInput(true);

			if (request.getMethod().equals(RequestMethod.POST) || request.getMethod().equals(RequestMethod.PUT)
					|| request.getMethod().equals(RequestMethod.DELETE)) {
				conn.setRequestProperty("content-type", "application/json; charset=utf-8");
				if (request.getBean() != null) {
					conn.setDoOutput(true);
					DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
					String json = GsonHelper.getGson().toJson(request.getBean());
					dos.write(json.getBytes("UTF-8"));
				} else {
					// TODO handle `else` case
				}
			}
			// Starts the query
			conn.connect();

			response.setCode(conn.getResponseCode());

			if (response.isValidResponseCode()) {
				String responseBody = WebHelper.convertInputStreamToString(conn.getInputStream());
				response.setBody(responseBody);
			}
		} catch (Exception e) {
			response.setException(e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return response;
	}

	@Override
	protected void onPostExecute(ResponseBean responseBean) {
		listener.handleResponse(responseBean);
	}

}
