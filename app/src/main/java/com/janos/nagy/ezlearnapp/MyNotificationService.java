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

        if (!allowedApps.contains(packageName)) {
            // Törlés az értesítési sávból
            cancelNotification(sbn.getKey());

            // Naplózás a részletekhez
            Log.i(TAG, "Értesítés: " + packageName +
                    ", Prioritás: " + sbn.getNotification().priority +
                    ", Csatorna: " + sbn.getNotification().getChannelId());
        } else {
            Log.i(TAG, "Megengedett értesítés: " + packageName);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG, "Értesítés törölve: " + sbn.getPackageName());
    }
}