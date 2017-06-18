package com.biryanistudio.todo.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import com.biryanistudio.todo.R
import com.biryanistudio.todo.fragments.CompletedFragment
import com.biryanistudio.todo.fragments.PendingFragment

class FragmentPagerAdapter(fm: FragmentManager, private val context: Context) :
        FragmentStatePagerAdapter(fm) {

    private val pendingFragment: PendingFragment = PendingFragment()
    private val completedFragment: CompletedFragment = CompletedFragment()

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return pendingFragment
            1 -> return completedFragment
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        when (position) {
            0 -> return context.getString(R.string.pending_tab_title)
            1 -> return context.getString(R.string.completed_tab_title)
        }
        return super.getPageTitle(position)
    }
}