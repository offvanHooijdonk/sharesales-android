package com.share.sales.helper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonHelper {

	public static Gson getGson() {
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
	}
	
	public static <T> T convertFromString(Class<T> clazz, String jsonString) {
		T result = GsonHelper.getGson().fromJson(jsonString, clazz);
		return result;
	}
}
