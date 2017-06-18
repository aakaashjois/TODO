package com.biryanistudio.todo.userinterface

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import com.biryanistudio.todo.R
import java.util.*

/**
 * Created by Aakaash Jois.
 * 05/04/17 - 12:28 PM.
 */

object UiUtils {

    /**
     * This method is a helper method to create a Snackbar themed as per the app theme.
     * @param context Context using which the Snackbar has to be created.
     * *
     * @param parentLayout The Snackbar attaches to the bottom of parentLayout. parentLayout must
     * *                     implement CoordinatorLayout for the Snackbar to work.
     * *
     * @param action The action text to display in the Snackbar.
     * *
     * @param snackbarLength The amount of time Snackbar has to be displayed.
     * *
     * @return A Snackbar object is returned.
     */
    fun createSnackBar(context: Context, parentLayout: View, action: String,
                       snackbarLength: Int): Snackbar {
        val snackbar = Snackbar.make(parentLayout, action, snackbarLength)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        val textView = snackbarView
                .findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        val actionView = snackbarView
                .findViewById<TextView>(android.support.design.R.id.snackbar_action)
        actionView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        actionView.typeface = Typeface.create("casual", Typeface.BOLD)
        return snackbar
    }

    /**
     * This method creates a notification to tell the user when a new task has been added.
     * @param context Context using which the notification is created.
     * *
     * @param text The text displayed in the notification body.
     */
    fun createNotification(context: Context, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
                    100,
                    NotificationCompat.Builder(context, NotificationChannel.DEFAULT_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_logo)
                            .setContentTitle(context.getString(R.string.todo_added))
                            .setContentText(text)
                            .setContentIntent(PendingIntent.getActivity(
                                    context,
                                    System.currentTimeMillis().toInt(),
                                    Intent(context, MainActivity::class.java),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                            .build())
        } else {
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
                    100,
                    NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_logo)
                            .setContentTitle(context.getString(R.string.todo_added))
                            .setContentText(text)
                            .setContentIntent(PendingIntent.getActivity(
                                    context,
                                    System.currentTimeMillis().toInt(),
                                    Intent(context, MainActivity::class.java),
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                            .build())
        }
    }

    /**
     * This method creates a formatted timestamp to display for the tasks.
     * @param millis The timestamp at which the task was created.
     * *
     * @return Returns the human readable formatted timestamp.
     */
    fun createTimeStamp(context: Context, millis: String): String {
        val result: String
        val calendar = GregorianCalendar.getInstance()
        val taskMillis = java.lang.Long.parseLong(millis)
        val currentMillis = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayMidnightMillis = calendar.timeInMillis
        val yesterdayMidnightMillis = todayMidnightMillis - 86400000
        val deltaMillis = currentMillis - taskMillis
        if (deltaMillis < 1000)
            result = context.getString(R.string.just_now)
        else if (deltaMillis < 50000)
            result = context.getString(R.string.few_seconds_ago)
        else if (deltaMillis < 600000)
            result = context.getString(R.string.few_minutes_ago)
        else if (deltaMillis < 1800000)
            result = context.getString(R.string.half_hour_ago)
        else if (deltaMillis < 3600000)
            result = context.getString(R.string.hour_ago)
        else {
            val day: String
            val time = DateFormat.getTimeFormat(context).format(taskMillis)
            if (taskMillis - todayMidnightMillis < 86400000)
                day = context.getString(R.string.today)
            else if (taskMillis in (yesterdayMidnightMillis + 1)..(todayMidnightMillis - 1))
                day = context.getString(R.string.yesterday)
            else
                day = DateFormat.getLongDateFormat(context).format(taskMillis)
            result = context.getString(R.string.timestamp_format, day, time)
        }
        return result
    }
}