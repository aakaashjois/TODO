package com.biryanistudio.todo.Completed;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class CompletedPresenter {

    private CompletedFragment fragment;

    public CompletedPresenter(@NonNull CompletedFragment fragment) {
        this.fragment = fragment;
    }

    public void setRecyclerViewAdapter(@NonNull RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext());
        recyclerView.setLayoutManager(layoutManager);
        CompletedAdapter adapter = new CompletedAdapter(new ArrayList<String>());
        recyclerView.setAdapter(adapter);
    }
}
