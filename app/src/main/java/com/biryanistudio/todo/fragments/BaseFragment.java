package com.biryanistudio.todo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.interfaces.ITasksUpdated;


public abstract class BaseFragment extends Fragment implements ITasksUpdated {

    FragmentPresenter presenter;
    RecyclerView recyclerView;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        textView = (TextView) view.findViewById(R.id.empty_view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setRecyclerViewAdapter();
        presenter.setTextViewText();
    }

    @Override
    public abstract void clearAllTasks();

    public void updateRecyclerView() {
        presenter.setRecyclerViewAdapter();
    }
}