package labelingStudy.nctu.boredom_detection.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import labelingStudy.nctu.boredom_detection.fragment.LabelButtonFragment;

public class LabelButtonPagerAdapter extends FragmentStateAdapter {

    private static final int TAB_COUNT = 3;
    public LabelButtonPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return LabelButtonFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        // Return the total number of tabs
        return TAB_COUNT; // Change this to the number of tabs you have
    }
}

