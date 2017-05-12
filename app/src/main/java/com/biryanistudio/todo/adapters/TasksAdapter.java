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

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.database.DbTransactions;
import com.biryanistudio.todo.database.TasksContract;
import com.biryanistudio.todo.fragments.FragmentPresenter;
import com.biryanistudio.todo.userinterface.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>
        implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final Context context;
    private final FragmentPresenter presenter;

    /**
     * {@link ArrayList<String>} of all tasks.
     */
    private final List<String> tasks = new ArrayList<>();

    /**
     * {@link ArrayList<String>} of status of all tasks.
     */
    private final List<String> statuses = new ArrayList<>();

    /**
     * {@link ArrayList<String>} of timestamps of all tasks.
     */
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
        if (statuses.get(position).equalsIgnoreCase("no")) {
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

    /**
     * Moves all the tasks from Pending Fragment to Completed Fragment and marks them as completed.
     * <p>
     * Uses {@link TasksAdapter#clearAllLists()} to clear the {@link TasksAdapter#tasks},
     * {@link TasksAdapter#statuses} and {@link TasksAdapter#timestamps}.
     * <p>
     * Calls {@link DbTransactions#updateAllPendingTasksAsCompleted(Context)} to update the tasks in
     * the database.
     */
    public void updateAllPendingTasksAsCompleted() {
        clearAllLists();
        int updated = DbTransactions.updateAllPendingTasksAsCompleted(context);
        notifyItemRangeRemoved(0, updated);
        showEmptyViewIfNoTasksPresent();
    }

    /**
     * Deletes all the tasks from Completed Fragment.
     * <p>
     * Uses {@link TasksAdapter#clearAllLists()} to clear the {@link TasksAdapter#tasks},
     * {@link TasksAdapter#statuses} and {@link TasksAdapter#timestamps}.
     * <p>
     * Calls {@link DbTransactions#deleteAllCompletedTasks(Context)} to delete all the completed
     * tasks in the database.
     */
    public void deleteAllCompletedTasks() {
        clearAllLists();
        int updated = DbTransactions.deleteAllCompletedTasks(context);
        notifyItemRangeRemoved(0, updated);
        showEmptyViewIfNoTasksPresent();
    }

    /**
     * Wrapper method which calls {@link FragmentPresenter#handleEmptyView(boolean)} if no tasks are
     * present.
     */
    private void showEmptyViewIfNoTasksPresent() {
        presenter.handleEmptyView(statuses.size() == 0);
    }

    /**
     * Converts the cursor into {@link TasksAdapter#tasks}, {@link TasksAdapter#statuses} and
     * {@link TasksAdapter#timestamps}.
     * Calls {@link FragmentPresenter#handleEmptyView(boolean)} to display or hide the empty view
     * based on the Cursor.
     *
     * @param cursor Cursor passed to {@link TasksAdapter} when instantiated.
     */
    private void convertCursorToList(final Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            presenter.handleEmptyView(false);
            do {
                tasks.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)));
                statuses.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_PENDING)));
                timestamps.add(cursor.getString(
                        cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TIME_STAMP)));
            } while (cursor.moveToNext());
            cursor.close();
        } else
            presenter.handleEmptyView(true);
    }

    private void handleItemChecked(final String timestamp) {
        // Update task as completed, or remove from database (if already completed)
        int itemPosition = timestamps.indexOf(timestamp);
        String isPending = statuses.get(itemPosition);
        deleteItemInAllLists(itemPosition);
        int updateRowId = isPending.equalsIgnoreCase("yes") ?
                DbTransactions.updateTaskAsCompleted(context, timestamp) :
                DbTransactions.updateTaskAsPending(context, timestamp);
        if (updateRowId == 0)
            Log.e("Handle Item Check", "Error updating");
        notifyItemRemoved(itemPosition);
        showEmptyViewIfNoTasksPresent();
    }

    private void handleItemDeleted(final String timestamp) {
        // Delete task
        int itemPosition = timestamps.indexOf(timestamp);
        deleteItemInAllLists(itemPosition);
        int deletedRows = DbTransactions.deleteTask(context, timestamp);
        if (deletedRows == 0)
            Log.e("Handle Item Delete", "Error deleting");
        notifyItemRemoved(itemPosition);
        showEmptyViewIfNoTasksPresent();
    }

    /**
     * Clears {@link TasksAdapter#tasks}, {@link TasksAdapter#statuses} and
     * {@link TasksAdapter#timestamps}.
     */
    private void clearAllLists() {
        tasks.clear();
        statuses.clear();
        timestamps.clear();
    }

    /**
     * Remove a single item from {@link TasksAdapter#tasks}, {@link TasksAdapter#statuses} and
     * {@link TasksAdapter#timestamps}.
     *
     * @param itemPosition The index of the item to be removed.
     */
    private void deleteItemInAllLists(int itemPosition) {
        tasks.remove(itemPosition);
        statuses.remove(itemPosition);
        timestamps.remove(itemPosition);
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
