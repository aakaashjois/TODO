package com.biryanistudio.todo.Ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.biryanistudio.todo.Db.TasksContract;
import com.biryanistudio.todo.Fragments.BaseFragment;
import com.biryanistudio.todo.Fragments.PendingFragment;
import com.biryanistudio.todo.R;
import com.biryanistudio.todo.Utils.DbTransactions;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>
        implements CompoundButton.OnCheckedChangeListener {
    private Fragment fragment;
    private List<String> tasks = new ArrayList<>();
    private List<String> pending = new ArrayList<>();
    private List<String> timestamps = new ArrayList<>();

    public TasksAdapter(@NonNull final Cursor cursor, @NonNull final Fragment fragment) {
        convertCursorToList(cursor);
        this.fragment = fragment;
    }

    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.task.setText(tasks.get(position));
        holder.checkBox.setTag(position);
        holder.checkBox.setOnCheckedChangeListener(this);
        //TODO: Add animations for recyclerview
        //TODO: If user checks last item, animation is cut short by textview being set to visible
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void swapCursor(@NonNull Cursor newCursor) {
        convertCursorToList(newCursor);
        notifyDataSetChanged();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = (int) buttonView.getTag();
        handleItemChecked(pos);
    }

    private void convertCursorToList(@NonNull final Cursor cursor) {
        cursor.moveToFirst();
        do {
            Log.i(getClass().getSimpleName(),
                    cursor.getString(cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)));
            tasks.add(cursor.getString(
                    cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)));
            pending.add(cursor.getString(
                    cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_PENDING)));
            timestamps.add(cursor.getString(
                    cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP)));
        } while (cursor.moveToNext());
        cursor.close();
    }

    private void handleItemChecked(final int pos) {
        String isPending = pending.get(pos);
        String task = tasks.get(pos);
        tasks.remove(pos);
        pending.remove(pos);
        timestamps.remove(pos);
        notifyItemChanged(pos);
        if (isPending.equalsIgnoreCase("yes")) {
            DbTransactions.updatePendingTaskAsCompleted(fragment.getContext(), task);
            ((PendingFragment) fragment).updateCompletedFragment();
        } else {
            DbTransactions.deleteCompletedTask(fragment.getContext(), task);
        }
        if(tasks.size() == 0)
            ((BaseFragment) fragment).updateRecyclerView();
        }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView task;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            task = (TextView) itemView.findViewById(R.id.task);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);
        }
    }
}