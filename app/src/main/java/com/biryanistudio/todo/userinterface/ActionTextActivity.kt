package com.biryanistudio.todo.userinterface

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.biryanistudio.todo.TodoApplication

import com.biryanistudio.todo.database.TodoItem
import io.realm.Realm
import java.util.*
import kotlin.concurrent.thread

class ActionTextActivity : Activity() {

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
                .trim { it <= ' ' }
        thread {
            Realm.getDefaultInstance().use {
                it.executeTransaction {
                    it.insertOrUpdate(TodoItem().apply {
                        id = UUID.randomUUID().toString()
                        completed = 0
                        task = text
                        timestamp = System.currentTimeMillis()
                    })
                }
            }
        }
        TodoApplication.createNotification(this, text)
        finish()
    }
}
