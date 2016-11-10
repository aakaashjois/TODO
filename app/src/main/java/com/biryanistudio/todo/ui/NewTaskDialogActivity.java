package com.biryanistudio.todo.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
                final TextInputLayout dialogTaskInputLayout =
                        ( TextInputLayout ) dialog.findViewById(R.id.dialog_task_input_layout);
                final TextInputEditText dialogTaskInput =
                        ( TextInputEditText ) dialog.findViewById(R.id.dialog_task_input);
                dialogTaskInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if ( charSequence.length() > 140 ) {
                            dialogTaskInputLayout.setErrorEnabled(true);
                            dialogTaskInputLayout.setError("Exceeded limit");
                            dialogTaskInput.setPaintFlags(
                                    dialogTaskInput.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            dialogTaskInput.setAlpha(0.7f);
                        } else {
                            dialogTaskInputLayout.setError("");
                            dialogTaskInputLayout.setErrorEnabled(false);
                            dialogTaskInput.setPaintFlags(0);
                            dialogTaskInput.setAlpha(1f);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                dialogTaskInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if ( i == EditorInfo.IME_ACTION_DONE )
                            if ( dialogTaskInputLayout.isErrorEnabled() )
                                if ( textView.getText().toString().trim().equals("") )
                                    textView.setError("Enter a longer //TODO");
                                else
                                    textView.setError("Enter a shorter //TODO");
                            else {
                                DbTransactions.writeTask(NewTaskDialogActivity.this, textView.getText().toString().trim());
                                dialog.dismiss();
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
