package com.share.sales.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.share.sales.Constants;
import com.share.sales.dao.model.TagBean;
import com.share.sales.dao.service.TagDAO;

public class TagsHelper {
	public static final String TAG_DELIMITER = "[|]";
	public static final String TAG_PATTERN = "\\{(\\d*)\\}";
	public static final String TAG_SIMPLE_DELIMITER = "|";
	public static final String TAG_SIMPLE_OPEN = "{";
	public static final String TAG_SIMPLE_CLOSE = "}";
	private static final Pattern PATTERN = Pattern.compile(TAG_PATTERN);
	
	public static List<TagBean> createTagsList(String tagString, Context ctx) {
		List<TagBean> list = new ArrayList<TagBean>();
		
		if (tagString != null) {
			String[] tagsEntries = tagString.split(TAG_DELIMITER);
			
			for (String entry : tagsEntries) {
				entry = entry.trim();
				Matcher matcher = PATTERN.matcher(entry);
				
				if (matcher.matches()) {
					String strId = matcher.group(1);
					
					long id = 0;
					try {
						id = Long.parseLong(strId);
					} catch (NumberFormatException e) {
						Log.w(Constants.LOG_TAG, "Tag `" + strId + "` ID is not a number", e);
						continue;
					}
					
					TagDAO dao = new TagDAO(ctx);
					TagBean tagBean = dao.getById(id);
					list.add(tagBean);
				} else {
					TagBean tagBean = new TagBean();
					tagBean.setTitle(entry);
					list.add(tagBean);
				}
			}
		}
		return list;
	}
	
	public static TextView settleTagTextView(TagBean tag, TextView tv) {
		tv.setText(tag.getTitle());
		if (tag.getTextColor() != null) {
        	tv.setTextColor(Color.parseColor(tag.getTextColor()));
		}
		if (tag.getBackgroundColor() != null) {
			tv.setBackgroundColor(Color.parseColor(tag.getBackgroundColor()));
		}
		return tv;
	}
	
	public static String createTagsString(List<TagBean> list) {
		StringBuilder str = new StringBuilder();
		int i = 0;
		for (TagBean tag : list) {
			if (tag.getId() == TagBean.VOID_ID) {
				str.append(tag.getTitle());
			} else {
				str.append(TAG_SIMPLE_OPEN).append(tag.getId()).append(TAG_SIMPLE_CLOSE);
			}
			if (i != list.size()) {
				str.append(TAG_SIMPLE_DELIMITER);
			}
			i++;
		}
		return str.toString();
	}
}
