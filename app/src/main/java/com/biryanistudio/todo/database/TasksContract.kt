package com.biryanistudio.todo.database

import android.provider.BaseColumns

object TasksContract {

    internal val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME
    private val TEXT_TYPE = " TEXT"
    private val COMMA_SEP = ","
    internal val SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TasksContract.TaskEntry.TABLE_NAME + " (" +
                    TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskEntry.COLUMN_NAME_TASK + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_NAME_TIME_STAMP + TEXT_TYPE + COMMA_SEP +
                    TaskEntry.COLUMN_NAME_PENDING + TEXT_TYPE + " )"

    class TaskEntry : BaseColumns {
        companion object {
            internal val TABLE_NAME = "tasks"
            val _ID = BaseColumns._ID
            val _COUNT = BaseColumns._COUNT
            val COLUMN_NAME_TASK = "task"
            val COLUMN_NAME_TIME_STAMP = "timestamp"
            val COLUMN_NAME_PENDING = "pending"
        }
    }
}
