package labelingStudy.nctu.boredom_detection.preference;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.preference.DialogPreference;
import androidx.preference.PreferenceDialogFragmentCompat;

import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.boredom_detection.R;

import static android.content.Context.MODE_PRIVATE;
import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * The Dialog for the {@link TimePreference}.
 *
 * @author Jakob Ulbrich
 */
public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    /**
     * The TimePicker widget
     */
    private TimePicker mTimePicker;

    SharedPreferences sharedPrefs;
    /**
     * Creates a new Instance of the TimePreferenceDialogFragment and stores the key of the
     * related Preference
     *
     * @param key The key of the related Preference
     * @return A new Instance of the TimePreferenceDialogFragment
     */
    public static TimePreferenceDialogFragmentCompat newInstance(String key) {
        final TimePreferenceDialogFragmentCompat
                fragment = new TimePreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);


        return fragment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        mTimePicker = (TimePicker) view.findViewById(R.id.edit);

        // Exception: There is no TimePicker with the id 'edit' in the dialog.
        if (mTimePicker == null) {
            throw new IllegalStateException("Dialog view must contain a TimePicker with id 'edit'");
        }


        // Get the time from the related Preference
        Integer minutesAfterMidnight = null;
        DialogPreference preference = getPreference();
        if (preference instanceof TimePreference) {
            minutesAfterMidnight = ((TimePreference) preference).getTime();
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            int hours = minutesAfterMidnight / 60;
            int minutes = minutesAfterMidnight % 60;
            boolean is24hour = DateFormat.is24HourFormat(getContext());

            mTimePicker.setIs24HourView(is24hour);
            mTimePicker.setCurrentHour(hours);
            mTimePicker.setCurrentMinute(minutes);
        }
    }

    /**
     * Called when the Dialog is closed.
     *
     * @param positiveResult Whether the Dialog was accepted or canceled.
     */
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {

            // Get the current values from the TimePicker
            int hours;
            int minutes;
            if (Build.VERSION.SDK_INT >= 23) {
                hours = mTimePicker.getHour();
                minutes = mTimePicker.getMinute();
            } else {
                hours = mTimePicker.getCurrentHour();
                minutes = mTimePicker.getCurrentMinute();
            }

            // Generate value to save
            int minutesAfterMidnight = (hours * 60) + minutes;

            // Save the value
            DialogPreference preference = getPreference();
            if (preference instanceof TimePreference) {
                TimePreference timePreference = ((TimePreference) preference);
                // This allows the client to ignore the user value.
                if(timePreference.getKey().equalsIgnoreCase("prefKey_recording_start"))
                {
                    sharedPrefs.edit().putInt("recording_start", minutesAfterMidnight).apply();

                    //int test = sharedPrefs.getInt("recording_start", 0);
                    //Toast.makeText(getActivity().getApplicationContext(), "recording_start + " + test, Toast.LENGTH_SHORT).show();

                }else if(timePreference.getKey().equalsIgnoreCase("prefKey_recording_end"))
                {
                    sharedPrefs.edit().putInt("recording_end", minutesAfterMidnight).apply();
                    //int test = sharedPrefs.getInt("recording_end", 0);
                    //Toast.makeText(getActivity().getApplicationContext(), "recording_end + " + test, Toast.LENGTH_SHORT).show();
                }
                if (timePreference.callChangeListener(minutesAfterMidnight)) {
                    // Save the value
                    timePreference.setTime(minutesAfterMidnight);


                }
            }
        }
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case BUTTON_NEGATIVE:
                // int which = -2
                dialog.dismiss();
                break;

            case BUTTON_POSITIVE:
                // int which = -1


                // Get the current values from the TimePicker
                sharedPrefs = this.getActivity().getSharedPreferences(Constants.sharedPrefString, MODE_PRIVATE);

                int hours;
                int minutes;
                if (Build.VERSION.SDK_INT >= 23) {
                    hours = mTimePicker.getHour();
                    minutes = mTimePicker.getMinute();
                } else {
                    hours = mTimePicker.getCurrentHour();
                    minutes = mTimePicker.getCurrentMinute();
                }

                int duration = 0;
                // Save the value
                DialogPreference preference = getPreference();
                if (preference instanceof TimePreference) {
                    TimePreference timePreference = ((TimePreference) preference);
                    // This allows the client to ignore the user value.


                    if (timePreference.getKey().equalsIgnoreCase("prefKey_recording_start")) {
                        int time = sharedPrefs.getInt("recording_end", 1320);

                        // Generate value to save
                        int minutesAfterMidnight = (hours * 60) + minutes;
                        if (minutesAfterMidnight < time) {
                            duration = time - minutesAfterMidnight;
                        } else if (minutesAfterMidnight > time) {
                            duration = time + (24 * 60) - minutesAfterMidnight;
                        } else {
                            duration = 24 * 60;
                        }

                    } else if (timePreference.getKey().equalsIgnoreCase("prefKey_recording_end")) {
                        int minutesAfterMidnight = sharedPrefs.getInt("recording_start", 600);

                        // Generate value to save
                        int time = (hours * 60) + minutes;
                        if (minutesAfterMidnight < time) {
                            duration = time - minutesAfterMidnight;
                        } else if (minutesAfterMidnight > time) {
                            duration = time + (24 * 60) - minutesAfterMidnight;
                        } else {
                            duration = 24 * 60;
                        }
                    }
                }

                Boolean canCloseDialog = false;
                if (duration >= (12 * 60)) {
                    canCloseDialog = true;
                }
                if (canCloseDialog) {

                    onDialogClosed(true);
                } else {

                    Toast t = Toast.makeText(getContext(), getResources().getString(R.string.time_preference_reminder), Toast.LENGTH_LONG);
                    t.show();

                }

                break;

        }
    }



}
