package com.biryanistudio.todo.userinterface

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi

import com.biryanistudio.todo.database.DbTransactions

class ActionTextActivity : Activity() {

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
                .trim { it <= ' ' }
        DbTransactions.writeTask(this, text)
        UiUtils.createNotification(this, text)
        finish()
    }
}
