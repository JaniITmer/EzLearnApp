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

        String myAppPackageName = "com.janos.nagy.ezlearnapp";

        List<String> allowedApps = Arrays.asList(
                myAppPackageName,
                "com.android.systemui",
                "com.google.android.gm"
        );

        if (!allowedApps.contains(packageName)) {
            cancelNotification(sbn.getKey());
        }

        Log.i("NotificationService", "Értesítés érkezett: " + packageName + " - " +
                (allowedApps.contains(packageName) ? "Megengedett" : "Törölve"));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "Értesítés törölve: " + sbn.getPackageName());
    }
}