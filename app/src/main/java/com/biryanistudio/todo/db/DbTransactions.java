package com.biryanistudio.todo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

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

    private static Cursor readAllTasks(@NonNull final Context context,
                                       @NonNull final String[] selectionArgs) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        final String[] projection = null;
        final String selection = TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String sortOrder = TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP + " DESC";
        final Cursor cursor = database.query(
                TasksContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        Log.i(TAG, "readAllTasks: " + cursor.getCount());
        return cursor;
    }

    public static long writeTask(@NonNull final Context context, @NonNull final String text) {
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
            Log.i(TAG, "writeTask: " + newRowId);
            return newRowId;
        } else {
            Log.i(TAG, "writeTask: -1");
            return -1;
        }
    }

    public static long updateTaskAsCompleted(@NonNull final Context context,
                                             @NonNull final String task) {
        long updateRowId = updateTaskStatus(context, task, true);
        Log.i(TAG, "updateTaskAsCompleted: " + updateRowId);
        return updateRowId;
    }

    public static long updateTaskAsPending(@NonNull final Context context, @NonNull final String task) {
        long updateRowId = updateTaskStatus(context, task, false);
        Log.i(TAG, "updateTaskAsPending: " + updateRowId);
        return updateRowId;
    }

    private static long updateTaskStatus(@NonNull final Context context, @NonNull final String task,
                                         final boolean pending) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String taskStatus = pending ? "no" : "yes";
        final String currentTaskStatus = pending ? "yes" : "no";
        ContentValues contentValues = new ContentValues();
        contentValues.put(TasksContract.TaskEntry.COLUMN_NAME_PENDING, taskStatus);
        final String where = TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ? AND "
                + TasksContract.TaskEntry.COLUMN_NAME_TASK + " = ?";
        final String[] whereArgs = new String[]{currentTaskStatus, task};
        return database.update(TasksContract.TaskEntry.TABLE_NAME, contentValues, where,
                whereArgs);
    }

    public static long updateAllPendingTasksAsCompleted(@NonNull final Context context) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TasksContract.TaskEntry.COLUMN_NAME_PENDING, "no");
        final String where = TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String[] whereArgs = new String[]{"yes"};
        final long updatedRowIds = database.update(TasksContract.TaskEntry.TABLE_NAME, contentValues, where,
                whereArgs);
        Log.i(TAG, "updateAllPendingTasksAsCompleted: " + updatedRowIds);
        return updatedRowIds;
    }

    public static long deleteAllCompletedTasks(@NonNull final Context context) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String where = TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ?";
        final String[] whereArgs = new String[]{"no"};
        final long deletedRowIds = database.delete(TasksContract.TaskEntry.TABLE_NAME, where, whereArgs);
        Log.i(TAG, "deleteAllCompletedTasks: " + deletedRowIds);
        return deletedRowIds;
    }

    private static boolean checkForDuplicacy(@NonNull final Context context, @NonNull final String task) {
        final TasksDbHelper dbHelper = TasksDbHelper.getInstance(context);
        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        final String[] projection = {TasksContract.TaskEntry._ID, TasksContract.TaskEntry.COLUMN_NAME_TASK};
        final String selection = TasksContract.TaskEntry.COLUMN_NAME_TASK + " = ? AND " + TasksContract.TaskEntry.COLUMN_NAME_PENDING + " = ? ";
        final String[] selectionArgs = new String[]{task, "yes"};
        final Cursor cursor = database.query(
                TasksContract.TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        final boolean duplicate = cursor.getCount() == 0;
        cursor.close();
        Log.i(TAG, "checkForDuplicacy: " + duplicate);
        return duplicate;
    }
}