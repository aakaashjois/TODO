package com.biryanistudio.todo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.database.DbTransactions;
import com.biryanistudio.todo.database.TasksContract;
import com.biryanistudio.todo.fragments.FragmentPresenter;
import com.biryanistudio.todo.userinterface.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>
        implements CompoundButton.OnCheckedChangeListener {
    private final Context context;
    private final FragmentPresenter presenter;
    // ArrayList of pending and completed tasks; pending is an ArrayList that only contains status of tasks
    private final List<String> tasks = new ArrayList<>();
    private final List<String> pending = new ArrayList<>();
    private final List<String> timestamps = new ArrayList<>();

    public TasksAdapter(@NonNull final Context context, @NonNull final FragmentPresenter presenter, final Cursor cursor) {
        this.context = context;
        this.presenter = presenter;
        convertCursorToList(cursor);
    }

    @Override
    public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (!pending.isEmpty() && pending.indexOf(presenter.getPendingConditionBasedOnFragmentType()) == -1) {
            Log.i("TAG", presenter.getPendingConditionBasedOnFragmentType());
            // Check if local tasks, pending and timestamps ArrayLists are empty
            // If empty, call hideNoTodosTextView()
            // Perform check here because onBindViewHolder() is called after any notifyItem() calls
            presenter.hideNoTodosTextView();

            holder.task.setAlpha(1f);
            holder.time.setText(UiUtils.createTimeStamp(context, timestamps.get(position)));
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
        } else {
            presenter.showNoTodosTextView();
        }
    }

    @Override
    public int getItemCount() {
        Log.i("TAG", tasks.toString());
        return tasks.size();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String task = (String) buttonView.getTag();
        handleItemChecked(task);
    }

    private void convertCursorToList(final Cursor cursor) {
        // Extract data from Cursor into separate ArrayLists: tasks, pending, timestamps
        if (cursor != null && cursor.moveToFirst()) {
            do {
                tasks.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)));
                pending.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_PENDING)));
                timestamps.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP)));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void handleItemChecked(final String task) {
        // Update task as completed, or remove from database (if already completed)
        int pos = tasks.indexOf(task);
        String isPending = pending.get(pos);
        tasks.remove(pos);
        pending.remove(pos);
        timestamps.remove(pos);
        if (isPending.equalsIgnoreCase("yes")) {
            DbTransactions.updateTaskAsCompleted(context, task);
        } else {
            DbTransactions.updateTaskAsPending(context, task);
        }
        notifyItemRemoved(pos);
    }

    public void updateAllPendingTasksAsCompleted() {
        // Clear tasks, pending and timestamps ArrayLists; update all pending tasks in database as completed
        tasks.clear();
        pending.clear();
        timestamps.clear();
        final int updated = (int) DbTransactions.updateAllPendingTasksAsCompleted(context);
        notifyItemRangeRemoved(0, updated);
    }

    public void deleteAllCompletedTasks() {
        // Clear tasks, pending and timestamps ArrayLists; delete all completed tasks in database
        tasks.clear();
        pending.clear();
        timestamps.clear();
        final int updated = (int) DbTransactions.deleteAllCompletedTasks(context);
        notifyItemRangeRemoved(0, updated);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView task;
        final TextView time;
        final CheckBox checkBox;
        final ImageButton delete;

        ViewHolder(View itemView) {
            super(itemView);
            task = (TextView) itemView.findViewById(R.id.task);
            time = (TextView) itemView.findViewById(R.id.time);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_box);
            delete = (ImageButton) itemView.findViewById(R.id.delete);
        }
    }
}
