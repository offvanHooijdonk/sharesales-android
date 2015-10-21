package com.share.sales.ui.settings;

import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.share.sales.R;
import com.share.sales.dao.model.CategoryBean;
import com.share.sales.dao.model.CurrencyBean;
import com.share.sales.dao.model.TagBean;
import com.share.sales.dao.service.CategoryDAO;
import com.share.sales.dao.service.CurrencyDAO;
import com.share.sales.dao.service.TagDAO;
import com.share.sales.helper.GsonHelper;
import com.share.sales.web.client.ResponseBean;
import com.share.sales.web.client.ServiceCallListener;
import com.share.sales.web.client.impl.CategoryRestClient;
import com.share.sales.web.client.impl.CurrencyRestClient;
import com.share.sales.web.client.impl.TagRestClient;

public class SyncDictFragment extends Fragment implements ServiceCallListener {
	private static final String REST_TAG_CATEGORY = "REST_TAG_CATEGORY";
	private static final String REST_TAG_CURR = "REST_TAG_CURR";
	private static final String REST_TAG_TAG = "REST_TAG_TAG";
	
	private SyncDictFragment that;
	private TextView tvStatus;
	private TextView tvRes;
	private Button btnSync;
	private ProgressBar pbSync;
	
	private Map<String, ResponseBean> recievedResponse = new HashMap<String, ResponseBean>();
	private int index = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.that = this;
		View rootView = inflater.inflate(R.layout.layout_sync_dict, container, false);

		tvStatus = (TextView) rootView.findViewById(R.id.tvStatus);
		tvRes = (TextView) rootView.findViewById(R.id.tvRes);
		btnSync = (Button) rootView.findViewById(R.id.btnSync);
		pbSync = (ProgressBar) rootView.findViewById(R.id.pbSync);
		
		btnSync.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mapAllEmpty();
				tvStatus.setText("In progress...");
				tvRes.setText("");
				pbSync.setVisibility(View.VISIBLE);
				index = 0;
				new CategoryRestClient(getActivity(), that).list(null, REST_TAG_CATEGORY);
				new CurrencyRestClient(getActivity(), that).list(null, REST_TAG_CURR);
				new TagRestClient(getActivity(), that).list(null, REST_TAG_TAG);
			}
		});
		
		tvStatus.setText("Not Synched");
		pbSync.setVisibility(View.GONE);
		
		return rootView;
	}

	@Override
	public void handleResponse(ResponseBean responseBean) {
		String tag = responseBean.getTag();
		recievedResponse.put(tag, responseBean);
		
		if (responseBean.isFailed()) {
			tvStatus.setText("Failed");
			pbSync.setVisibility(View.GONE);
			tvRes.setText(tvRes.getText() + responseBean.getException().toString());
		} else if (!responseBean.isValidResponseCode()){
			tvStatus.setText("Failed");
			pbSync.setVisibility(View.GONE);
			tvRes.setText(tvRes.getText() + responseBean.getCode().toString());
		} else {
			/*tvRes.setText(tvRes.getText() + responseBean.getBody());*/
			tvStatus.setText(String.format("Recieved %s of %s...", index, recievedResponse.size()));
			index++;
		}
		
		if (isAllRecieved()) {
			pbSync.setVisibility(View.GONE);
			tvStatus.setText("Saving to DB...");
			storeAllToDB();
			tvStatus.setText("All Synchronized");
		}
	}
	
	private void storeAllToDB() {
		ResponseBean respCat = recievedResponse.get(REST_TAG_CATEGORY);
		ResponseBean respCur = recievedResponse.get(REST_TAG_CURR);
		ResponseBean respTags = recievedResponse.get(REST_TAG_TAG);
		
		CategoryBean[] cats = GsonHelper.getGson().fromJson(respCat.getBody(), CategoryBean[].class);
		CategoryDAO catDao = new CategoryDAO(getActivity());
		catDao.removeAll();
		for (CategoryBean cat : cats) {
			catDao.save(cat);
		}
		
		CurrencyBean[] curs = GsonHelper.getGson().fromJson(respCur.getBody(), CurrencyBean[].class);
		CurrencyDAO curDao = new CurrencyDAO(getActivity());
		curDao.removeAll();
		for (CurrencyBean cur : curs) {
			curDao.save(cur);
		}
		
		TagBean[] tags = GsonHelper.getGson().fromJson(respTags.getBody(), TagBean[].class);
		TagDAO tagDao = new TagDAO(getActivity());
		tagDao.removeAll();
		for (TagBean tag : tags) {
			tagDao.save(tag);
		}
	}

	private void mapAllEmpty() {
		recievedResponse.put(REST_TAG_CATEGORY, null);
		recievedResponse.put(REST_TAG_CURR, null);
		recievedResponse.put(REST_TAG_TAG, null);
	}
	
	private boolean isAllRecieved() {
		boolean res = true;
		for (String key : recievedResponse.keySet()) {
			res = res && (recievedResponse.get(key) != null);
		}
		return res;
	}
}
