package com.biryanistudio.todo;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.biryanistudio.todo.Db.TasksContract;
import com.biryanistudio.todo.Db.TasksDbHelper;

/**
 * Created by Sravan on 19-Sep-16.
 */
public class CopyListenerService extends Service implements
        ClipboardManager.OnPrimaryClipChangedListener {
    private final String TAG = CopyListenerService.class.getSimpleName();
    private ClipboardManager clipboardManager;
    // TODO Fix multiple calls

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
        TasksDbHelper dbHelper = new TasksDbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TasksContract.TaskEntry.COLUMN_NAME_TASK, text);
        values.put(TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP, String.valueOf(System.currentTimeMillis()));
        long newRowId = database.insert(TasksContract.TaskEntry.TABLE_NAME, null, values);
        Log.i(TAG, "saveTextToDatabase: " + newRowId);
        database.close();
        dbHelper.close();
        clipboardManager.addPrimaryClipChangedListener(this);
    }
}
