package labelingStudy.nctu.boredom_detection.permissions;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Arrays;
import java.util.List;

import labelingStudy.nctu.boredom_detection.BoredomApp;
import labelingStudy.nctu.boredom_detection.R;
import labelingStudy.nctu.boredom_detection.databinding.PermissionBinding;
import labelingStudy.nctu.minuku.config.Constants;
import labelingStudy.nctu.minuku.logger.Log;
import labelingStudy.nctu.minuku.service.MobileAccessibilityService;
import labelingStudy.nctu.minuku.service.NotificationListenService;

public class PermissionActivity extends AppCompatActivity implements PermissionSelectListener {
    private static final String TAG = "PermissionActivity";
    PermissionRequest permissionRequest;
    PermissionAdaptor adapter;
    ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permissionRequest = new PermissionRequest(this);
        BoredomApp.setActivityVisibility(TAG, true);
        PermissionBinding permission = PermissionBinding.inflate((getLayoutInflater()));
        permission.toolbar.setTitle((R.string.permission));
        setSupportActionBar(permission.toolbar);
        permission.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PermissionAdaptor(permissionRequest.getItems(), this);
        permission.recyclerView2.setAdapter(adapter);
        setContentView(permission.getRoot());

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    permissionRequest.getItems().get(7).permission = (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED);
                    adapter.notifyItemChanged(7);
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        BoredomApp.setActivityVisibility(TAG, true);
        if (permissionRequest.areAllTrue()) {
            finish();
        }
    }
    @Override
    public void onPause(){
        super.onPause();
        BoredomApp.setActivityVisibility(TAG, false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        BoredomApp.setActivityVisibility(TAG, false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }

    @Override
    public void onItemClicked(PermissionItem item) {
        if (!item.permission) {
            switch (item.text) {
                case "READ_EXTERNAL_STORAGE":
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                    break;
                case "WRITE_EXTERNAL_STORAGE":
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    break;
                case "ACCESS_FINE_LOCATION":
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    break;
                case "ACCESS_COARSE_LOCATION":
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
                    break;
                case "READ_PHONE_STATE":
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 4);
                    break;
                case "WAKE_LOCK":
                    requestPermissions(new String[]{Manifest.permission.WAKE_LOCK}, 5);
                    break;
                case "ACTIVITY_RECOGNITION":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 6);
                    }
                    break;
                case "POST_NOTIFICATIONS":
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        createNotificationChannel(getResources().getString(R.string.survey_channel_name), Constants.SURVEY_CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH);
                        createNotificationChannel(getResources().getString(R.string.ongoing_channel_name), Constants.ONGOING_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
                        createNotificationChannel(getResources().getString(R.string.uploading_channel_name), Constants.UPLOAD_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
                        createNotificationChannel(getResources().getString(R.string.uploading_background_channel_name), Constants.UPLOAD_DATA_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW);
                        if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                            Intent app_notification_intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                            app_notification_intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                            startActivity(app_notification_intent);
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                        }
                    }
                    break;
                case "ACTION_USAGE_ACCESS_SETTINGS":
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    break;
                case "ACTION_ACCESSIBILITY_SETTINGS":
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case "ACTION_LOCATION_SOURCE_SETTINGS":
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    break;
                case "ACTION_NOTIFICATION_LISTENER_SETTINGS":
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    startService(new Intent(getBaseContext(), NotificationListenService.class));
                    break;
                case "SYSTEM_ALERT_WINDOW":
                    startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName())), RESULT_OK);
                    break;
                case "IS_NOTIFICATION_IMPORTANT_HIGH":
                    Intent app_notification_intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    app_notification_intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivity(app_notification_intent);
            }
        }
    }

    private void createNotificationChannel(CharSequence name, String channel_id, int importance) {
        NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissionRequest.areAllTrue()) {
            finish();
        }else {
            if (grantResults.length != 0) {
                if (grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    permissionRequest.getItems().get(requestCode).permission = true;

                }
            }
            adapter.notifyItemChanged(requestCode);
        }
    }

    @Override
    public void onClick(View v) {

    }
}