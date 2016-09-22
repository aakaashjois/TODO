package com.biryanistudio.todo.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.biryanistudio.todo.Db.TasksContract;
import com.biryanistudio.todo.Db.TasksDbHelper;

public class DbTransactions {
    private static final String TAG = DbTransactions.class.getSimpleName();

    public static Cursor readPendingTasks(@NonNull final Context context) {
        Log.i(TAG, "readPendingTasks");
        return readAllTasks(context, new String[]{"yes"});
    }

    public static Cursor readCompletedTasks(@NonNull final Context context) {
        Log.i(TAG, "readCompletedTasks");
        return readAllTasks(context, new String[]{"no"});
    }

    public static Cursor readAllTasks(@NonNull final Context context,
                                      @NonNull final String[] selectionArgs) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        final String[] projection = {TasksContract.TaskEntry._ID, TasksContract.TaskEntry.COLUMN_NAME_TASK};
        final String selection = TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String sortOrder = TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP + " DESC";
        final Cursor cursor = database.query(
                TasksContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        Log.i(TAG, "readAllTasks: " + cursor.getCount());
        return cursor;
    }

    public static long writeTasks(@NonNull final Context context, @NonNull final String text) {
        final boolean canProceed = checkForDuplicacy(context, text);
        if (canProceed) {
            final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
            final SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TasksContract.TaskEntry.COLUMN_NAME_TASK, text);
            values.put(TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP,
                    String.valueOf(System.currentTimeMillis()));
            values.put(TasksContract.TaskEntry.COLUMN_NAME_PENDING, "yes");
            final long newRowId = database.insert(TasksContract.TaskEntry.TABLE_NAME, null, values);
            Log.i(TAG, "writeTasks: " + newRowId);
            return newRowId;
        } else {
            Log.i(TAG, "writeTasks: -1");
            return -1;
        }
    }

    public static long updatePendingTasksAsCompleted(@NonNull final Context context) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TasksContract.TaskEntry.COLUMN_NAME_PENDING, "no");
        final String where = TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String[] whereArgs = new String[]{"yes"};
        long updateRowId = database.update(TasksContract.TaskEntry.TABLE_NAME, contentValues, where,
                whereArgs);
        Log.i(TAG, "updatePendingTasksAsCompleted: " + updateRowId);
        return updateRowId;
    }

    public static long deleteCompletedTasks(final Context context) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String selection = TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String[] selectionArgs = new String[]{"no"};
        long delRowId = database.delete(TasksContract.TaskEntry.TABLE_NAME, selection, selectionArgs);
        Log.i(TAG, "deleteTasks: " + delRowId);
        return delRowId;
    }

    private static boolean checkForDuplicacy(@NonNull final Context context, @NonNull final String task) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        final String[] projection = {TasksContract.TaskEntry._ID, TasksContract.TaskEntry.COLUMN_NAME_TASK};
        final String selection = TasksContract.TaskEntry.COLUMN_NAME_TASK + " = ? AND " + TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ? ";
        final String[] selectionArgs = new String[]{task, "yes"};
        final Cursor cursor = database.query(
                TasksContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        return cursor.getCount() == 0;
    }
}
