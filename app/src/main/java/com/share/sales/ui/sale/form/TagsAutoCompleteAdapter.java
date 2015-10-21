package com.share.sales.ui.sale.form;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.share.sales.R;
import com.share.sales.dao.model.TagBean;
import com.share.sales.helper.TagsHelper;

public class TagsAutoCompleteAdapter extends BaseAdapter implements Filterable  {
	private List<TagBean> tags;
	private List<TagBean> filteredTags = new ArrayList<TagBean>();
	private TagFilter tagFilter = null;

	public TagsAutoCompleteAdapter(List<TagBean> tagsList) {
		tags = tagsList;
	}
	
	@Override
	public int getCount() {
		return filteredTags.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredTags.get(position).getTitle(); 
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.li_tag_auto_complete, parent, false);
        }
        TagBean tag = filteredTags.get(position);
        TextView textTagTitle = (TextView) view.findViewById(R.id.textTagTitle);
        textTagTitle = TagsHelper.settleTagTextView(tag, textTagTitle);
		return view;
	}
	
	public List<TagBean> getFilteredList() {
		return filteredTags;
	}

	@Override
	public Filter getFilter() {		
		if (tagFilter == null) {
			tagFilter = new TagFilter();
		}
		return tagFilter;
	}


	private class TagFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
		    // We implement here the filter logic
		    if (constraint == null || constraint.length() == 0) {
		        // No filter applied, no data returned
		        results.values = null;
		        results.count = 0;
		    }
		    else {
		        // We perform filtering operation
		    	filteredTags.clear();
		        for (TagBean tag : tags) {
		            if (tag.getTitle().toUpperCase().startsWith(constraint.toString().toUpperCase()))
		            	filteredTags.add(tag);
		        }
		         
		        results.values = filteredTags;
		        results.count = filteredTags.size();
		 
		    }
		    return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if (results.count == 0)
		        notifyDataSetInvalidated();
		    else {
		    	filteredTags = (List<TagBean>) results.values;
		        notifyDataSetChanged();
		    }
		}
	}
}
