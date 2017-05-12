package com.biryanistudio.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.biryanistudio.todo.database.TasksContract.TaskEntry;

public class DbTransactions {
    private static final String TAG = DbTransactions.class.getSimpleName();

    public static Cursor readPendingTasks(@NonNull final Context context) {
        return readAllTasks(context, new String[]{"yes"});
    }

    public static Cursor readCompletedTasks(@NonNull final Context context) {
        return readAllTasks(context, new String[]{"no"});
    }

    private static Cursor readAllTasks(@NonNull final Context context,
                                       @NonNull final String[] selectionArgs) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        final String[] projection = null;
        final String selection = TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String sortOrder = TaskEntry.COLUMN_NAME_TIME_STAMP + " DESC";
        return database.query(TaskEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    public static long writeTask(@NonNull final Context context, @NonNull final String text) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_TASK, text);
        values.put(TaskEntry.COLUMN_NAME_TIME_STAMP,
                String.valueOf(System.currentTimeMillis()));
        values.put(TaskEntry.COLUMN_NAME_PENDING, "yes");
        final long newRowId = database.insert(TaskEntry.TABLE_NAME, null, values);
        Log.i(TAG, "writeTask: " + newRowId);
        return newRowId;
    }

    public static int updateTaskAsCompleted(@NonNull final Context context,
                                            @NonNull final String timestamp) {
        return updateTaskStatus(context, timestamp, true);
    }

    public static int updateTaskAsPending(@NonNull final Context context,
                                          @NonNull final String timestamp) {
        return updateTaskStatus(context, timestamp, false);
    }

    private static int updateTaskStatus(@NonNull final Context context,
                                        @NonNull final String timestamp,
                                        final boolean pending) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String taskStatus = pending ? "no" : "yes";
        final String currentTaskStatus = pending ? "yes" : "no";
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskEntry.COLUMN_NAME_PENDING, taskStatus);
        final String where = TaskEntry.COLUMN_NAME_PENDING + " = ? AND "
                + TaskEntry.COLUMN_NAME_TIME_STAMP + " = ?";
        final String[] whereArgs = new String[]{currentTaskStatus, timestamp};
        return database.update(TaskEntry.TABLE_NAME, contentValues, where,
                whereArgs);
    }

    public static int updateAllPendingTasksAsCompleted(@NonNull final Context context) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskEntry.COLUMN_NAME_PENDING, "no");
        final String where = TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String[] whereArgs = new String[]{"yes"};
        return database.update(TaskEntry.TABLE_NAME, contentValues, where,
                whereArgs);
    }

    public static int deleteAllCompletedTasks(@NonNull final Context context) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String where = TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String[] whereArgs = new String[]{"no"};
        return database.delete(TaskEntry.TABLE_NAME, where, whereArgs);
    }

    public static int deleteTask(@NonNull final Context context, @NonNull final String timestamp) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String where = TaskEntry.COLUMN_NAME_TIME_STAMP + " = ?";
        final String[] whereArgs = new String[]{timestamp};
        return database.delete(TaskEntry.TABLE_NAME, where, whereArgs);
    }
}