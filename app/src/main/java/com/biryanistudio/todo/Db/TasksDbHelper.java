package com.biryanistudio.todo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class TasksDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Tasks.db";
    private static TasksDbHelper dbHelper;

    private TasksDbHelper(Context context) {
        // If you change the database schema, you must increment the database version.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static TasksDbHelper getInstance(@NonNull Context context) {
        if (dbHelper == null) {
            dbHelper = new TasksDbHelper(context);
            return dbHelper;
        } else {
            return dbHelper;
        }
    }

    //TODO: We are not calling this method anywhere.
    public static void closeDbHelper() {
        dbHelper.getReadableDatabase().close();
        dbHelper.close();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TasksContract.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(TasksContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}