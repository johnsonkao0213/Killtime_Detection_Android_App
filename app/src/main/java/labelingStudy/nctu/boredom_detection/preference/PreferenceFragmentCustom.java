package labelingStudy.nctu.boredom_detection.preference;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.boredom_detection.R;

import static android.content.Context.MODE_PRIVATE;


/**
 * The Preference Fragment which shows the Preferences as a List and handles the Dialogs for the
 * Preferences.
 *
 * @author Jakob Ulbrich
 */
public class PreferenceFragmentCustom extends PreferenceFragmentCompat {



    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.preference);

        SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

        int recording_start = sharedPrefs.getInt("recording_start", 600);
        int recording_end = sharedPrefs.getInt("recording_end", 1320);

        TimePreference pref_start_time = (TimePreference) findPreference("prefKey_recording_start");
        pref_start_time.setTime(recording_start);
        pref_start_time.updateSummary(recording_start);
        TimePreference pref_end_time = (TimePreference) findPreference("prefKey_recording_end");
        pref_end_time.setTime(recording_end);
        pref_end_time.updateSummary(recording_end);


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {

        // Try if the preference is one of our custom Preferences
        DialogFragment dialogFragment = null;
        if (preference instanceof TimePreference) {
            // Create a new instance of TimePreferenceDialogFragment with the key of the related
            // Preference
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey());
        }


        if (dialogFragment != null) {
            // The dialog was created (it was one of our custom Preferences), show the dialog for it
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), "androidx.preference" +
                    ".PreferenceFragment.DIALOG");
        } else {
            // Dialog creation could not be handled here. Try with the super method.
            super.onDisplayPreferenceDialog(preference);
        }

    }
}

