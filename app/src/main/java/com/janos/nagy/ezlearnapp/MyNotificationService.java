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

        // saját alkalmazás csomagneve:
        String myAppPackageName = "com.janos.nagy.ezlearnapp";

        // megengedett alkalmazások
        List<String> allowedApps = Arrays.asList(
                myAppPackageName,           //  EzLearnApp
                "com.android.systemui",     // Rendszer értesítések
                "com.google.android.gm"
        );

        // Ha az alkalmazás nincs a megengedett listán, töröld az értesítést
        if (!allowedApps.contains(packageName)) {
            cancelNotification(sbn.getKey());
        }

        // Naplózás (opcionális)
        Log.i("NotificationService", "Értesítés érkezett: " + packageName + " - " +
                (allowedApps.contains(packageName) ? "Megengedett" : "Törölve"));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Értesítés törlésekor fut
        Log.i(TAG, "Értesítés törölve: " + sbn.getPackageName());
    }
}