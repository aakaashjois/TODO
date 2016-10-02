package com.biryanistudio.todo.ui;

import android.database.Cursor;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.db.TasksContract;
import com.biryanistudio.todo.fragments.BaseFragment;
import com.biryanistudio.todo.fragments.CompletedFragment;
import com.biryanistudio.todo.fragments.PendingFragment;
import com.biryanistudio.todo.utils.DbTransactions;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private Fragment fragment;
    private List<String> tasks = new ArrayList<>();
    private List<String> pending = new ArrayList<>();
    private List<String> timestamps = new ArrayList<>();

    public TasksAdapter(@NonNull final Cursor cursor, @NonNull final Fragment fragment) {
        convertCursorToList(cursor, -1);
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
        holder.checkBox.setTag(tasks.get(position));
        holder.checkBox.setAlpha(1f);
        holder.task.setAlpha(1f);
        if (pending.get(position).equalsIgnoreCase("no")) {
            holder.checkBox.setChecked(true);
            holder.checkBox.setAlpha(0.7f);
            holder.task.setAlpha(0.7f);
            holder.task.setPaintFlags(holder.task.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        holder.checkBox.setOnCheckedChangeListener(this);
        holder.delete.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void swapCursor(@NonNull Cursor newCursor, final long updatedRows) {
        convertCursorToList(newCursor, updatedRows);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String task = (String) buttonView.getTag();
        handleItemChecked(task);
    }

    private void convertCursorToList(@NonNull final Cursor cursor, long updatedRows) {
        cursor.moveToFirst();
        do {
            tasks.add(cursor.getString(
                    cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)));
            pending.add(cursor.getString(
                    cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_PENDING)));
            timestamps.add(cursor.getString(
                    cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP)));
        } while (cursor.moveToNext());
        cursor.close();
        if (updatedRows != -1) notifyItemRangeRemoved(0, (int) updatedRows);
    }

    private void handleItemChecked(final String task) {
        int pos = tasks.indexOf(task);
        String isPending = pending.get(pos);
        tasks.remove(pos);
        pending.remove(pos);
        timestamps.remove(pos);
        if (isPending.equalsIgnoreCase("yes")) {
            DbTransactions.updateTaskAsCompleted(fragment.getContext(), task);
            ((PendingFragment) fragment).updateCompletedFragment();
        } else {
            DbTransactions.updateTaskAsPending(fragment.getContext(), task);
            ((CompletedFragment) fragment).updatePendingFragment();
        }
        notifyItemRemoved(pos);
        if (tasks.size() == 0)
            ((BaseFragment) fragment).updateRecyclerView();
    }

    @Override
    public void onClick(View view) {
        //TODO: Handle deleting of item.
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView task;
        CheckBox checkBox;
        ImageButton delete;

        ViewHolder(View itemView) {
            super(itemView);
            task = (TextView) itemView.findViewById(R.id.task);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);
            delete = ( ImageButton ) itemView.findViewById(R.id.delete);
        }
    }
}