package com.biryanistudio.todo.Utils;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

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
                Log.i(TAG, "onPrimaryClipChanged: " + clipText);
                saveTextToDatabase(clipText);
            }
        }
    }

    private void saveTextToDatabase(String text) {
        long newRowId = DbTransactions.writeTask(this, text);
        if (newRowId != -1) {
            Toast toast = Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        clipboardManager.addPrimaryClipChangedListener(this);
    }
}
