package com.share.sales.ui.sale.form;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.share.sales.R;
import com.share.sales.utils.LocationUtil;

public class MapPickerActivity extends Activity {

	public static final String EXTRA_MAP_POSITION = "extra_map_return_position";
	public static final String EXTRA_INPUT_MAP_POSITION = "extra_input_map_position";
	
	private GoogleMap map;
	private MyLocationSource locationSource;

	private Button btnSelect;
	private Button btnClear;
	private Button btnCancel;
	
	Marker marker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_map_picker);
		
		btnSelect = (Button) findViewById(R.id.btnSelect);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		
		Intent intent = getIntent();
		LatLng locationToDisplay = intent.getExtras().getParcelable(EXTRA_INPUT_MAP_POSITION);
		
		if (LocationUtil.servicesConnected(this)) {
			btnClear.setEnabled(false);
			btnClear.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (marker != null) {
						marker.remove();
						marker = null;
					}
					
					btnClear.setEnabled(false);
				}
			});
			
			btnSelect.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					LatLng latLng = null;
					if (marker != null) {
						latLng = marker.getPosition();
					}
					intent.putExtra(EXTRA_MAP_POSITION, latLng);
					setResult(RESULT_OK, intent);
					finish();
				}
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_CANCELED);
					finish();
				}
			});
			
			initLocation(locationToDisplay);
		}
		
	}
	
	@Override
	protected void onStop() {
		locationSource.deactivate();
		
		super.onStop();
	}
	
	private void initLocation(LatLng locationToDisplay) {
		
		FragmentManager myFragmentManager = getFragmentManager();
		MapFragment mySupportMapFragment = (MapFragment) myFragmentManager
				.findFragmentById(R.id.map);
		map = mySupportMapFragment.getMap();
		map.setMyLocationEnabled(true);
		map.setBuildingsEnabled(true);
		map.getUiSettings().setCompassEnabled(true);
		
		locationSource = new MyLocationSource(this);
		map.setLocationSource(locationSource);
		
		map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
			@Override
			public boolean onMyLocationButtonClick() {
				locationSource.requestUpdates(true);
				return false;
			}
		});
		
		map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location location) {
				zoomToLocation(new LatLng(location.getLatitude(), location.getLongitude()));
			}
		});
		
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				displayNewMarker(latLng);
			}
		});
		try {
			if (locationToDisplay != null) {
				displayNewMarker(locationToDisplay);
			}
		} catch(Exception e) {
			Toast.makeText(this, "Exception: " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	private void displayNewMarker(LatLng latLng) {
		MarkerOptions mo = new MarkerOptions();
		mo.position(latLng);
		mo.draggable(false);
		
		if (marker != null) {
			marker.remove();
		}
		marker = map.addMarker(mo);
		
		zoomToLocation(latLng);
		
		btnClear.setEnabled(true);
	}
	
	private void zoomToLocation(LatLng latLng) {
		float zoom = LocationUtil.getZoomOptimal(map);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
	}
}
