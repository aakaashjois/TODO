package com.biryanistudio.todo.fragments

import android.database.Cursor
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.widget.ViewSwitcher

import com.biryanistudio.todo.R
import com.biryanistudio.todo.adapters.TasksAdapter
import com.biryanistudio.todo.database.DbTransactions


class FragmentPresenter internal constructor(private val fragment: Fragment,
                                             private val recyclerView: RecyclerView?,
                                             private val emptyTextView: TextView?,
                                             private val emptyViewSwitcher: ViewSwitcher?) {
    private var adapter: TasksAdapter? = null

    val coordinatorLayout: View
        get() = fragment.activity.findViewById<View>(R.id.activity_list)

    private val appropriateCursor: Cursor get() {
        if (fragment is PendingFragment)
            return DbTransactions.readPendingTasks(fragment.context)
        else
            return DbTransactions.readCompletedTasks(fragment.context)
    }

    internal fun setRecyclerViewAdapter() {
        val layoutManager = object : LinearLayoutManager(fragment.context) {
            override fun supportsPredictiveItemAnimations(): Boolean {
                return true
            }
        }
        recyclerView?.layoutManager = layoutManager
        adapter = TasksAdapter(fragment.context, this, appropriateCursor)
        recyclerView?.adapter = adapter
    }

    internal fun setTextViewText() {
        // Set appropriate text for TextView depending on Fragment
        emptyTextView?.setText(if (fragment is PendingFragment)
            R.string.text_not_added_pending_yet
        else
            R.string.text_not_completed)
    }

    internal fun clearPendingTasks() {
        adapter?.updateAllPendingTasksAsCompleted()
    }

    internal fun clearCompletedTasks() {
        adapter?.deleteAllCompletedTasks()

    }

    fun handleEmptyView(showEmptyView: Boolean) {
        if (showEmptyView) {
            if (emptyViewSwitcher?.nextView?.id == R.id.empty_view) emptyViewSwitcher.showNext()
        } else {
            if (emptyViewSwitcher?.nextView?.id == R.id.recycler_view) emptyViewSwitcher.showNext()
        }
    }
}
