package com.biryanistudio.todo.services

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService

import com.biryanistudio.todo.userinterface.NewTaskDialogActivity

/**
 * Created by Aakaash Jois.
 * 01-11-2016 - 11:43 PM.
 */

@TargetApi(Build.VERSION_CODES.N)
class QuickSettingsInputIntentService : TileService() {

    override fun onClick() {
        if (isLocked) unlockAndRun {
            startActivityAndCollapse(Intent(applicationContext, NewTaskDialogActivity::class.java))
        }
        else startActivityAndCollapse(Intent(applicationContext, NewTaskDialogActivity::class.java))
    }
}