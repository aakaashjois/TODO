package com.biryanistudio.todo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.biryanistudio.todo.db.DbTransactions;

public class ActionTextActivity extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString().trim();
        DbTransactions.writeTask(this, text);
        UiUtils.createNotification(this, text);
        finish();
    }
}
