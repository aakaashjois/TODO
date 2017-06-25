package com.biryanistudio.todo.userinterface

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.biryanistudio.todo.TodoApplication

import com.biryanistudio.todo.database.TodoItem
import com.vicpin.krealmextensions.save
import kotlin.concurrent.thread

class ActionTextActivity : Activity() {

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
                .trim { it <= ' ' }
        thread {
            TodoItem().apply {
                completed = 0
                task = text
                timestamp = System.currentTimeMillis()
            }.save()
        }
        TodoApplication.createNotification(this, text)
        finish()
    }
}
