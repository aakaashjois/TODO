package com.biryanistudio.todo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class PendingFragment extends BaseFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new FragmentPresenter(this, recyclerView, noTodosTextView);
        presenter.setTextViewText();
    }

    @Override
    public void clearAllTasks() {
        // TODO Fix clear all
        presenter.clearPendingTasks();
    }

    public void updateTasks() {
        presenter.setRecyclerViewAdapter();
        presenter.setTextViewText();
    }
}