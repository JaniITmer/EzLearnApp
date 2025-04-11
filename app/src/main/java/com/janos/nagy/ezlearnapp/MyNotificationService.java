package com.janos.nagy.ezlearnapp;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

public class MyNotificationService extends NotificationListenerService {
    private static final String TAG = "MyNotificationService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        List<String> allowedApps = Arrays.asList(
                "com.janos.nagy.ezlearnapp"

        );

        if (EzLearnApplication.isAppInForeground() && !allowedApps.contains(packageName)) {
            cancelNotification(sbn.getKey());
            Log.i(TAG, "Értesítés törölve (app előtérben): " + packageName);
        } else {
            Log.i(TAG, "Értesítés megengedve: " + packageName +
                    (EzLearnApplication.isAppInForeground() ? " (app előtérben)" : " (app háttérben)"));
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "Értesítés törölve: " + sbn.getPackageName());
    }
}