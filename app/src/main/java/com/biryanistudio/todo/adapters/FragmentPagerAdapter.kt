package com.biryanistudio.todo.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import com.biryanistudio.todo.R
import com.biryanistudio.todo.fragments.TodoFragment

class FragmentPagerAdapter(fm: FragmentManager, private val context: Context) :
        FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return TodoFragment.newInstance(position)
            1 -> return TodoFragment.newInstance(position)
            else -> return null
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return context.getString(R.string.pending_tab_title)
            1 -> return context.getString(R.string.completed_tab_title)
            else -> return super.getPageTitle(position)
        }
    }
}