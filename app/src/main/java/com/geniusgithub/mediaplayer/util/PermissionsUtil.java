/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geniusgithub.mediaplayer.util;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import com.geniusgithub.mediaplayer.R;


public class PermissionsUtil {

    public static final String PHONE = permission.READ_PHONE_STATE;
    public static final String CONTACTS = permission.READ_CONTACTS;
    public static final String SMS = permission.SEND_SMS;
    public static final String LOCATION = permission.ACCESS_FINE_LOCATION;
    public static final String STORAGE = permission.WRITE_EXTERNAL_STORAGE;
    public static final String CALENDAR = permission.READ_CALENDAR; 
    public static final String MICROPHONE = permission.RECORD_AUDIO;
    public static final String SENSORS = permission.BODY_SENSORS;
    public static final String CAMERA = permission.CAMERA;

    private static boolean sInitialized = false;
    public static boolean sIsAtLeastM = getApiVersion() >= android.os.Build.VERSION_CODES.M;
    public static final String PACKAGE_URI_PREFIX = "package:";
    public static final String SECURITY_INTENT = "com.android.SETTINGS";

    public static int getApiVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String[] sRequiredPermissions = new String[] {  STORAGE, MICROPHONE, PHONE};
    


    public static boolean hasPhonePermissions(Context context) {
        return hasPermission(context, PHONE);
    }

    public static boolean hasContactsPermissions(Context context) {
        return hasPermission(context, CONTACTS);
    }

    public static boolean hasLocationPermissions(Context context) {
        return hasPermission(context, LOCATION);
    }

    public static boolean hasSmsPermissions(Context context) {
        return hasPermission(context, SMS);
    }

    public static boolean hasStoragePermissions(Context context) {
        return hasPermission(context, STORAGE);
    }

    public static boolean hasCalendarPermissions(Context context) {
        return hasPermission(context, CALENDAR);
    }
    
    public static boolean hasMicrophonePermissions(Context context) {
        return hasPermission(context, MICROPHONE);
    }
    
    public static boolean hasSensorsPermissions(Context context) {
        return hasPermission(context, SENSORS);
    }
    
    public static boolean hasCameraPermissions(Context context) {
        return hasPermission(context, CAMERA);
    }

    
    public static boolean hasNecessaryRequiredPermissions(Context context) {
        return hasPermissions(context, sRequiredPermissions);
    }
    
    @SuppressLint("NewApi")
	public static boolean hasPermission(Context context, String permission) {

        if (!sIsAtLeastM)
            return true;
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;

    }

    public static boolean hasPermissions(Context context,
            final String... permissions) {
        for (final String permission : permissions) {
            if (!hasPermission(context, permission)) {
                return false;
            }
        }
        return true;

    }

 


    

    public static Dialog createPermissionSettingDialog(final Activity activity,
            String forbiddenPermissions) {
        if (forbiddenPermissions.length() > 1) {
            forbiddenPermissions = forbiddenPermissions.substring(0,
                    forbiddenPermissions.length() - 1);
        }
        Dialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.permission_title)
                .setMessage(
                        activity.getString(
                                R.string.permission_setting_guidedialog,
                                forbiddenPermissions))
                .setPositiveButton(R.string.permission_gosetting,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                    final Intent intentnew = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    				Uri.parse(PermissionsUtil.PACKAGE_URI_PREFIX + activity.getPackageName()));
                                    activity.startActivity(intentnew);
                            }
                        })
                .setNegativeButton(R.string.permission_know,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        }).create();
        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                activity.finish();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


}
