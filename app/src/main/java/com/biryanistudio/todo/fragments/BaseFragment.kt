package com.biryanistudio.todo.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewSwitcher

import com.biryanistudio.todo.R
import com.biryanistudio.todo.interfaces.ITasksUpdated


abstract class BaseFragment : Fragment(), ITasksUpdated {
    internal var presenter: FragmentPresenter? = null
    internal var recyclerView: RecyclerView? = null
    internal var emptyTextView: TextView? = null
    internal var emptyViewSwitcher: ViewSwitcher? = null

    override fun onCreateView(inflater: LayoutInflater?,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.list_fragment, container, false)
        if (view != null) {
            recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
            emptyTextView = view.findViewById<View>(R.id.empty_view) as TextView
            emptyViewSwitcher = view.findViewById<View>(R.id.empty_view_switcher) as ViewSwitcher
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        presenter?.setRecyclerViewAdapter()
    }

    abstract override fun clearAllTasks()

    abstract override fun updateTasks()
}