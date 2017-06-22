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
import android.view.View
import android.widget.TextView
import com.biryanistudio.todo.R

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
        (snackbar.view).apply {
            setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            (findViewById<TextView>(android.support.design.R.id.snackbar_text))
                    .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            (findViewById<TextView>(android.support.design.R.id.snackbar_action)).apply {
                setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                typeface = Typeface.create("casual", Typeface.BOLD)
            }
        }
        return snackbar
    }

    /**
     * This method creates a notification to tell the user when a new task has been added.
     * @param context Context using which the notification is created.
     * *
     * @param text The text displayed in the notification body.
     */
    fun createNotification(context: Context, text: String) {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
                100, (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NotificationCompat.Builder(context, NotificationChannel.DEFAULT_CHANNEL_ID)
        else NotificationCompat.Builder(context)).apply {
            setSmallIcon(R.drawable.ic_logo)
            setContentTitle(context.getString(R.string.todo_added))
            setContentText(text)
            setContentIntent(PendingIntent.getActivity(context, System.currentTimeMillis().toInt(),
                    Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
        }.build())
    }

}