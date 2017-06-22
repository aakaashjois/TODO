package com.biryanistudio.todo.fragments

import android.os.Bundle
import android.view.View

class CompletedFragment : BaseFragment() {

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = FragmentPresenter(this, recyclerView, emptyTextView, emptyViewSwitcher)
        presenter?.setTextViewText()
    }

    override fun clearAllTasks() {
        presenter?.clearCompletedTasks()
    }

    override fun updateTasks() {
        presenter?.setRecyclerViewAdapter()
        presenter?.setTextViewText()
    }
}