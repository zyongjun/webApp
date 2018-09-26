package com.joe.frame.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.joe.base.BaseFragment;
import com.joe.frame.R;
import com.joe.frame.widget.PromptManager;

import java.util.ArrayList;
import java.util.List;

public class BasePermissionFragment extends BaseFragment{
    protected static final int REQUEST_CODE_PERMISSION = 100;

    protected void checkPermissions(String[] permissions) {
        if (permissions != null && permissions.length > 0) {
            if (Build.VERSION.SDK_INT >= 23) {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), permission);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        list.add(permission);
                    }
                }
                if (list.size() > 0) {
                    final String[] array = list.toArray(new String[]{});
                    requestPermissions(array, REQUEST_CODE_PERMISSION);
                } else {
                    onGranted();
                }
            } else {
                onGranted();
            }
        } else {
            onGranted();
        }
    }

    protected void checkPermission(final String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int state = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (state != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission}, REQUEST_CODE_PERMISSION);
            } else {
                onGranted();
            }
        } else {
            onGranted();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            boolean isGranted = true;
            String permission = null;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                    permission = permissions[i];
                    break;
                }
            }
            if (isGranted) {
                onGranted();
            } else {
                onDenied(permission);
            }
        }
    }

    protected void onGranted() {
    }

    protected void onDenied(String permission) {
        String title = "权限申请";
        String confirm = "去设置";
        String cancel = "取消";
        String message;
        String permissionName;
        if (Manifest.permission.RECORD_AUDIO.equalsIgnoreCase(permission)) {
            permissionName = "麦克风";
        } else if (Manifest.permission.CAMERA.equalsIgnoreCase(permission)) {
            permissionName = "相机";
        } else if (Manifest.permission.READ_PHONE_STATE.equalsIgnoreCase(permission)) {
            permissionName = "电话";
        } else if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equalsIgnoreCase(permission)) {
            permissionName = "存储空间";
        } else {
            permissionName = "相关";
        }
        String appName = getString(R.string.app_name);
        message = String.format("在设置-应用-%s-权限中开启%s权限,以正常使用本应用", appName, permissionName);
        PromptManager.getInstance(getActivity()).showDialog(title, message, cancel, confirm, new PromptManager.OnClickBtnCallback() {
            @Override
            public void confirmClick() {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
                startActivity(intent);
            }

            @Override
            public void cancelClick() {

            }
        });
    }
}
