package com.biryanistudio.todo.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import com.biryanistudio.todo.R
import com.biryanistudio.todo.fragments.TodoFragment

class TodoFragmentPagerAdapter(fm: FragmentManager, private val context: Context) :
        FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? = when (position) {
        0 -> TodoFragment.newInstance(position)
        1 -> TodoFragment.newInstance(position)
        else -> null
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> context.getString(R.string.pending_tab_title)
        1 -> context.getString(R.string.completed_tab_title)
        else -> super.getPageTitle(position)
    }
}