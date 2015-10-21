package com.share.sales;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.share.sales.ui.sale.MainLayoutFragment;

public class MainActivity extends FragmentActivity /*implements
		ActionBar.OnNavigationListener*/ {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	//private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private MainActivity that;
	
	private ActionBarDrawerToggle drawerToggle;
	private DrawerLayout drLayout;
	private ListView lvDrawer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		that = this;
		setContentView(R.layout.activity_main);
		
		String[] cmd = new String[] { "logcat", "-f", "/sdcard/yousave.log", "-v", "time", "ActivityManager:W", "myapp:D" };
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);

		setUpDrawer();
		displayMainFragment();
	}
	
	private void setUpDrawer() {
		drLayout = (DrawerLayout) findViewById(R.id.dlMainDrawler);
		drawerToggle = new ActionBarDrawerToggle(this, drLayout,
				R.drawable.ic_drawer, R.string.action_open_drawer,
				R.string.action_close_drawer);
		drLayout.setDrawerListener(drawerToggle);
		drLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		lvDrawer = (ListView) that.findViewById(R.id.lvDrawer);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(that, R.layout.li_drawer_item, that.getResources().getStringArray(R.array.drawer_nav_items));
		lvDrawer.setAdapter(adapter);
		lvDrawer.setItemChecked(0, true);
		lvDrawer.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				drLayout.closeDrawers();
				switch (position) {
					case 0: {
						displayMainFragment();
					} break;
					default: {
						break;
					}
				}
			}
		});
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns true, then it
		// has handled the app icon touch event
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getActionBar().getThemedContext();
		} else {
			return this;
		}
	}

	/*@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}*/
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/

	/*@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new MainLayoutFragment();
		Bundle args = new Bundle();
		args.putInt(MainLayoutFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		return true;
	}*/

	private void displayMainFragment() {
		Fragment fragment = new MainLayoutFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
	}
}
