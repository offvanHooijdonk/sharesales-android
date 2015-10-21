package com.share.sales.ui.sale;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.share.sales.R;
import com.share.sales.dao.model.SaleSearchBean;
import com.share.sales.dao.model.SalesBean;
import com.share.sales.dao.service.SaleDAO;
import com.share.sales.helper.GsonHelper;
import com.share.sales.helper.LayoutHelper;
import com.share.sales.ui.sale.form.SaleFormActivity;
import com.share.sales.ui.sale.view.SaleViewActivity;
import com.share.sales.ui.settings.SettingsActivity;
import com.share.sales.web.client.ResponseBean;
import com.share.sales.web.client.ServiceCallListener;
import com.share.sales.web.client.impl.SaleRestClient;

public class MainLayoutFragment extends Fragment implements ServiceCallListener {

	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	private static final String WEB_TAG_LIST_ALL = "list_all";

	private MainLayoutFragment that;

	private List<SalesBean> currentList;
	private SaleDAO saleDAO;

	private SalesListAdapter adapter;
	private ListView lvSales;
	/* private ViewGroup blockFilterForm; */
	private ViewGroup blockLoader;

	public MainLayoutFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.layout_main_frag, container, false);
		setHasOptionsMenu(true);
		this.that = this;

		blockLoader = (ViewGroup) rootView.findViewById(R.id.blockLoader);
		lvSales = (ListView) rootView.findViewById(R.id.lvSales);

		/*
		 * blockFilterForm = (ViewGroup)
		 * rootView.findViewById(R.id.blockFilterForm);
		 */

		// TODO get the list of last displayed items from local table
		saleDAO = new SaleDAO(getActivity());
		currentList = saleDAO.getAll();
		adapter = new SalesListAdapter(currentList);
		lvSales.setAdapter(adapter);
		lvSales.setEmptyView(rootView.findViewById(R.id.blockEmptyList));

		lvSales.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SalesBean salesBean = (SalesBean) lvSales.getItemAtPosition(position);
				Intent intent = new Intent(getActivity(), SaleViewActivity.class);
				intent.putExtra(SaleViewActivity.SEND_SALE_ID, salesBean.getId());
				getActivity().startActivity(intent);
			}
		});

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();

		//callForSales(null);
	}

	private void callForSales(SaleSearchBean searchBean) {
		displayLoader(true);

		SaleRestClient client = new SaleRestClient(getActivity(), that);

		client.listAll(WEB_TAG_LIST_ALL);
	}

	private void displayLoader(boolean display) {
		if (display) {
			blockLoader.setVisibility(View.VISIBLE);
			lvSales.setEnabled(false);
		} else {
			blockLoader.setVisibility(View.GONE);
			lvSales.setEnabled(true);
		}
	}

	@Override
	public void handleResponse(ResponseBean responseBean) {
		if (WEB_TAG_LIST_ALL.equals(responseBean.getTag())) {
			if (responseBean.isFailed()) {
				Toast.makeText(getActivity(), "Error! " + responseBean.getException(), Toast.LENGTH_LONG).show();
			} else if (!responseBean.isValidResponseCode()) {
				Toast.makeText(getActivity(), "Invalid response code " + responseBean.getCode(), Toast.LENGTH_LONG).show();
			} else {
				String responseString = responseBean.getBody();
				SalesBean[] sales = GsonHelper.convertFromString(SalesBean[].class, responseString);

				SaleDAO saleDAO = new SaleDAO(getActivity());
				saleDAO.deleteAll();
				for (SalesBean bean : sales) {
					saleDAO.save(bean);
				}
				
				currentList.clear();
				currentList.addAll(saleDAO.getAll());

				LayoutHelper.refreshListView(lvSales);
			}
			displayLoader(false);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_main, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_add_new) {
			Intent intent = new Intent(getActivity(), SaleFormActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_refresh) {
			callForSales(null);
		} else if (item.getItemId() == R.id.action_search) {
			/*
			 * if (blockFilterForm.getVisibility() == View.VISIBLE) {
			 * blockFilterForm.setVisibility(View.GONE); } else {
			 * blockFilterForm.setVisibility(View.VISIBLE); }
			 */
		} else if (item.getItemId() == R.id.action_settings) {
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

}
