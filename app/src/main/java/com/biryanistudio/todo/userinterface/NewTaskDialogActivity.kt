package com.biryanistudio.todo.userinterface

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.EditorInfo

import com.biryanistudio.todo.R
import com.biryanistudio.todo.database.DbTransactions

import kotlinx.android.synthetic.main.activity_new_task_dialog.*

class NewTaskDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialog = Dialog(this, R.style.NewTaskDialog)
        dialog.setContentView(R.layout.activity_new_task_dialog)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setOnCancelListener { finish() }
        dialog.setOnShowListener {
            dialog_task_input.requestFocus()
            dialog_task_input.setOnEditorActionListener { textView, i, _ ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    val text = textView.text.toString().trim { it <= ' ' }
                    DbTransactions.writeTask(this, text)
                    dialog.dismiss()
                    UiUtils.createNotification(this, text)
                    finish()
                }
                true
            }
        }
        dialog.show()
    }
}
