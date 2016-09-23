package com.biryanistudio.todo.Fragments;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.biryanistudio.todo.Ui.TasksAdapter;
import com.biryanistudio.todo.Utils.DbTransactions;

/**
 * Created by Aakaash Jois on 23-09-2016 at 12:57 AM.
 */

class FragmentPresenter {
	private static final String TAG = FragmentPresenter.class.getSimpleName();
	private Fragment fragment;
	private TasksAdapter adapter;

	FragmentPresenter(@NonNull Fragment fragment) {
		this.fragment = fragment;
	}

	void setRecyclerViewAdapter(@NonNull RecyclerView recyclerView) {
		LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext());
		recyclerView.setLayoutManager(layoutManager);
		final Cursor cursor = getCursor();
		if ( cursor != null )
			adapter = new TasksAdapter(cursor);
		recyclerView.setAdapter(adapter);
	}

	void clearPendingTasks() {
		DbTransactions.updatePendingTasksAsCompleted(fragment.getContext());
		final Cursor cursor = getCursor();
		if ( cursor != null ) {
			adapter.swapCursor(cursor);
		}
	}

	void clearCompletedTasks() {
		DbTransactions.deleteCompletedTasks(fragment.getContext());
		final Cursor cursor = getCursor();
		if ( cursor != null ) {
			adapter.swapCursor(cursor);
		}
	}

	private Cursor getCursor() {
		if ( fragment instanceof PendingFragment )
			return DbTransactions.readPendingTasks(fragment.getContext());
		if ( fragment instanceof CompletedFragment )
			return DbTransactions.readCompletedTasks(fragment.getContext());
		return null;
	}

	//TODO: Change icon of FAB when tab changes
}
