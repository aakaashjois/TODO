package com.biryanistudio.todo.Ui;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.biryanistudio.todo.Db.TasksContract;
import com.biryanistudio.todo.R;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> {
	private Cursor cursor;

	public TasksAdapter(@NonNull Cursor cursor) {
		this.cursor = cursor;
	}

	@Override
	public TasksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new ViewHolder(LayoutInflater.from(parent.getContext())
				.inflate(R.layout.list_item, parent, false));
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		cursor.moveToPosition(position);
		holder.task.setText(cursor.getString(
				cursor.getColumnIndex(TasksContract.TaskEntry.COLUMN_NAME_TASK)));
		//TODO: Set listeners for checkbox

		//TODO: Add animations for recyclerview
	}

	@Override
	public int getItemCount() {
		return cursor.getCount();
	}

	public void swapCursor(@NonNull Cursor newCursor) {
		this.cursor = newCursor;
		notifyDataSetChanged();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		TextView task;
		CheckBox checkBox;

		ViewHolder(View itemView) {
			super(itemView);
			task = ( TextView ) itemView.findViewById(R.id.task);
			checkBox = ( CheckBox ) itemView.findViewById(R.id.check_box);
		}
	}
}