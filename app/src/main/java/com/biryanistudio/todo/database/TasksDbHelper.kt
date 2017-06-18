package com.biryanistudio.todo.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class TasksDbHelper private constructor(context: Context) : SQLiteOpenHelper(context,
        TasksDbHelper.DATABASE_NAME, null, TasksDbHelper.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TasksContract.SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(TasksContract.SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "Tasks.db"
        private var dbHelper: TasksDbHelper? = null

        fun getInstance(context: Context): TasksDbHelper {
            if (dbHelper == null) dbHelper = TasksDbHelper(context)
            return dbHelper as TasksDbHelper
        }
    }
}