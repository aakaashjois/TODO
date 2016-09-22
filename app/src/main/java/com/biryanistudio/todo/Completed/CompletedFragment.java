package com.biryanistudio.todo.Completed;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.Ui.MainActivity;

public class CompletedFragment extends Fragment implements MainActivity.ITasksUpdated {

    private CompletedPresenter presenter;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new CompletedPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setRecyclerViewAdapter(recyclerView);
    }

    @Override
    public void tasksUpdated() {
        presenter.clearCompletedTasks();
    }

    public void updateRecyclerView() {
        presenter.setRecyclerViewAdapter(recyclerView);
    }
}
