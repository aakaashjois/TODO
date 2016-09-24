package com.biryanistudio.todo.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class CompletedFragment extends BaseFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new FragmentPresenter(this, recyclerView, textView);
    }

    @Override
    public void clearAllTasks() {
        presenter.clearCompletedTasks();
    }

    public void updateTasks() {
        presenter.setRecyclerViewAdapter();
        presenter.setTextViewText();
    }
}