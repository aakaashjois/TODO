package com.biryanistudio.todo.userinterface

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView

import com.biryanistudio.todo.R
import com.biryanistudio.todo.adapters.FragmentPagerAdapter
import com.biryanistudio.todo.database.DbTransactions
import com.biryanistudio.todo.fragments.BaseFragment
import com.biryanistudio.todo.fragments.PendingFragment
import com.biryanistudio.todo.services.CopyListenerService
import kotlinx.android.synthetic.main.activity_main.*

import kotlinx.android.synthetic.main.view_pager.*

class MainActivity : AppCompatActivity(), View.OnClickListener, TextView.OnEditorActionListener, ViewPager.OnPageChangeListener {

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startService(Intent(this, CopyListenerService::class.java))
        initUi()
    }

    private fun initUi() {
        (findViewById<View>(R.id.tabs) as TabLayout).setupWithViewPager(tasks_view_pager)
        clear.setOnClickListener(this)
        task_input.setOnEditorActionListener(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) clear.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.done_clear_animation, null))
        else clear.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_done_all, null))
        tasks_view_pager.adapter = FragmentPagerAdapter(supportFragmentManager, this)
        tasks_view_pager.addOnPageChangeListener(this)
    }

    override fun onClick(view: View) {
        val currentTab = tasks_view_pager.currentItem
        val action = if (currentTab == 0) getString(R.string.complete_all_tasks)
        else getString(R.string.clear_all_tasks)
        val actionMessage = if (currentTab == 0) getString(R.string.complete_all_tasks_message)
        else getString(R.string.clear_all_tasks_message)
        val snackbar = UiUtils.createSnackBar(this@MainActivity, activity_list, action, Snackbar.LENGTH_LONG)
        snackbar.setAction(R.string.yes) {
            ((tasks_view_pager.adapter as FragmentPagerAdapter).getItem(currentTab) as BaseFragment).clearAllTasks()
            snackbar.dismiss()
            UiUtils.createSnackBar(this@MainActivity, activity_list, actionMessage, Snackbar.LENGTH_SHORT).show()
        }
        snackbar.show()
    }

    override fun onEditorAction(textView: TextView, i: Int, keyEvent: KeyEvent): Boolean {
        if (i == EditorInfo.IME_ACTION_DONE) {
            DbTransactions.writeTask(this@MainActivity, textView.text.toString().trim { it <= ' ' })
            ((tasks_view_pager.adapter as FragmentPagerAdapter).getItem(0) as PendingFragment).updateTasks()
            textView.text = null
            task_input.clearFocus()
            activity_list.requestFocus()
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(textView.windowToken, 0)
        }
        return true
    }

    override fun onPageSelected(position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clear.setImageResource(if (position == 0) R.drawable.clear_done_animation else R.drawable.done_clear_animation)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) (clear.drawable as AnimatedVectorDrawableCompat).start()
            else (clear.drawable as AnimatedVectorDrawable).start()
        } else clear.setImageResource(if (position == 0) R.drawable.ic_done_all else R.drawable.ic_clear_all)
        ((tasks_view_pager.adapter as FragmentPagerAdapter).getItem(position) as BaseFragment).updateTasks()
    }
}