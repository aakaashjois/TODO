package com.biryanistudio.todo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.interfaces.ITasksUpdated;


public abstract class BaseFragment extends Fragment implements ITasksUpdated {
    FragmentPresenter presenter;
    RecyclerView recyclerView;
    TextView noTodosTextView;
    ViewSwitcher emptyViewSwitcher;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        noTodosTextView = (TextView) view.findViewById(R.id.empty_view);
        emptyViewSwitcher = (ViewSwitcher) view.findViewById(R.id.empty_view_switcher);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setRecyclerViewAdapter();
    }

    @Override
    public abstract void clearAllTasks();

    @Override
    public abstract void updateTasks();
}