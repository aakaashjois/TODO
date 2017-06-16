package com.biryanistudio.todo.userinterface;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
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

    /**
     * This method is a helper method to create a Snackbar themed as per the app theme.
     * @param context Context using which the Snackbar has to be created.
     * @param parentLayout The Snackbar attaches to the bottom of parentLayout. parentLayout must
     *                     implement CoordinatorLayout for the Snackbar to work.
     * @param action The action text to display in the Snackbar.
     * @param snackbarLength The amount of time Snackbar has to be displayed.
     * @return A Snackbar object is returned.
     */
    public static Snackbar createSnackBar(final Context context,
                                   final View parentLayout,
                                   final String action,
                                   final int snackbarLength) {
        Snackbar snackbar = Snackbar.make(parentLayout, action, snackbarLength);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        TextView textView = snackbarView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        TextView actionView = snackbarView
                .findViewById(android.support.design.R.id.snackbar_action);
        actionView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        actionView.setTypeface(Typeface.create("casual", Typeface.BOLD));
        return snackbar;
    }

    /**
     * This method creates a notification to tell the user when a new task has been added.
     * @param context Context using which the notification is created.
     * @param text The text displayed in the notification body.
     */
    public static void createNotification(final Context context, final String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                    100,
                    new NotificationCompat.Builder(context, NotificationChannel.DEFAULT_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_logo)
                            .setContentTitle(context.getString(R.string.todo_added))
                            .setContentText(text)
                            .setContentIntent(PendingIntent.getActivity(
                                    context,
                                    (int) System.currentTimeMillis(),
                                    new Intent(context, MainActivity.class),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                            .build());
        } else {
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

    /**
     * This method creates a formatted timestamp to display for the tasks.
     * @param millis The timestamp at which the task was created.
     * @return Returns the human readable formatted timestamp.
     */
    public static String createTimeStamp(final Context context, final String millis) {
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
        long deltaMillis = currentMillis - taskMillis;
        if (deltaMillis < 1000)
            result = context.getString(R.string.just_now);
        else if (deltaMillis < 50000)
            result = context.getString(R.string.few_seconds_ago);
        else if (deltaMillis < 600000)
            result = context.getString(R.string.few_minutes_ago);
        else if (deltaMillis < 1800000)
            result = context.getString(R.string.half_hour_ago);
        else if (deltaMillis < 3600000)
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