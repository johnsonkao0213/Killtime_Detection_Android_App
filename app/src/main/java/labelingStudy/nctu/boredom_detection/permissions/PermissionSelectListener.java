package labelingStudy.nctu.boredom_detection.permissions;

import android.view.View;

public interface PermissionSelectListener extends View.OnClickListener {
    void onItemClicked(PermissionItem item);
}
