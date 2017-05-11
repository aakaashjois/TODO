package com.biryanistudio.todo.fragments;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.adapters.TasksAdapter;
import com.biryanistudio.todo.database.DbTransactions;


public class FragmentPresenter {
    private final Fragment fragment;
    private TasksAdapter adapter;
    private final RecyclerView recyclerView;
    private final TextView noTodosTextView;
    private final ViewSwitcher emptyViewSwitcher;

    FragmentPresenter(@NonNull Fragment fragment,
                      @NonNull final RecyclerView recyclerView,
                      @NonNull final TextView noTodosTextView,
                      @NonNull final ViewSwitcher emptyViewSwitcher) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.noTodosTextView = noTodosTextView;
        this.emptyViewSwitcher = emptyViewSwitcher;
    }

    public String getPendingConditionBasedOnFragmentType() {
        // Check whether current fragment is PendingFragment
        //
        return fragment instanceof PendingFragment ? "yes" : "no";
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

    void setTextViewText() {
        // Set appropriate text for TextView depending on Fragment
        noTodosTextView.setText(fragment instanceof PendingFragment ?
                R.string.text_not_added_pending_yet : R.string.text_not_completed);
    }

    public void showEmptyView() {
        if (emptyViewSwitcher.getNextView().getId() == R.id.empty_view)
            emptyViewSwitcher.showNext();
    }

    public void hideEmptyView() {
        if (emptyViewSwitcher.getNextView().getId() == R.id.recycler_view)
            emptyViewSwitcher.showNext();
    }

    private Cursor getAppropriateCursor() {
        return fragment instanceof PendingFragment ?
                DbTransactions.readPendingTasks(fragment.getContext()) :
                fragment instanceof CompletedFragment ?
                        DbTransactions.readCompletedTasks(fragment.getContext()) :
                        null;
    }

    void clearPendingTasks() {
        adapter.updateAllPendingTasksAsCompleted();
    }

    void clearCompletedTasks() {
        adapter.deleteAllCompletedTasks();

    }
}
