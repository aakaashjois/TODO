package com.biryanistudio.todo.fragments;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.adapters.TasksAdapter;
import com.biryanistudio.todo.database.DbTransactions;


public class FragmentPresenter {
    private static final String TAG = FragmentPresenter.class.getSimpleName();
    private final Fragment fragment;
    private TasksAdapter adapter;
    private final RecyclerView recyclerView;
    private final TextView noTodosTextView;

    FragmentPresenter(@NonNull Fragment fragment, @NonNull final RecyclerView recyclerView,
                      @NonNull final TextView noTodosTextView) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.noTodosTextView = noTodosTextView;
    }

    void setRecyclerViewAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        adapter = new TasksAdapter(fragment.getContext(), this, getAppropriateCursor());
        recyclerView.setAdapter(adapter);
    }

    public void showNoTodosTextView() {
        // Show TextView to display message to user, hide RecyclerView
        noTodosTextView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    public void hideNoTodosTextView() {
        // Hide TextView to display message to user, show RecyclerView
        noTodosTextView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    void setTextViewText() {
        // Set appropriate text for TextView depending on Fragment
        if (fragment instanceof PendingFragment) {
            noTodosTextView.setText(R.string.text_not_added_pending_yet);
        } else {
            noTodosTextView.setText(R.string.text_not_completed);
        }
    }

    private Cursor getAppropriateCursor() {
        if (fragment instanceof PendingFragment)
            return DbTransactions.readPendingTasks(fragment.getContext());
        if (fragment instanceof CompletedFragment)
            return DbTransactions.readCompletedTasks(fragment.getContext());
        return null;
    }

    void clearPendingTasks() {
        adapter.updateAllPendingTasksAsCompleted();
    }

    void clearCompletedTasks() {
        adapter.deleteAllCompletedTasks();

    }
}
