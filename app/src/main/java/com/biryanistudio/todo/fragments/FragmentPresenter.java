package com.biryanistudio.todo.fragments;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.db.DbTransactions;
import com.biryanistudio.todo.ui.TasksAdapter;


class FragmentPresenter {
    private static final String TAG = FragmentPresenter.class.getSimpleName();
    private Fragment fragment;
    private TasksAdapter adapter;
    private RecyclerView recyclerView;
    private TextView textView;

    FragmentPresenter(@NonNull Fragment fragment, @NonNull final RecyclerView recyclerView,
                      @NonNull final TextView textView) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.textView = textView;
    }

    void setRecyclerViewAdapter() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        };
        recyclerView.setLayoutManager(layoutManager);
        final Cursor cursor = getAppropriateCursor();
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                setRecyclerViewVisibility(View.VISIBLE);
                setTextViewVisibiltiy(View.GONE);
                adapter = new TasksAdapter(cursor, fragment);
            } else {
                setRecyclerViewVisibility(View.GONE);
                setTextViewVisibiltiy(View.VISIBLE);
            }
        }
        recyclerView.setAdapter(adapter);
    }

    void setTextViewText() {
        if (fragment instanceof PendingFragment) {
            textView.setText(R.string.text_not_added_pending_yet);
        } else {
            textView.setText(R.string.text_not_completed);
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
        long updatedRows = DbTransactions.updateAllPendingTasksAsCompleted(fragment.getContext());
        swapCursorOnAdapter(getAppropriateCursor(), updatedRows);
    }

    void clearCompletedTasks() {
        long updatedRows = DbTransactions.deleteAllCompletedTasks(fragment.getContext());
        swapCursorOnAdapter(getAppropriateCursor(), updatedRows);

    }

    private void swapCursorOnAdapter(final Cursor cursor, final long updatedRows) {
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                adapter.swapCursor(cursor, updatedRows);
            } else {
                setTextViewText();
            }
        }
    }

    private void setRecyclerViewVisibility(int visibility) {
        recyclerView.setVisibility(visibility);
    }

    private void setTextViewVisibiltiy(int visibility) {
        textView.setVisibility(visibility);
    }
}
