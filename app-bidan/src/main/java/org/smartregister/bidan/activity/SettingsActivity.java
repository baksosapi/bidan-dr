package org.smartregister.bidan.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.validator.routines.UrlValidator;
import org.smartregister.bidan.R;
import org.smartregister.repository.AllSharedPreferences;

import java.net.MalformedURLException;
import java.net.URL;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by sid-tech on 11/17/17.
 */

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference baseUrlPreference = findPreference("DRISHTI_BASE_URL");
            if (baseUrlPreference != null) {
                final EditTextPreference baseUrlEditTextPreference = (EditTextPreference) baseUrlPreference;
                baseUrlEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue != null) {
                            updateUrl(newValue.toString());
                        }
                        return true;
                    }
                });


                baseUrlEditTextPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        final Dialog dialog = baseUrlEditTextPreference.getDialog();
                        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String newValue = baseUrlEditTextPreference.getEditText().getText().toString();
                                UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});
                                if (newValue != null && urlValidator.isValid(newValue)) {
                                    baseUrlEditTextPreference.onClick(null, DialogInterface.BUTTON_POSITIVE);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Please enter a valid url!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        return false;
                    }
                });
            }
        }

        private void updateUrl(String baseUrl) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

                URL url = new URL(baseUrl);

                String base = url.getProtocol() + "://" + url.getHost();
                int port = url.getPort();

                logInfo("Base URL: " + base);
                logInfo("Port: " + port);

                allSharedPreferences.saveHost(base);
                allSharedPreferences.savePort(port);

                logInfo("Saved URL: " + allSharedPreferences.fetchHost(""));
                logInfo("Port: " + allSharedPreferences.fetchPort(0));
            } catch (MalformedURLException e) {
                logError("Malformed Url: " + baseUrl);
            }
        }
    }

}
