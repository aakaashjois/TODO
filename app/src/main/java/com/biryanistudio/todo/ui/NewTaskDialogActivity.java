package com.biryanistudio.todo.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.biryanistudio.todo.R;
import com.biryanistudio.todo.db.DbTransactions;

public class NewTaskDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Dialog dialog = new Dialog(this, R.style.NewTaskDialog);
        dialog.setContentView(R.layout.activity_new_task_dialog);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                final TextInputEditText dialogTaskInput =
                        (TextInputEditText) dialog.findViewById(R.id.dialog_task_input);
                dialogTaskInput.requestFocus();
                dialogTaskInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if (i == EditorInfo.IME_ACTION_DONE) {
                            String text = textView.getText().toString().trim();
                            DbTransactions.writeTask(NewTaskDialogActivity.this, text);
                            dialog.dismiss();
                            NotificationUtils.createNotification(NewTaskDialogActivity.this, text);
                            finish();
                        }
                        return true;
                    }
                });
            }
        });
        dialog.show();
    }
}
