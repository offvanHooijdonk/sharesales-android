package com.share.sales.ui.sale.form;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.google.android.gms.maps.LocationSource;

public class MyLocationSource implements LocationSource, LocationListener {
	private LocationManager locationManager;
	private Context ctx;
	private OnLocationChangedListener listener = null;
	private static boolean isProviderRequested = false;

	public MyLocationSource(Context context) {
		this.ctx = context;
		
		locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
	}

	private void showGPSConfirm() {
		AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
		alert.setTitle("Enable GPS");
		alert.setMessage("Enable GPS?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				ctx.startActivity(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		});
		alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alert.show();
	}

	public OnLocationChangedListener getListener() {
		return listener;
	}

	@Override
	public void activate(OnLocationChangedListener l) {
		listener = l;

		requestUpdates(false);
	}
	
	public void requestUpdates(boolean requestGPS) {
		LocationProvider gpsProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
		if (gpsProvider != null) {
			if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !isProviderRequested && requestGPS) {
				isProviderRequested = true;
				showGPSConfirm();
			}
			
			locationManager.requestLocationUpdates(gpsProvider.getName(), 0, 10, this);
			Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null && listener != null) {
				listener.onLocationChanged(location);
			}
		}

		LocationProvider networkProvider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER);
		if (networkProvider != null) {
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60 * 5, 0, this);
			Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location != null && listener != null) {
				listener.onLocationChanged(location);
			}
		}
	}

	@Override
	public void deactivate() {
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		if(listener != null) {
            listener.onLocationChanged(location);
        }
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null && listener != null) {
			listener.onLocationChanged(location);
		}
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
