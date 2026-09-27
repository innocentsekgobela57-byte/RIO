package com.rio.v4;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
public class NotificationService extends NotificationListenerService {
    @Override public void onNotificationPosted(StatusBarNotification sbn){
        // Ready to read aloud - safe no crash if permission granted
        String text = sbn.getNotification().extras.getString("android.text");
        // Will be used later for "read notifications"
    }
}
