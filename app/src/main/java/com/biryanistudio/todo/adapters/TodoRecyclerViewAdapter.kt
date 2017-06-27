package com.biryanistudio.todo.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.biryanistudio.todo.R
import com.biryanistudio.todo.database.TodoItem
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmRecyclerViewAdapter
import java.util.*

/**
 * Created by Aakaash Jois.
 * 25/06/17 - 3:39 PM.
 */

class TodoRecyclerViewAdapter(private val context: Context,
                              data: OrderedRealmCollection<TodoItem>)
    : RealmRecyclerViewAdapter<TodoItem, TodoRecyclerViewAdapter.TodoViewHolder>(data, true) {

    init {
        setHasStableIds(true)
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task: TextView = itemView.findViewById(R.id.task)
        val time: TextView = itemView.findViewById(R.id.time)
        val checkBox: ImageButton = itemView.findViewById(R.id.check_box)
        val delete: ImageButton = itemView.findViewById(R.id.delete)
        val reminder: ImageButton = itemView.findViewById(R.id.reminder)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(data?.get(position)) {
            holder.apply {
                task.text = this@with?.task
                time.text = createTimeStamp(this@with?.timestamp)
                checkBox.tag = this@with?.id
                delete.tag = this@with?.id
                when (this@with?.completed) {
                    0 -> {
                        checkBox.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check_circle))
                        checkBox.alpha = 1f
                        task.alpha = 1f
                    }
                    1 -> {
                        checkBox.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_uncheck_circle))
                        task.alpha = 0.7f
                        task.paintFlags = task.paintFlags or STRIKE_THRU_TEXT_FLAG
                    }
                }
                checkBox.setOnClickListener { view ->
                    Realm.getDefaultInstance().use {
                        it.executeTransactionAsync {
                            it.where(TodoItem::class.java)
                                    .equalTo(TodoItem.ID, view.tag.toString())
                                    .findFirst().apply {
                                when (completed) {
                                    0 -> completed = 1
                                    1 -> completed = 0
                                }
                            }
                        }
                    }
                }
                delete.setOnClickListener { view ->
                    com.biryanistudio.todo.TodoApplication.createSnackBar(context,
                            (context as Activity).findViewById(
                                    com.biryanistudio.todo.R.id.activity_list),
                            context.getString(com.biryanistudio.todo.R.string.delete_current_todo),
                            android.support.design.widget.Snackbar.LENGTH_SHORT).apply {
                        setAction(context.getString(com.biryanistudio.todo.R.string.yes)) {
                            this.dismiss()
                            Realm.getDefaultInstance().use {
                                it.executeTransactionAsync {
                                    it.where(TodoItem::class.java)
                                            .equalTo(TodoItem.ID, view.tag.toString())
                                            .findFirst().deleteFromRealm()
                                }
                            }
                        }
                    }.show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder
            = TodoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item,
            parent, false))

    /**
     * This method creates a formatted timestamp to display for the tasks.
     * @param taskMillis The timestamp at which the task was created.
     * *
     * @return Returns the human readable formatted timestamp.
     */
    private fun createTimeStamp(taskMillis: Long?): String? {
        val calendar = GregorianCalendar.getInstance()
        val currentMillis = calendar.timeInMillis
        with(calendar) {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return if (taskMillis != null)
            when (currentMillis - taskMillis) {
                in 0..1000 -> context.getString(R.string.just_now)
                in 1000..50000 -> context.getString(R.string.few_seconds_ago)
                in 50000..600000 -> context.getString(R.string.few_minutes_ago)
                in 600000..1800000 -> context.getString(R.string.half_hour_ago)
                in 1800000..3600000 -> context.getString(R.string.hour_ago)
                else -> context.getString(R.string.timestamp_format,
                        if (taskMillis - calendar.timeInMillis < 86400000)
                            context.getString(R.string.today)
                        else if (taskMillis in calendar.timeInMillis - 86400000 + 1
                                ..calendar.timeInMillis - 1)
                            context.getString(R.string.yesterday)
                        else DateFormat.getLongDateFormat(context).format(taskMillis),
                        DateFormat.getTimeFormat(context).format(taskMillis))
            }
        else null
    }
}
