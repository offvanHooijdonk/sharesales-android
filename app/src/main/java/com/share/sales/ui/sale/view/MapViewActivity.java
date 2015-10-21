package com.share.sales.ui.sale.view;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.share.sales.R;
import com.share.sales.utils.LocationUtil;

public class MapViewActivity extends Activity {
	public static final String EXTRA_LATLNG = "latlng";
	
	private MapViewActivity that;
	
	private GoogleMap map;
	private Marker markerPlace;
	
	private LatLng locationPlace;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		that = this;
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_map_view);
		
		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment mySupportMapFragment = (MapFragment) myFragmentManager.findFragmentById(R.id.map);
		map = mySupportMapFragment.getMap();
		
		if (LocationUtil.servicesConnected(this)) {
			locationPlace = getIntent().getExtras().getParcelable(EXTRA_LATLNG);
			if (locationPlace != null) {
				displayLocation(locationPlace);
			}
		} else {
			Toast.makeText(that, "Google play Services are not available!", Toast.LENGTH_LONG).show();
			finish();
		}
		
	}
	
	private void displayLocation(LatLng location) {
		//map.setMyLocationEnabled(true);
		map.setBuildingsEnabled(true);
		map.getUiSettings().setCompassEnabled(true);
		
		MarkerOptions mo = new MarkerOptions();
		mo.position(location);
		mo.draggable(false);
		
		markerPlace = map.addMarker(mo);
		markerPlace.setDraggable(false);
		markerPlace.setTitle("The Place");
		
		zoomToLocation(location);
	}
	
	private void zoomToLocation(LatLng latLng) {
		float zoom = LocationUtil.getZoomOptimal(map);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
	}
}
