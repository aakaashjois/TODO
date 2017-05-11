package com.biryanistudio.todo.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.database.DbTransactions;
import com.biryanistudio.todo.database.TasksContract;
import com.biryanistudio.todo.fragments.FragmentPresenter;
import com.biryanistudio.todo.userinterface.MainActivity;
import com.biryanistudio.todo.userinterface.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private final Context context;
    private final FragmentPresenter presenter;
    // ArrayList of pending and completed tasks; pending is an ArrayList that only contains status of tasks
    private final List<String> tasks = new ArrayList<>();
    private final List<String> pending = new ArrayList<>();
    private final List<String> timestamps = new ArrayList<>();

    public TasksAdapter(@NonNull final Context context,
                        @NonNull final FragmentPresenter presenter,
                        final Cursor cursor) {
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
        holder.task.setAlpha(1f);
        holder.time.setText(UiUtils.createTimeStamp(context, timestamps.get(position)));
        holder.task.setText(tasks.get(position));
        holder.checkBox.setTag(timestamps.get(position));
        holder.delete.setTag(timestamps.get(position));
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String timestamp = (String) buttonView.getTag();
        handleItemChecked(timestamp);
    }

    @Override
    public void onClick(View v) {
        final String timestamp = (String) v.getTag();
        final Snackbar snackbar = UiUtils.createSnackBar(context, presenter.getCoordinatorLayout(),
                context.getString(R.string.delete_current_todo), Snackbar.LENGTH_SHORT);
        snackbar.setAction(context.getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                handleItemDeleted(timestamp);
            }
        });
        snackbar.show();
    }

    private void showEmptyViewIfNoTasksPresent() {
        // Show empty view if pending is empty *and* there are no pending items
        if (pending.size() == 0)
            presenter.showEmptyView();
        else
            presenter.hideEmptyView();
    }

    private void convertCursorToList(final Cursor cursor) {
        // Extract data from Cursor into separate ArrayLists: tasks, pending, timestamps
        if (cursor != null && cursor.moveToFirst()) {
            presenter.hideEmptyView();
            do {
                tasks.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)));
                pending.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_PENDING)));
                timestamps.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP)));
            } while (cursor.moveToNext());
            cursor.close();
        } else
            presenter.showEmptyView();
    }

    private void handleItemChecked(final String timestamp) {
        // Update task as completed, or remove from database (if already completed)
        int pos = timestamps.indexOf(timestamp);
        String isPending = pending.get(pos);
        tasks.remove(pos);
        pending.remove(pos);
        timestamps.remove(pos);
        if (isPending.equalsIgnoreCase("yes"))
            DbTransactions.updateTaskAsCompleted(context, timestamp);
        else
            DbTransactions.updateTaskAsPending(context, timestamp);
        notifyItemRemoved(pos);
        showEmptyViewIfNoTasksPresent();
    }

    private void handleItemDeleted(final String timestamp) {
        // Delete task
        int pos = timestamps.indexOf(timestamp);
        tasks.remove(pos);
        pending.remove(pos);
        timestamps.remove(pos);
        DbTransactions.deleteTask(context, timestamp);
        notifyItemRemoved(pos);
        showEmptyViewIfNoTasksPresent();
    }

    public void updateAllPendingTasksAsCompleted() {
        // Clear tasks, pending and timestamps ArrayLists; update all pending tasks in database as completed
        tasks.clear();
        pending.clear();
        timestamps.clear();
        final int updated = (int) DbTransactions.updateAllPendingTasksAsCompleted(context);
        notifyItemRangeRemoved(0, updated);
        showEmptyViewIfNoTasksPresent();
    }

    public void deleteAllCompletedTasks() {
        // Clear tasks, pending and timestamps ArrayLists; delete all completed tasks in database
        tasks.clear();
        pending.clear();
        timestamps.clear();
        final int updated = (int) DbTransactions.deleteAllCompletedTasks(context);
        notifyItemRangeRemoved(0, updated);
        showEmptyViewIfNoTasksPresent();
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
