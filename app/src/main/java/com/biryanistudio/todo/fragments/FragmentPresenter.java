package com.biryanistudio.todo.fragments;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.adapters.TasksAdapter;
import com.biryanistudio.todo.database.DbTransactions;


public class FragmentPresenter {
    private TasksAdapter adapter;
    private final Fragment fragment;
    private final RecyclerView recyclerView;
    private final TextView emptyTextView;
    private final ViewSwitcher emptyViewSwitcher;

    FragmentPresenter(@NonNull Fragment fragment,
                      @NonNull final RecyclerView recyclerView,
                      @NonNull final TextView emptyTextView,
                      @NonNull final ViewSwitcher emptyViewSwitcher) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.emptyTextView = emptyTextView;
        this.emptyViewSwitcher = emptyViewSwitcher;
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
        emptyTextView.setText(fragment instanceof PendingFragment ?
                R.string.text_not_added_pending_yet : R.string.text_not_completed);
    }

    void clearPendingTasks() {
        adapter.updateAllPendingTasksAsCompleted();
    }

    void clearCompletedTasks() {
        adapter.deleteAllCompletedTasks();

    }

    public void handleEmptyView(boolean showEmptyView) {
        if (showEmptyView) {
            if (emptyViewSwitcher.getNextView().getId() == R.id.empty_view)
                emptyViewSwitcher.showNext();
        } else {
            if (emptyViewSwitcher.getNextView().getId() == R.id.recycler_view)
                emptyViewSwitcher.showNext();
        }
    }

    public View getCoordinatorLayout() {
        return fragment.getActivity().findViewById(R.id.activity_list);
    }

    private Cursor getAppropriateCursor() {
        return fragment instanceof PendingFragment ?
                DbTransactions.readPendingTasks(fragment.getContext()) :
                DbTransactions.readCompletedTasks(fragment.getContext());
    }
}
