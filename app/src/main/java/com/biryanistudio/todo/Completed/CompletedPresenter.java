package com.biryanistudio.todo.Completed;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.biryanistudio.todo.Ui.TasksAdapter;
import com.biryanistudio.todo.Utils.DbTransactions;

public class CompletedPresenter {
    private static final String TAG = CompletedPresenter.class.getSimpleName();
    private CompletedFragment fragment;
    private TasksAdapter tasksAdapter;

    public CompletedPresenter(@NonNull CompletedFragment fragment) {
        this.fragment = fragment;
    }

    public void setRecyclerViewAdapter(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext());
        recyclerView.setLayoutManager(layoutManager);
        final Cursor cursor = DbTransactions.readCompletedTasks(fragment.getContext());
        tasksAdapter = new TasksAdapter(cursor);
        recyclerView.setAdapter(tasksAdapter);
    }

    public void clearCompletedTasks() {
        DbTransactions.deleteCompletedTasks(fragment.getContext());
        final Cursor cursor = DbTransactions.readCompletedTasks(fragment.getContext());
        tasksAdapter.swapCursor(cursor);
    }
}
