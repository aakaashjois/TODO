package com.biryanistudio.todo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import com.biryanistudio.todo.database.TasksContract.TaskEntry

object DbTransactions {

    fun readPendingTasks(context: Context): Cursor {
        return readAllTasks(context, arrayOf("yes"))
    }

    fun readCompletedTasks(context: Context): Cursor {
        return readAllTasks(context, arrayOf("no"))
    }

    private fun readAllTasks(context: Context,
                             selectionArgs: Array<String>): Cursor {
        val dbHelper = TasksDbHelper.getInstance(context)
        val database = dbHelper.readableDatabase
        val projection: Array<String>? = null
        val selection = TaskEntry.COLUMN_NAME_PENDING + " = ?"
        val sortOrder = TaskEntry.COLUMN_NAME_TIME_STAMP + " DESC"
        return database.query(TaskEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs, null, null,
                sortOrder)
    }

    fun writeTask(context: Context, text: String): Long {
        val dbHelper = TasksDbHelper.getInstance(context)
        val database = dbHelper.writableDatabase
        val values = ContentValues()
        values.put(TaskEntry.COLUMN_NAME_TASK, text)
        values.put(TaskEntry.COLUMN_NAME_TIME_STAMP,
                System.currentTimeMillis().toString())
        values.put(TaskEntry.COLUMN_NAME_PENDING, "yes")
        val newRowId = database.insert(TaskEntry.TABLE_NAME, null, values)
        return newRowId
    }

    fun updateTaskAsCompleted(context: Context,
                              timestamp: String): Int {
        return updateTaskStatus(context, timestamp, true)
    }

    fun updateTaskAsPending(context: Context,
                            timestamp: String): Int {
        return updateTaskStatus(context, timestamp, false)
    }

    private fun updateTaskStatus(context: Context,
                                 timestamp: String,
                                 pending: Boolean): Int {
        val dbHelper = TasksDbHelper.getInstance(context)
        val database = dbHelper.writableDatabase
        val taskStatus = if (pending) "no" else "yes"
        val currentTaskStatus = if (pending) "yes" else "no"
        val contentValues = ContentValues()
        contentValues.put(TaskEntry.COLUMN_NAME_PENDING, taskStatus)
        val where = TaskEntry.COLUMN_NAME_PENDING + " = ? AND " +
                TaskEntry.COLUMN_NAME_TIME_STAMP + " = ?"
        val whereArgs = arrayOf(currentTaskStatus, timestamp)
        return database.update(TaskEntry.TABLE_NAME, contentValues, where,
                whereArgs)
    }

    fun updateAllPendingTasksAsCompleted(context: Context): Int {
        val dbHelper = TasksDbHelper.getInstance(context)
        val database = dbHelper.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(TaskEntry.COLUMN_NAME_PENDING, "no")
        val where = TaskEntry.COLUMN_NAME_PENDING + " = ?"
        val whereArgs = arrayOf("yes")
        return database.update(TaskEntry.TABLE_NAME, contentValues, where,
                whereArgs)
    }

    fun deleteAllCompletedTasks(context: Context): Int {
        val dbHelper = TasksDbHelper.getInstance(context)
        val database = dbHelper.writableDatabase
        val where = TaskEntry.COLUMN_NAME_PENDING + " = ?"
        val whereArgs = arrayOf("no")
        return database.delete(TaskEntry.TABLE_NAME, where, whereArgs)
    }

    fun deleteTask(context: Context, timestamp: String): Int {
        val dbHelper = TasksDbHelper.getInstance(context)
        val database = dbHelper.writableDatabase
        val where = TaskEntry.COLUMN_NAME_TIME_STAMP + " = ?"
        val whereArgs = arrayOf(timestamp)
        return database.delete(TaskEntry.TABLE_NAME, where, whereArgs)
    }
}