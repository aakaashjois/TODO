package com.biryanistudio.todo.userinterface

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.biryanistudio.todo.R
import com.biryanistudio.todo.TodoApplication
import com.biryanistudio.todo.adapters.TodoFragmentPagerAdapter
import com.biryanistudio.todo.database.TodoItem
import com.biryanistudio.todo.services.CopyListenerService
import com.vicpin.krealmextensions.delete
import com.vicpin.krealmextensions.query
import com.vicpin.krealmextensions.save
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_pager.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) clear.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.done_clear_animation, null))
        else clear.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_done_all, null))

        clear.setOnClickListener {
            val currentTab = tasks_view_pager.currentItem
            val action = if (currentTab == 0) getString(R.string.complete_all_tasks)
            else getString(R.string.clear_all_tasks)
            val actionMessage = if (currentTab == 0) getString(R.string.complete_all_tasks_message)
            else getString(R.string.clear_all_tasks_message)
            TodoApplication.createSnackBar(this@MainActivity, activity_list,
                    action, Snackbar.LENGTH_LONG).apply {
                setAction(R.string.yes) {
                    when (currentTab) {
                        0 -> thread {
                            TodoItem().query { query -> query.equalTo(TodoItem.COMPLETED, 0) }
                                    .forEach {
                                        it.completed = 1
                                        it.save()
                                    }
                            this.dismiss()
                            TodoApplication.createSnackBar(this@MainActivity, activity_list,
                                    actionMessage, Snackbar.LENGTH_SHORT).show()
                        }
                        1 -> thread {
                            TodoItem().delete { query -> query.equalTo(TodoItem.COMPLETED, 1) }
                            this.dismiss()
                            TodoApplication.createSnackBar(this@MainActivity, activity_list,
                                    actionMessage, Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }.show()
        }

        task_input.setOnEditorActionListener({ textView, i, _ ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                thread {
                    TodoItem().apply {
                        completed = 0
                        task = textView.text.toString().trim { it <= ' ' }
                        timestamp = System.currentTimeMillis()
                    }.save()
                }
                textView.text = null
                task_input.clearFocus()
                activity_list.requestFocus()
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(textView.windowToken, 0)
            }
            true
        })

        tasks_view_pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int)
                    = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                clear.setImageResource(if (position == 0) R.drawable.clear_done_animation
                else R.drawable.done_clear_animation)
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                    (clear.drawable as AnimatedVectorDrawableCompat).start()
                else (clear.drawable as AnimatedVectorDrawable).start()
            } else clear.setImageResource(if (position == 0) R.drawable.ic_done_all
            else R.drawable.ic_clear_all)
        })
        tasks_view_pager.adapter = TodoFragmentPagerAdapter(supportFragmentManager, this)
        tabs.setupWithViewPager(tasks_view_pager)
    }
}
