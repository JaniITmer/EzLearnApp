package com.janos.nagy.ezlearnapp;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class MyNotificationService extends NotificationListenerService {
    private static final String TAG = "MyNotificationService";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // Új értesítés érkezett
        String packageName = sbn.getPackageName();
        Log.i(TAG, "Értesítés érkezett: " + packageName);


        if ("com.facebook.katana".equals(packageName) || "com.instagram.android".equals(packageName)) {
            cancelNotification(sbn.getKey()); // Értesítés eltávolítása
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Értesítés törlésekor fut
        Log.i(TAG, "Értesítés törölve: " + sbn.getPackageName());
    }
}