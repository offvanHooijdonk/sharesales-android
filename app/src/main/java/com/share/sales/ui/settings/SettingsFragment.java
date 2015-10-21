package com.share.sales.ui.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

import com.share.sales.R;

public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);

		Preference myPref = (Preference) findPreference("Sync");
		myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				getActivity().getFragmentManager().beginTransaction().replace(android.R.id.content, new SyncDictFragment())
						.addToBackStack("sync").commit();

				return true;
			}
		});
	}
}
