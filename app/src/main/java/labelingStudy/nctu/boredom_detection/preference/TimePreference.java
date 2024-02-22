package labelingStudy.nctu.boredom_detection.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;
import androidx.preference.Preference;

import labelingStudy.nctu.boredom_detection.R;

/**
 * A Preference to select a specific Time with a {@link android.widget.TimePicker}.
 *
 * @author Jakob Ulbrich
 */
public class TimePreference extends DialogPreference implements Preference.OnPreferenceChangeListener{

    /**
     * In Minutes after midnight
     */
    private int mTime;

    /**
     * Resource of the dialog layout
     */
    private int mDialogLayoutResId = R.layout.pref_dialog_time;


    public TimePreference(Context context) {
        // Delegate to other constructor
        this(context, null);
        this.setOnPreferenceChangeListener(this);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        // Delegate to other constructor
        // Use the preferenceStyle as the default style
        this(context, attrs, R.attr.preferenceStyle);
        this.setOnPreferenceChangeListener(this);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        // Delegate to other constructor
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // Du custom stuff here
        // ...
        // read attributes etc.
    }

    /**
     * Gets the time from the Shared Preferences
     *
     * @return The current preference value
     */
    public int getTime() {
        return mTime;
    }

    /**
     * Saves the time to the SharedPreferences
     *
     * @param time The time to save
     */
    public void setTime(int time) {
        mTime = time;

        // Save to SharedPreference
        persistInt(time);
    }

    protected void updateSummary(int time) {
        int hours = time / 60;
        int minutes = time % 60;
        String sum  =  "";

        if(hours <10)
            sum = sum + "0" ;
        sum = sum + hours +":" ;
        if(minutes <10)
            sum = sum + "0" ;
        sum = sum + minutes;
        setSummary(sum);
    }
    //

    /**
     * Called when a Preference is being inflated and the default value attribute needs to be read
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // The type of this preference is Int, so we read the default value from the attributes
        // as Int. Fallback value is set to 0.
        return a.getInt(index, 0);
    }

    //

    /**
     * Returns the layout resource that is used as the content View for the dialog
     */
    @Override
    public int getDialogLayoutResource() {
        return mDialogLayoutResId;
    }


    //

    /**
     * Implement this to set the initial value of the Preference.
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        // If the value can be restored, do it. If not, use the default value.
        setTime(restorePersistedValue ?
                getPersistedInt(mTime) : (int) defaultValue);
    }


    @Override
    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
        ((TimePreference) preference).updateSummary((int)newValue);
        return true;
    }

}
