package com.biryanistudio.todo.userinterface

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * Created by Aakaash Jois.
 * 25/06/17 - 2:10 PM.
 */

class TodoRecyclerView : RecyclerView {

    var mEmptyView: View? = null

    private val mDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            super.onChanged()
            updateEmptyView()
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        if (getAdapter() != null) getAdapter().unregisterAdapterDataObserver(mDataObserver)
        adapter.registerAdapterDataObserver(mDataObserver)
        super.setAdapter(adapter)
        updateEmptyView()
    }

    private fun updateEmptyView() {
        if (mEmptyView != null && adapter != null) {
            with(adapter.itemCount == 0) {
                mEmptyView?.visibility = if (this) View.VISIBLE else View.GONE
                visibility = if (this) View.GONE else View.VISIBLE
            }
        }
    }
}