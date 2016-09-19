package com.biryanistudio.todo.Pending;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.biryanistudio.todo.Db.TasksContract;
import com.biryanistudio.todo.Db.TasksDbHelper;

/**
 * Created by Sravan on 19-Sep-16.
 */
public class PendingPresenter {

    private PendingFragment fragment;
    private Cursor cursor;

    public PendingPresenter(@NonNull PendingFragment fragment) {
        this.fragment = fragment;
        TasksDbHelper dbHelper = new TasksDbHelper(fragment.getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {TasksContract.TaskEntry._ID, TasksContract.TaskEntry.COLUMN_NAME_TASK};
        String sortOrder = TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP + " DESC";
        cursor = db.query(
                TasksContract.TaskEntry.TABLE_NAME, projection, null, null, null, null, sortOrder);
    }

    public void setRecyclerViewAdapter(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext());
        recyclerView.setLayoutManager(layoutManager);
        PendingAdapter adapter = new PendingAdapter(cursor);
        recyclerView.setAdapter(adapter);
    }
}
