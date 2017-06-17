package com.biryanistudio.todo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class CompletedFragment extends BaseFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new FragmentPresenter(this, recyclerView, emptyTextView, emptyViewSwitcher);
        presenter.setTextViewText();
    }

    @Override
    public void clearAllTasks() {
        presenter.clearCompletedTasks();
    }

    @Override
    public void updateTasks() {
        presenter.setRecyclerViewAdapter();
        presenter.setEmptyTextView();
    }
}