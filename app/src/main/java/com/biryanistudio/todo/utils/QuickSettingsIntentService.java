package com.biryanistudio.todo.utils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;

import com.biryanistudio.todo.ui.NewTaskDialogActivity;

/**
 * Created by Aakaash Jois on 01-11-2016 at 11:43 PM.
 */

@TargetApi( Build.VERSION_CODES.N )
public class QuickSettingsIntentService extends TileService {

    @Override
    public void onClick() {
        if ( !isLocked() )
            startActivityAndCollapse(new Intent(getApplicationContext(), NewTaskDialogActivity.class));
    }
}
