package com.biryanistudio.todo.Pending;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biryanistudio.todo.Db.TasksContract;

/**
 * Created by Sravan on 19-Sep-16.
 */
public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {
    private Cursor cursor;

    public PendingAdapter(@NonNull Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public PendingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.textView.setText(cursor.getString(cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)));
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(TextView textView) {
            super(textView);
            this.textView = textView;
        }
    }
}