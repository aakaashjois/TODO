package com.biryanistudio.todo.Pending;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.biryanistudio.todo.Ui.TasksAdapter;
import com.biryanistudio.todo.Utils.DbTransactions;

/**
 * Created by Sravan on 19-Sep-16.
 */
public class PendingPresenter {
    private static final String TAG = PendingPresenter.class.getSimpleName();
    private PendingFragment fragment;
    private TasksAdapter tasksAdapter;

    public PendingPresenter(@NonNull PendingFragment fragment) {
        this.fragment = fragment;
    }

    public void setRecyclerViewAdapter(@NonNull RecyclerView recyclerView) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext());
        recyclerView.setLayoutManager(layoutManager);
        final Cursor cursor = DbTransactions.readPendingTasks(fragment.getContext());
        tasksAdapter = new TasksAdapter(cursor);
        recyclerView.setAdapter(tasksAdapter);
    }

    public void clearPendingTasks() {
        DbTransactions.updatePendingTasksAsCompleted(fragment.getContext());
        final Cursor cursor = DbTransactions.readPendingTasks(fragment.getContext());
        tasksAdapter.swapCursor(cursor);
    }
}
