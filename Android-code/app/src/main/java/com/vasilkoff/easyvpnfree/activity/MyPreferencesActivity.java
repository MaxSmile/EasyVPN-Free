package com.vasilkoff.easyvpnfree.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import com.vasilkoff.easyvpnfree.R;
import com.vasilkoff.easyvpnfree.database.DBHelper;

import java.util.List;

/**
 * Created by Kusenko on 13.12.2016.
 */

public class MyPreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());
            List<String> countryList = dbHelper.getCountries();
            CharSequence entries[] = new CharSequence[countryList.size()];

            for (int i = 0; i < countryList.size(); i++) {
                entries[i] = countryList.get(i);
            }

            ListPreference listPreference = (ListPreference) findPreference("selectedCountry");
            if (listPreference != null && entries.length > 0) {
                listPreference.setEntries(entries);
                listPreference.setEntryValues(entries);
                listPreference.setValueIndex(0);
            }
        }
    }

}
