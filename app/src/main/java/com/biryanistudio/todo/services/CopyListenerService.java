package com.biryanistudio.todo.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.db.DbTransactions;
import com.biryanistudio.todo.ui.MainActivity;

public class CopyListenerService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener {
    private final String TAG = CopyListenerService.class.getSimpleName();
    private ClipboardManager clipboardManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(this);
        return START_STICKY;
    }

    @Override
    public void onPrimaryClipChanged() {
        ClipData clipData = clipboardManager.getPrimaryClip();
        clipboardManager.removePrimaryClipChangedListener(this);
        if (clipData.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
            String clipText = String.valueOf(clipData.getItemAt(0).getText());
            if (clipText.contains("//TODO") || clipText.contains("// TODO")
                    || clipText.contains("// todo") || clipText.contains("//todo")) {
                clipText = clipText.contains("//TODO") ? clipText.replace("//TODO", "") :
                        clipText.contains("// TODO") ? clipText.replace("// TODO", "") :
                                clipText.contains("//todo") ? clipText.replace("//todo", "") :
                                        clipText.replace("// todo", "");
                clipText = clipText.trim();
                if (!(clipText.trim().isEmpty())) {
                    Log.i(TAG, "onPrimaryClipChanged: " + clipText);
                    saveTextToDatabase(clipText);
                }
            }
        }
    }

    private void saveTextToDatabase(String text) {
        long newRowId = DbTransactions.writeTask(this, text);
        if(newRowId != -1)
            createNotification(text);
        clipboardManager.addPrimaryClipChangedListener(this);
    }

    private void createNotification(String text) {
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                100,
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_new)
                        .setContentTitle(getString(R.string.todo_added))
                        .setContentText(text)
                        .setContentIntent(PendingIntent.getActivity(
                                this,
                                (int) System.currentTimeMillis(),
                                new Intent(this, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                        .build());
    }
}