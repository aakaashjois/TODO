package com.biryanistudio.todo.adapters

import android.content.Context
import android.database.Cursor
import android.graphics.Paint
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.TextView
import com.biryanistudio.todo.R
import com.biryanistudio.todo.database.DbTransactions
import com.biryanistudio.todo.database.TasksContract
import com.biryanistudio.todo.fragments.FragmentPresenter
import com.biryanistudio.todo.userinterface.UiUtils
import java.util.*

class TasksAdapter(private val context: Context,
                   private val presenter: FragmentPresenter,
                   cursor: Cursor?) : RecyclerView.Adapter<TasksAdapter.ViewHolder>(),
        CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task: TextView = itemView.findViewById(R.id.task)
        val time: TextView = itemView.findViewById(R.id.time)
        val checkBox: CheckBox = itemView.findViewById(R.id.check_box)
        val delete: ImageButton = itemView.findViewById(R.id.delete)
    }

    /**
     * [<] of all tasks.
     */
    private val tasks = ArrayList<String>()

    /**
     * [<] of status of all tasks.
     */
    private val statuses = ArrayList<String>()

    /**
     * [<] of timestamps of all tasks.
     */
    private val timestamps = ArrayList<String>()

    init {
        convertCursorToList(cursor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.task.alpha = 1f
        holder.time.text = createTimeStamp(context, timestamps[position])
        holder.task.text = tasks[position]
        holder.checkBox.tag = timestamps[position]
        holder.delete.tag = timestamps[position]
        holder.checkBox.alpha = 1f
        holder.task.alpha = 1f
        if (statuses[position].equals("no", ignoreCase = true)) {
            holder.checkBox.isChecked = true
            holder.checkBox.alpha = 0.7f
            holder.task.alpha = 0.7f
            holder.task.paintFlags = holder.task.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
        holder.checkBox.setOnCheckedChangeListener(this)
        holder.delete.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        handleItemChecked(buttonView.tag as String)
    }

    override fun onClick(v: View) {
        val snackbar = UiUtils.createSnackBar(context, presenter.coordinatorLayout,
                context.getString(R.string.delete_current_todo), Snackbar.LENGTH_SHORT)
        snackbar.setAction(context.getString(R.string.yes)) {
            snackbar.dismiss()
            handleItemDeleted(v.tag as String)
        }
        snackbar.show()
    }

    /**
     * Moves all the tasks from Pending Fragment to Completed Fragment and marks them as completed.
     *
     *
     * Uses [TasksAdapter.clearAllLists] to clear the [TasksAdapter.tasks],
     * [TasksAdapter.statuses] and [TasksAdapter.timestamps].
     *
     *
     * Calls [DbTransactions.updateAllPendingTasksAsCompleted] to update the tasks in
     * the database.
     */
    fun updateAllPendingTasksAsCompleted() {
        clearAllLists()
        val updated = DbTransactions.updateAllPendingTasksAsCompleted(context)
        notifyItemRangeRemoved(0, updated)
        showEmptyViewIfNoTasksPresent()
    }

    /**
     * Deletes all the tasks from Completed Fragment.
     *
     *
     * Uses [TasksAdapter.clearAllLists] to clear the [TasksAdapter.tasks],
     * [TasksAdapter.statuses] and [TasksAdapter.timestamps].
     *
     *
     * Calls [DbTransactions.deleteAllCompletedTasks] to delete all the completed
     * tasks in the database.
     */
    fun deleteAllCompletedTasks() {
        clearAllLists()
        val updated = DbTransactions.deleteAllCompletedTasks(context)
        notifyItemRangeRemoved(0, updated)
        showEmptyViewIfNoTasksPresent()
    }

    /**
     * Wrapper method which calls [FragmentPresenter.handleEmptyView] if no tasks are
     * present.
     */
    private fun showEmptyViewIfNoTasksPresent() {
        presenter.handleEmptyView(statuses.size == 0)
    }

    /**
     * Converts the cursor into [TasksAdapter.tasks], [TasksAdapter.statuses] and
     * [TasksAdapter.timestamps].
     * Calls [FragmentPresenter.handleEmptyView] to display or hide the empty view
     * based on the Cursor.

     * @param cursor Cursor passed to [TasksAdapter] when instantiated.
     */
    private fun convertCursorToList(cursor: Cursor?) {
        if (cursor != null && cursor.moveToFirst()) {
            presenter.handleEmptyView(false)
            do {
                tasks.add(cursor.getString(cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)))
                statuses.add(cursor.getString(cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_PENDING)))
                timestamps.add(cursor.getString(cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP)))
            } while (cursor.moveToNext())
            cursor.close()
        } else presenter.handleEmptyView(true)
    }

    private fun handleItemChecked(timestamp: String) {
        // Update task as completed, or remove from database (if already completed)
        val itemPosition = timestamps.indexOf(timestamp)
        val isPending = statuses[itemPosition]
        deleteItemInAllLists(itemPosition)
        if (isPending.equals("yes", ignoreCase = true)) DbTransactions.updateTaskAsCompleted(context, timestamp)
        else DbTransactions.updateTaskAsPending(context, timestamp)
        notifyItemRemoved(itemPosition)
        showEmptyViewIfNoTasksPresent()
    }

    private fun handleItemDeleted(timestamp: String) {
        val itemPosition = timestamps.indexOf(timestamp)
        deleteItemInAllLists(itemPosition)
        DbTransactions.deleteTask(context, timestamp)
        notifyItemRemoved(itemPosition)
        showEmptyViewIfNoTasksPresent()
    }

    /**
     * Clears [TasksAdapter.tasks], [TasksAdapter.statuses] and
     * [TasksAdapter.timestamps].
     */
    private fun clearAllLists() {
        tasks.clear()
        statuses.clear()
        timestamps.clear()
    }

    /**
     * Remove a single item from [TasksAdapter.tasks], [TasksAdapter.statuses] and
     * [TasksAdapter.timestamps].

     * @param itemPosition The index of the item to be removed.
     */
    private fun deleteItemInAllLists(itemPosition: Int) {
        tasks.removeAt(itemPosition)
        statuses.removeAt(itemPosition)
        timestamps.removeAt(itemPosition)
    }

    /**
     * This method creates a formatted timestamp to display for the tasks.
     * @param millis The timestamp at which the task was created.
     * *
     * @return Returns the human readable formatted timestamp.
     */
    private fun createTimeStamp(context: Context, millis: String): String {
        val result: String
        val calendar = GregorianCalendar.getInstance()
        val taskMillis = java.lang.Long.parseLong(millis)
        val currentMillis = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayMidnightMillis = calendar.timeInMillis
        val yesterdayMidnightMillis = todayMidnightMillis - 86400000
        when (currentMillis - taskMillis) {
            in 0..1000 -> result = context.getString(R.string.just_now)
            in 1000..50000 -> result = context.getString(R.string.few_seconds_ago)
            in 50000..600000 -> result = context.getString(R.string.few_minutes_ago)
            in 600000..1800000 -> result = context.getString(R.string.half_hour_ago)
            in 1800000..3600000 -> result = context.getString(R.string.hour_ago)
            else -> {
                val day: String
                val time = DateFormat.getTimeFormat(context).format(taskMillis)
                if (taskMillis - todayMidnightMillis < 86400000)
                    day = context.getString(R.string.today)
                else if (taskMillis in (yesterdayMidnightMillis + 1)..(todayMidnightMillis - 1))
                    day = context.getString(R.string.yesterday)
                else
                    day = DateFormat.getLongDateFormat(context).format(taskMillis)
                result = context.getString(R.string.timestamp_format, day, time)
            }
        }
        return result
    }
}
