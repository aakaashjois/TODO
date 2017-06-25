package com.biryanistudio.todo.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Created by Aakaash Jois.
 * 12/05/17 - 9:19 AM.
 */

class TodoStartCopyListenerServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED, ignoreCase = true) ||
                intent.action.equals(Intent.ACTION_PACKAGE_REPLACED, ignoreCase = true))
            context.startService(Intent(context, CopyListenerService::class.java))
    }
}
