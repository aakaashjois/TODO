package com.biryanistudio.todo.database;

import android.provider.BaseColumns;

public final class TasksContract {

    static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TasksContract.TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_TASK + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_NAME_TIME_STAMP + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_NAME_PENDING + TEXT_TYPE + " )";

    private TasksContract() {
    }

    public static class TaskEntry implements BaseColumns {
        static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_TIME_STAMP = "timestamp";
        public static final String COLUMN_NAME_PENDING = "pending";
    }
}
