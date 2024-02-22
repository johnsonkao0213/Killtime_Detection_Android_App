package labelingStudy.nctu.boredom_detection.permissions;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.service.MobileAccessibilityService;

public class PermissionRequest {
    private static final String TAG = "PermissionRequest";
    static  List<PermissionItem> items;
    Context context;
    public PermissionRequest(Context context) {
        this.context = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            items = Arrays.asList(
                    new PermissionItem("READ_EXTERNAL_STORAGE", "READ_EXTERNAL_STORAGE...", ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("WRITE_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE...", ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACCESS_FINE_LOCATION", "ACCESS_FINE_LOCATION...", ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACCESS_COARSE_LOCATION", "ACCESS_COARSE_LOCATION...", ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("READ_PHONE_STATE", "READ_PHONE_STATE...", ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("WAKE_LOCK", "WAKE_LOCK...", ContextCompat.checkSelfPermission(context, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACTIVITY_RECOGNITION", "ACTIVITY_RECOGNITION...", ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("POST_NOTIFICATIONS", "POST_NOTIFICATIONS...", ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACTION_USAGE_ACCESS_SETTINGS", "ACTION_USAGE_ACCESS_SETTINGS...", checkApplicationUsageAccess(context)),
                    new PermissionItem("ACTION_ACCESSIBILITY_SETTINGS", "ACTION_ACCESSIBILITY_SETTINGS...", isAccessibilitySettingsOn(context)),
                    new PermissionItem("ACTION_LOCATION_SOURCE_SETTINGS", "vACTION_LOCATION_SOURCE_SETTINGS...", isLocationEnabled(context)),
                    new PermissionItem("ACTION_NOTIFICATION_LISTENER_SETTINGS", "ACTION_NOTIFICATION_LISTENER_SETTINGS...", isNotificationServiceEnabled(context)),
                    new PermissionItem("IS_NOTIFICATION_IMPORTANT_HIGH", "IS_NOTIFICATION_IMPORTANT_HIGH...", isNotificationChannelImportantHigh()),
                    new PermissionItem("SYSTEM_ALERT_WINDOW", "SYSTEM_ALERT_WINDOW...", Settings.canDrawOverlays(context))
            );
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            items = Arrays.asList(
                    new PermissionItem("READ_EXTERNAL_STORAGE", "READ_EXTERNAL_STORAGE...", ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("WRITE_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE...", ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACCESS_FINE_LOCATION", "ACCESS_FINE_LOCATION...", ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACCESS_COARSE_LOCATION", "ACCESS_COARSE_LOCATION...", ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("READ_PHONE_STATE", "READ_PHONE_STATE...", ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("WAKE_LOCK", "WAKE_LOCK...", ContextCompat.checkSelfPermission(context, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACTIVITY_RECOGNITION", "ACTIVITY_RECOGNITION...", ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACTION_USAGE_ACCESS_SETTINGS", "ACTION_USAGE_ACCESS_SETTINGS...", checkApplicationUsageAccess(context)),
                    new PermissionItem("ACTION_ACCESSIBILITY_SETTINGS", "ACTION_ACCESSIBILITY_SETTINGS...", isAccessibilitySettingsOn(context)),
                    new PermissionItem("ACTION_LOCATION_SOURCE_SETTINGS", "vACTION_LOCATION_SOURCE_SETTINGS...", isLocationEnabled(context)),
                    new PermissionItem("ACTION_NOTIFICATION_LISTENER_SETTINGS", "ACTION_NOTIFICATION_LISTENER_SETTINGS...", isNotificationServiceEnabled(context)),
                    new PermissionItem("IS_NOTIFICATION_IMPORTANT_HIGH", "IS_NOTIFICATION_IMPORTANT_HIGH...", isNotificationChannelImportantHigh()),
                    new PermissionItem("SYSTEM_ALERT_WINDOW", "SYSTEM_ALERT_WINDOW...", Settings.canDrawOverlays(context))
            );
        }else {
            items = Arrays.asList(
                    new PermissionItem("READ_EXTERNAL_STORAGE", "READ_EXTERNAL_STORAGE...", ContextCompat.checkSelfPermission(context,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("WRITE_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE...", ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACCESS_FINE_LOCATION", "ACCESS_FINE_LOCATION...", ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACCESS_COARSE_LOCATION", "ACCESS_COARSE_LOCATION...", ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("READ_PHONE_STATE", "READ_PHONE_STATE...", ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("WAKE_LOCK", "WAKE_LOCK...", ContextCompat.checkSelfPermission(context, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED),
                    new PermissionItem("ACTION_USAGE_ACCESS_SETTINGS", "ACTION_USAGE_ACCESS_SETTINGS...", checkApplicationUsageAccess(context)),
                    new PermissionItem("ACTION_ACCESSIBILITY_SETTINGS", "ACTION_ACCESSIBILITY_SETTINGS...", isAccessibilitySettingsOn(context)),
                    new PermissionItem("ACTION_LOCATION_SOURCE_SETTINGS", "vACTION_LOCATION_SOURCE_SETTINGS...", isLocationEnabled(context)),
                    new PermissionItem("ACTION_NOTIFICATION_LISTENER_SETTINGS", "ACTION_NOTIFICATION_LISTENER_SETTINGS...", isNotificationServiceEnabled(context)),
                    new PermissionItem("IS_NOTIFICATION_IMPORTANT_HIGH", "IS_NOTIFICATION_IMPORTANT_HIGH...", isNotificationChannelImportantHigh()),
                    new PermissionItem("SYSTEM_ALERT_WINDOW", "SYSTEM_ALERT_WINDOW...", Settings.canDrawOverlays(context))
            );
        }
    }

    private boolean checkApplicationUsageAccess(Context context) {
        boolean granted = false;

        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), context.getPackageName());

            granted = mode == AppOpsManager.MODE_ALLOWED;
            Log.i(TAG, "[test source being requested]checkApplicationUsageAccess mode mIs : " + mode + " granted: " + granted);

        } catch (PackageManager.NameNotFoundException e) {
            Log.i(TAG, "[testing app]checkApplicationUsageAccess something is wrong");
        }
        return granted;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode;

        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            Log.i(TAG, "isLocationEnabled = " + locationMode);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    private boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + MobileAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.i(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.i(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.i(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.i(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.i(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }

    private boolean isNotificationServiceEnabled(Context context){
        android.util.Log.i(TAG, "isNotificationServiceEnabled");
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(),
                "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean isNotificationChannelImportantHigh() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = notificationManager.getNotificationChannel(Constants.SURVEY_CHANNEL_ID);
        if (channel != null) {
            Log.d("Notification", String.valueOf(channel.getImportance()));
            return channel.getImportance() >= NotificationManager.IMPORTANCE_HIGH;
        }else {
            Log.d("Notification", "NULL");
        }
        return true;
    }

    public boolean areAllTrue() {
        for(PermissionItem b : items) {
            if(!b.permission) return false;
        }
        return true;
    }

    public List<PermissionItem> getItems() {
        return items;
    }
}
