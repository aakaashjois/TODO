package com.biryanistudio.todo.userinterface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.biryanistudio.todo.R;

public class TodoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
    }
}