package com.biryanistudio.todo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

import com.biryanistudio.todo.database.TasksContract.TaskEntry

object DbTransactions {

    fun readPendingTasks(context: Context): Cursor = readAllTasks(context, arrayOf("yes"))

    fun readCompletedTasks(context: Context): Cursor = readAllTasks(context, arrayOf("no"))

    private fun readAllTasks(context: Context, selectionArgs: Array<String>): Cursor
            = TasksDbHelper.getInstance(context).readableDatabase.query(
            TaskEntry.TABLE_NAME,
            null,
            TaskEntry.COLUMN_NAME_PENDING + " = ?",
            selectionArgs,
            null,
            null,
            TaskEntry.COLUMN_NAME_TIME_STAMP + " DESC")

    fun writeTask(context: Context, text: String): Long = TasksDbHelper.getInstance(context)
            .writableDatabase.insert(
            TaskEntry.TABLE_NAME,
            null,
            ContentValues().apply {
                put(TaskEntry.COLUMN_NAME_TASK, text)
                put(TaskEntry.COLUMN_NAME_TIME_STAMP, System.currentTimeMillis().toString())
                put(TaskEntry.COLUMN_NAME_PENDING, "yes")
            })

    fun updateTaskAsCompleted(context: Context, timestamp: String): Int
            = updateTaskStatus(context, timestamp, true)

    fun updateTaskAsPending(context: Context, timestamp: String): Int
            = updateTaskStatus(context, timestamp, false)

    private fun updateTaskStatus(context: Context, timestamp: String, pending: Boolean): Int
            = TasksDbHelper.getInstance(context).writableDatabase.update(
            TaskEntry.TABLE_NAME,
            ContentValues().apply {
                put(TaskEntry.COLUMN_NAME_PENDING, if (pending) "no" else "yes")
            },
            TaskEntry.COLUMN_NAME_PENDING + " = ? AND " + TaskEntry.COLUMN_NAME_TIME_STAMP + " = ?",
            arrayOf(if (pending) "yes" else "no", timestamp))

    fun updateAllPendingTasksAsCompleted(context: Context): Int = TasksDbHelper.getInstance(context)
            .writableDatabase.update(
            TaskEntry.TABLE_NAME,
            ContentValues().apply { put(TaskEntry.COLUMN_NAME_PENDING, "no") },
            TaskEntry.COLUMN_NAME_PENDING + " = ?",
            arrayOf("yes"))


    fun deleteAllCompletedTasks(context: Context): Int = TasksDbHelper.getInstance(context)
            .writableDatabase.delete(
            TaskEntry.TABLE_NAME,
            TaskEntry.COLUMN_NAME_PENDING + " = ?",
            arrayOf("no"))

    fun deleteTask(context: Context, timestamp: String): Int = TasksDbHelper.getInstance(context)
            .writableDatabase.delete(
            TaskEntry.TABLE_NAME,
            TaskEntry.COLUMN_NAME_TIME_STAMP + " = ?",
            arrayOf(timestamp))
}