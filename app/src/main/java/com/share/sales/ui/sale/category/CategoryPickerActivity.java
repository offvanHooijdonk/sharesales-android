package com.share.sales.ui.sale.category;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.share.sales.R;
import com.share.sales.dao.model.CategoryBean;
import com.share.sales.dao.service.CategoryDAO;

public class CategoryPickerActivity extends Activity {
	public static final String EXTRA_CATEGORY_BEAN = "extra_category_bean";

	private ListView pickerListView;
	private CategoryListPickerAdapter adapter;
	private List<CategoryBean> categories = new ArrayList<CategoryBean>();
	private List<CategoryBean> breadCrumbesList = new ArrayList<CategoryBean>();
	
	private TextView textBreadCrumbs;
	private TextView textBCPlaceholder;
	
	private Menu optionsMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_category_picker);
		
		pickerListView = (ListView) findViewById(R.id.listCotegories);
		textBreadCrumbs = (TextView) findViewById(R.id.textBreadCrumbs);
		textBCPlaceholder = (TextView) findViewById(R.id.textBCPlaceholder);
		
		CategoryDAO categoryDAO = new CategoryDAO(this);
		categories.clear();
		categories.addAll(categoryDAO.getAll(null));
		
		adapter = new CategoryListPickerAdapter(categories);
		pickerListView.setAdapter(adapter);
		pickerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
				CategoryBean cat = (CategoryBean) adapter.getItemAtPosition(position);
				updateCategoriesList(cat, position, true);
			}
		});
	}
	
	private void updateCategoriesList(CategoryBean category, int selectedPosition, boolean forward) {
		Integer parentId = category == null ? null : category.getId().intValue();
		CategoryDAO categoryDAO = new CategoryDAO(this);
		List<CategoryBean> list = categoryDAO.getAll(parentId);
		if (list.size() > 0) {
			if (forward && parentId != null) {
				breadCrumbesList.add(category);
			}
			categories.clear();
			categories.addAll(list);
			adapter.notifyDataSetChanged();
			pickerListView.setItemChecked(-1, true);
			//pickerListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
			pickerListView.invalidateViews();
			pickerListView.refreshDrawableState();
		} else {
			pickerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			pickerListView.setItemChecked(selectedPosition, true);
		}
		if (parentId == null) {
			optionsMenu.findItem(R.id.itemBack).setEnabled(false);
		} else {
			optionsMenu.findItem(R.id.itemBack).setEnabled(true);
		}
		updateBreadCrumbs(breadCrumbesList);
	}
	
	private void updateBreadCrumbs(List<CategoryBean> list) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < list.size();  i++) {
			if (list.get(i) != null) {
				str.append(list.get(i).getNameLocal());
				if (i != list.size() - 1) {
					str.append(" > ");
				}
			}
		}
		if (!"".equals(str.toString().trim())) {
			textBreadCrumbs.setText(str.toString());
			textBCPlaceholder.setVisibility(View.GONE);
			textBreadCrumbs.setVisibility(View.VISIBLE);
		} else {
			textBreadCrumbs.setVisibility(View.GONE);
			textBCPlaceholder.setVisibility(View.VISIBLE);
			textBreadCrumbs.setText("");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenu = menu;
		getMenuInflater().inflate(R.menu.menu_category_picker, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.itemBack : {
				CategoryBean topParent;
				if (breadCrumbesList.size() > 1) {
					topParent = breadCrumbesList.get(breadCrumbesList.size() - 2);
					breadCrumbesList.remove(breadCrumbesList.size() - 1);
				} else {
					topParent = null;
					breadCrumbesList.clear();
				}
				updateCategoriesList(topParent, 0, false);
			} break;
			case R.id.itemSelect : {
				int position = pickerListView.getCheckedItemPosition();
				if (position != ListView.INVALID_POSITION) {
					CategoryBean category = (CategoryBean) pickerListView.getItemAtPosition(position);
					Intent intent = new Intent();
					ArrayList<CategoryBean> selectedCategoryHierarchy = new ArrayList<CategoryBean>();
					selectedCategoryHierarchy.addAll(breadCrumbesList);
					selectedCategoryHierarchy.add(category);
					intent.putParcelableArrayListExtra(EXTRA_CATEGORY_BEAN, selectedCategoryHierarchy);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(this, "No category selected!", Toast.LENGTH_LONG).show();
				}
			} break;
		}
		return true;
	}
}
