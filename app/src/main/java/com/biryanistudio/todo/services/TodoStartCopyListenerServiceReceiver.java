package com.biryanistudio.todo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by aakaashjois on 12/05/17.
 */

public class TodoStartCopyListenerServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED) ||
                intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_REPLACED))
            context.startService(new Intent(context, CopyListenerService.class));
    }
}
