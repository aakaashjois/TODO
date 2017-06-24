package com.biryanistudio.todo.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.support.design.widget.Snackbar
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.biryanistudio.todo.R
import com.biryanistudio.todo.database.TodoItem
import com.biryanistudio.todo.userinterface.UiUtils
import io.realm.Realm
import io.realm.RealmBasedRecyclerViewAdapter
import io.realm.RealmResults
import io.realm.RealmViewHolder
import java.util.*

/**
 * Created by aakaashjois on 23/06/17
 */

class TodoAdapter(
        context: Context,
        realmResults: RealmResults<TodoItem>?,
        automaticUpdate: Boolean,
        animateResults: Boolean
) : RealmBasedRecyclerViewAdapter<TodoItem, TodoAdapter.ViewHolder>(
        context, realmResults, automaticUpdate, animateResults) {

    class ViewHolder(itemView: View) : RealmViewHolder(itemView) {
        val task: TextView = itemView.findViewById(R.id.task)
        val time: TextView = itemView.findViewById(R.id.time)
        val checkBox: CheckBox = itemView.findViewById(R.id.check_box)
        val delete: ImageButton = itemView.findViewById(R.id.delete)
    }

    override fun onBindRealmViewHolder(holder: ViewHolder, position: Int) {
        with(realmResults[position]) {
            holder.apply {
                task.text = this@with.task
                time.text = createTimeStamp(this@with.timestamp)
                checkBox.tag = this@with.id
                delete.tag = this@with.id
                checkBox.alpha = 1f
                task.alpha = 1f
                if (!this@with.completed) {
                    checkBox.isChecked = true
                    checkBox.alpha = 0.7f
                    task.alpha = 0.7f
                    task.paintFlags = task.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                    Realm.getDefaultInstance().executeTransaction {
                        it.where(TodoItem::class.java)
                                .equalTo("id", buttonView.tag as Long)
                                .findFirst().completed = isChecked
                        it.close()
                    }
                }
                delete.setOnClickListener {
                    UiUtils.createSnackBar(context,
                            (context as Activity).findViewById(R.id.activity_list),
                            context.getString(R.string.delete_current_todo),
                            Snackbar.LENGTH_SHORT).apply {
                        setAction(context.getString(R.string.yes)) {
                            this.dismiss()
                            Realm.getDefaultInstance().executeTransaction {
                                it.where(TodoItem::class.java)
                                        .equalTo("id", delete.tag as Long)
                                        .findAll().deleteAllFromRealm()
                                it.close()
                            }
                        }
                    }.show()
                }
            }
        }
    }

    override fun onCreateRealmViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item,
            parent, false))

    /**
     * This method creates a formatted timestamp to display for the tasks.
     * @param taskMillis The timestamp at which the task was created.
     * *
     * @return Returns the human readable formatted timestamp.
     */
    private fun createTimeStamp(taskMillis: Long): String {
        val calendar = GregorianCalendar.getInstance()
        val currentMillis = calendar.timeInMillis
        with(calendar) {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        when (currentMillis - taskMillis) {
            in 0..1000 -> return context.getString(R.string.just_now)
            in 1000..50000 -> return context.getString(R.string.few_seconds_ago)
            in 50000..600000 -> return context.getString(R.string.few_minutes_ago)
            in 600000..1800000 -> return context.getString(R.string.half_hour_ago)
            in 1800000..3600000 -> return context.getString(R.string.hour_ago)
            else -> return context.getString(R.string.timestamp_format,
                    if (taskMillis - calendar.timeInMillis < 86400000)
                        context.getString(R.string.today)
                    else if (taskMillis in calendar.timeInMillis - 86400000 + 1..calendar.timeInMillis - 1)
                        context.getString(R.string.yesterday)
                    else
                        DateFormat.getLongDateFormat(context).format(taskMillis),
                    DateFormat.getTimeFormat(context).format(taskMillis))
        }
    }
}