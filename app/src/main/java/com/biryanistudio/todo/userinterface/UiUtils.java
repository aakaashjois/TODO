package com.biryanistudio.todo.userinterface;

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

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    public static String createTimeStamp(Context context, String millis) {
        String result;
        Calendar calendar = GregorianCalendar.getInstance();
        long taskMillis = Long.parseLong(millis);
        long currentMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long todayMidnightMillis = calendar.getTimeInMillis();
        long yesterdayMidnightMillis = todayMidnightMillis - 86400000;
        if (currentMillis - taskMillis < 1000)
            result = context.getString(R.string.just_now);
        else if (currentMillis - taskMillis < 50000)
            result = context.getString(R.string.few_seconds_ago);
        else if (currentMillis - taskMillis < 600000)
            result = context.getString(R.string.few_minutes_ago);
        else if (currentMillis - taskMillis < 1800000)
            result = context.getString(R.string.half_hour_ago);
        else if (currentMillis - taskMillis < 3600000)
            result = context.getString(R.string.hour_ago);
        else {
            String day;
            String time = DateFormat.getTimeFormat(context).format(taskMillis);
            if (taskMillis - todayMidnightMillis < 86400000)
                day = context.getString(R.string.today);
            else if (taskMillis > yesterdayMidnightMillis && taskMillis < todayMidnightMillis)
                day = context.getString(R.string.yesterday);
            else
                day = DateFormat.getLongDateFormat(context).format(taskMillis);
            result = context.getString(R.string.timestamp_format, day, time);
        }
        return result;
    }
}