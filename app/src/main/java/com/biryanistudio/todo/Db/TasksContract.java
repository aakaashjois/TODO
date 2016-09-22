package com.biryanistudio.todo.Db;

import android.provider.BaseColumns;

public final class TasksContract {
    private TasksContract() {}

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TasksContract.TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_TASK + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_NAME_TIME_STAMP + TEXT_TYPE + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_TIME_STAMP = "timestamp";
    }
}
