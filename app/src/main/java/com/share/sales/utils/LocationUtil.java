package com.share.sales.utils;

import java.io.IOException;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class LocationUtil {
	private static final float ZOOM_MAX_TO_INPUT_LOCATION = 15.0f;

	public static boolean servicesConnected(Activity activity) {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, 0);
            if (dialog != null) {
                dialog.show();
            }
            return false;
        }
    }
	
	public static float getZoomOptimal(GoogleMap map) {
		float maxZoomSupported = map.getMaxZoomLevel();
		float zoom = (maxZoomSupported < ZOOM_MAX_TO_INPUT_LOCATION) ? maxZoomSupported : ZOOM_MAX_TO_INPUT_LOCATION;
		
		return zoom;
	}
	
	public static String getLocalAdminArea(Context ctx, String countryCode, LatLng latLng) throws IOException {
		Locale locale = findLocale(countryCode);
		String adminAreaLocal = null;
		if (locale != null) {
			Geocoder geocoderLocal = new Geocoder(ctx, locale);
			Address addressLocal = geocoderLocal.getFromLocation(latLng.latitude, latLng.longitude, 3).get(0);
			adminAreaLocal = addressLocal.getAdminArea();
		}
		return adminAreaLocal;
	}
	
	public static String getLocalCountryName(Context ctx, String countryCode, LatLng latLng) throws IOException {
		Locale locale = findLocale(countryCode);
		String countryLocal = null;
		if (locale != null) {
			Geocoder geocoderLocal = new Geocoder(ctx, locale);
			Address addressLocal = geocoderLocal.getFromLocation(latLng.latitude, latLng.longitude, 3).get(0);
			countryLocal = addressLocal.getCountryName();
		}
		return countryLocal;
	}
	
	public static String getLocalCityName(Context ctx, String countryCode, LatLng latLng) throws IOException {
		Locale locale = findLocale(countryCode);
		String cityLocal = null;
		if (locale != null) {
			Geocoder geocoderLocal = new Geocoder(ctx, locale);
			Address addressLocal = geocoderLocal.getFromLocation(latLng.latitude, latLng.longitude, 3).get(0);
			cityLocal = addressLocal.getLocality();
		}
		return cityLocal;
	}
	
	private static Locale findLocale(String countryCode) {
		Locale locale = null;
		Locale[] locales = Locale.getAvailableLocales();
		for (Locale loc : locales) {
			if (loc.getCountry().equalsIgnoreCase(countryCode)) {
				locale = loc;
				break;
			}
		}
		return locale;
	}
}
