package com.biryanistudio.todo.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.biryanistudio.todo.R;

import java.util.Date;

/**
 * Created by Aakaash Jois.
 * 05/04/17 - 12:28 PM.
 */

public class UiUtils {

    static Snackbar createSnackBar(Context context, View parentLayout, final String action, final int snackbarLength) {
        Snackbar snackbar = Snackbar.make(parentLayout, action, snackbarLength);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        TextView textView = (TextView) snackbarView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        TextView actionView = (TextView) snackbarView
                .findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        actionView.setTypeface(Typeface.create("casual", Typeface.BOLD));
        return snackbar;
    }

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

    public static String createTimeStamp(Context context, String time) {
        Date date = new Date(Long.parseLong(time));
        java.text.DateFormat dateFormat = DateFormat.getLongDateFormat(context);
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        return context.getString(R.string.timestamp_format,
                dateFormat.format(date),
                timeFormat.format(date));
    }
}
