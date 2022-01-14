package com.example.telefixmain.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.telefixmain.R;

import java.util.Date;

public class NotificationHandler {
    public static void sendProgressTrackingNotification(Context context, String title, String content) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NotificationInstance.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(bitmap)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(getNotificationId(), notification.build());
    }

    public static int getNotificationId() {
        return (int) new Date().getTime();
    }
}
