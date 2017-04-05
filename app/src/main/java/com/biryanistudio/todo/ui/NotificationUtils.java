package com.biryanistudio.todo.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.biryanistudio.todo.R;

/**
 * Created by Aakaash Jois.
 * 05/04/17 - 12:28 PM.
 */

public class NotificationUtils {

    public static void createNotification(Context context, String text) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                100,
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(context.getString(R.string.todo_added))
                        .setContentText(text)
                        .setContentIntent(PendingIntent.getActivity(
                                context,
                                (int) System.currentTimeMillis(),
                                new Intent(context, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                        .build());
    }
}
