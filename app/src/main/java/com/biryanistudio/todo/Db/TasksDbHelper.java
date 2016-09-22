package com.biryanistudio.todo.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

public class TasksDbHelper extends SQLiteOpenHelper {
    private static TasksDbHelper dbHelper;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Tasks.db";

    public static TasksDbHelper getInstance(@NonNull Context context) {
        if (dbHelper == null) {
            return new TasksDbHelper(context);
        } else {
            return dbHelper;
        }
    }

    public static void closeDbHelper() {
        dbHelper.getReadableDatabase().close();
        dbHelper.close();
    }

    private TasksDbHelper(Context context) {
        // If you change the database schema, you must increment the database version.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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